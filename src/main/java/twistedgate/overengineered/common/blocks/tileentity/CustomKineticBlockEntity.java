package twistedgate.overengineered.common.blocks.tileentity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.BlockstateProvider;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGeneralMultiblock;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IRedstoneOutput;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Multiblock capable {@link KineticBlockEntity}<br>
 * <br>
 * <code>A lot of this is copy-pasted from {@link IEBaseBlockEntity}</code>
 * 
 * @author TwistedGate
 */
public abstract class CustomKineticBlockEntity extends GeneratingKineticBlockEntity implements BlockstateProvider{
		
	public CustomKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state){
		super(typeIn, pos, state);
	}
	
	@Override
	protected void read(CompoundTag compound, boolean clientPacket){
		readCustom(compound, clientPacket);
		super.read(compound, clientPacket);
	}
	
	@Override
	protected void write(CompoundTag compound, boolean clientPacket){
		writeCustom(compound, clientPacket);
		super.write(compound, clientPacket);
	}
	
	public abstract void readCustom(CompoundTag tag, boolean clientPacket);
	public abstract void writeCustom(CompoundTag tag, boolean clientPacket);
	
	// ------------------------------------------------------------
	// Everything copied from IEBaseBlockEntity is below this point
	// ------------------------------------------------------------
	
	/**
	 * Set by and for those instances of IGeneralMultiblock that need to drop their inventory
	 */
	protected IGeneralMultiblock tempMasterBE;
	@Nullable
	private BlockState overrideBlockState = null;
	private final EnumMap<Direction, Integer> redstoneBySide = new EnumMap<>(Direction.class);
	// ^ These would normaly be above the Constructor, but i've put them down here instead.
	
	@Override
	public void setState(BlockState state){
		if(getLevelNonnull().getBlockState(this.worldPosition) == getState())
			getLevelNonnull().setBlockAndUpdate(this.worldPosition, state);
	}
	
	@Override
	public BlockState getState(){
		return getBlockState();
	}
	
	public void setOverrideState(@Nullable BlockState state){
		this.overrideBlockState = state;
	}
	
	@Override
	public BlockState getBlockState(){
		if(this.overrideBlockState != null)
			return this.overrideBlockState;
		else
			return super.getBlockState();
	}
	
	/**
	 * Most calls to {@link BlockEntity#setChanged} should be replaced by this. The vanilla mD also updates comparator states and re-caches the block
	 * state, while in most cases we just want to say "this needs to be saved to disk"
	 */
	@SuppressWarnings("deprecation")
	protected void markChunkDirty(){
		if(this.level != null && this.level.hasChunkAt(this.worldPosition))
			this.level.getChunkAt(this.worldPosition).setUnsaved(true);
	}
	
	public void markContainingBlockForUpdate(@Nullable BlockState newState){
		if(this.level != null)
			markBlockForUpdate(getBlockPos(), newState);
	}
	
	public void markBlockForUpdate(BlockPos pos, @Nullable BlockState newState){
		BlockState state = this.level.getBlockState(pos);
		if(newState == null)
			newState = state;
		this.level.sendBlockUpdated(pos, state, newState, 3);
		this.level.updateNeighborsAt(pos, newState.getBlock());
	}
	
	@Nonnull
	public Level getLevelNonnull(){
		return Objects.requireNonNull(super.getLevel());
	}
	
	protected boolean isRSPowered(){
		for(Direction d:DirectionUtils.VALUES)
			if(getRSInput(d) > 0)
				return true;
		return false;
	}
	
	protected int getRSInput(Direction from){
		if(this.level.isClientSide || !this.redstoneBySide.containsKey(from))
			updateRSForSide(from);
		return this.redstoneBySide.get(from);
	}
	
	private void updateRSForSide(Direction side){
		int rsStrength = getLevelNonnull().getSignal(this.worldPosition.relative(side), side);
		if(rsStrength == 0 && this instanceof IRedstoneOutput redOut && redOut.canConnectRedstone(side)){
			BlockState state = SafeChunkUtils.getBlockState(this.level, this.worldPosition.relative(side));
			if(state.getBlock() == Blocks.REDSTONE_WIRE && state.getValue(RedStoneWireBlock.POWER) > rsStrength)
				rsStrength = state.getValue(RedStoneWireBlock.POWER);
		}
		this.redstoneBySide.put(side, rsStrength);
	}
	
	public void onNeighborBlockChange(BlockPos otherPos){
		BlockPos delta = otherPos.subtract(worldPosition);
		Direction side = Direction.getNearest(delta.getX(), delta.getY(), delta.getZ());
		Preconditions.checkNotNull(side);
		updateRSForSide(side);
	}
	
	@Override
	public void setLevel(Level world){
		super.setLevel(world);
		this.redstoneBySide.clear();
	}
	
	// Based on the super version, but works around a Forge patch to World#markChunkDirty causing duplicate comparator
	// updates and only performs comparator updates if this TE actually has comparator behavior
	@Override
	public void setChanged(){
		if(this.level != null){
			markChunkDirty();
			BlockState state = getBlockState();
			if(state.hasAnalogOutputSignal())
				this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
		}
	}
	
	@Override
	public boolean triggerEvent(int id, int type){
		if(id == 0 || id == 255){
			markContainingBlockForUpdate(null);
			return true;
		}else if(id == 254){
			BlockState state = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
			return true;
		}
		return super.triggerEvent(id, type);
	}
	
	private final List<ResettableCapability<?>> caps = new ArrayList<>();
	private final List<Runnable> onCapInvalidate = new ArrayList<>();
	
	protected <T> ResettableCapability<T> registerCapability(T val){
		ResettableCapability<T> cap = new ResettableCapability<>(val);
		this.caps.add(cap);
		return cap;
	}
	
	public void addCapInvalidateHook(Runnable hook){
		this.onCapInvalidate.add(hook);
	}
	
	protected ResettableCapability<IEnergyStorage> registerEnergyInput(IEnergyStorage directStorage){
		return registerCapability(new WrappingEnergyStorage(directStorage, true, false, this::setChanged));
	}
	
	protected ResettableCapability<IEnergyStorage> registerEnergyOutput(IEnergyStorage directStorage){
		return registerCapability(new WrappingEnergyStorage(directStorage, false, true, this::setChanged));
	}
	
	@Override
	public void invalidateCaps(){
		super.invalidateCaps();
		resetAllCaps();
		this.caps.clear();
		this.onCapInvalidate.forEach(Runnable::run);
		this.onCapInvalidate.clear();
	}
	
	protected void resetAllCaps(){
		this.caps.forEach(ResettableCapability::reset);
	}
	
	private boolean isUnloaded = false;
	
	@Override
	public void onLoad(){
		super.onLoad();
		this.isUnloaded = false;
	}
	
	@Override
	public void onChunkUnloaded(){
		super.onChunkUnloaded();
		this.isUnloaded = true;
	}
	
	@Override
	public final void remove(){
		if(!this.isUnloaded)
			setRemovedOE();
		super.remove();
	}
	
	public void setRemovedOE(){
	}
}
