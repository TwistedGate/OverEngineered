package twistedgate.overengineered.common.blocks.tileentity;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IComparatorOverride;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGeneralMultiblock;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IMirrorAble;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IReadOnPlacement;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IScrewdriverInteraction;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IStateBasedDirectional;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerControlState;
import blusunrize.immersiveengineering.common.util.compat.computers.generic.ComputerControllable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Lazy;

/**
 * This a lot of things copy-pasted from {@link MultiblockPartBlockEntity}, im pretty sure just the important bits.
 * 
 * @author TwistedGate
 */
public abstract class KineticMultiblockPartTileEntity<T extends KineticMultiblockPartTileEntity<T>> extends CustomKineticBlockEntity implements IStateBasedDirectional, IGeneralMultiblock, IScrewdriverInteraction, IMirrorAble, IModelOffsetProvider, ComputerControllable{
	
	public boolean formed = false;
	/** Position of this block according to the BlockInfo's returned by IMultiblock#getStructure */
	public BlockPos posInMultiblock = BlockPos.ZERO;
	/** Offset from the master to this block (world coordinate system) */
	public BlockPos offsetToMaster = BlockPos.ZERO;
	protected final IETemplateMultiblock multiblockInstance;
	/**
	 * stores the world time at which this block can only be disassembled by breaking the block associated with this TE. This prevents half/duplicate
	 * disassembly when working with the drill or TCon hammers
	 */
	public long onlyLocalDissassembly = -1;
	protected final Lazy<Vec3i> structureDimensions;
	protected final boolean hasRedstoneControl;
	protected boolean redstoneControlInverted = false;
	public final ComputerControlState computerControl = new ComputerControlState();
	
	public KineticMultiblockPartTileEntity(IETemplateMultiblock multiblockInstance, BlockEntityType<?> typeIn, boolean hasRSControl, BlockPos pos, BlockState state){
		super(typeIn, pos, state);
		this.multiblockInstance = multiblockInstance;
		this.structureDimensions = Lazy.of(() -> multiblockInstance.getSize(level));
		this.hasRedstoneControl = hasRSControl;
	}
	
	@Override
	public Direction getFacing(){
		return IStateBasedDirectional.super.getFacing();
	}
	
	@Override
	public Property<Direction> getFacingProperty(){
		return IEProperties.FACING_HORIZONTAL;
	}
	
	@Override
	public PlacementLimitation getFacingLimitation(){
		return PlacementLimitation.HORIZONTAL;
	}
	
	@Override
	public boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity){
		return false;
	}
	
	@Override
	public void readCustom(CompoundTag tag, boolean clientPacket){
		this.formed = tag.getBoolean("formed");
		this.posInMultiblock = NbtUtils.readBlockPos(tag.getCompound("posInMultiblock"));
		this.offsetToMaster = NbtUtils.readBlockPos(tag.getCompound("offset"));
		this.redstoneControlInverted = tag.getBoolean("redstoneControlInverted");
	}
	
	@Override
	public void writeCustom(CompoundTag tag, boolean clientPacket){
		tag.putBoolean("formed", this.formed);
		tag.put("posInMultiblock", NbtUtils.writeBlockPos(new BlockPos(this.posInMultiblock)));
		tag.put("offset", NbtUtils.writeBlockPos(new BlockPos(this.offsetToMaster)));
		tag.putBoolean("redstoneControlInverted", this.redstoneControlInverted);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T master(){
		if(this.offsetToMaster.equals(Vec3i.ZERO))
			return (T) this;
		// Used to provide tile-dependant drops after disassembly
		if(this.tempMasterBE != null)
			return (T) this.tempMasterBE;
		return getEntityForPos(this.multiblockInstance.getMasterFromOriginOffset());
	}
	
	public void updateMasterBlock(BlockState state, boolean blockUpdate){
		T master = master();
		if(master != null){
			master.markChunkDirty();
			if(blockUpdate)
				master.markContainingBlockForUpdate(state);
		}
	}
	
	@Override
	public boolean isDummy(){
		return !this.offsetToMaster.equals(Vec3i.ZERO);
	}
	
	public BlockState getOriginalBlock(){
		for(StructureBlockInfo block:this.multiblockInstance.getStructure(this.level))
			if(block.pos.equals(this.posInMultiblock))
				return block.state;
		return Blocks.AIR.defaultBlockState();
	}
	
	public void disassemble(){
		if(this.formed && !this.level.isClientSide){
			this.tempMasterBE = master();
			BlockPos startPos = getOrigin();
			this.multiblockInstance.disassemble(this.level, startPos, getIsMirrored(), this.multiblockInstance.untransformDirection(getFacing()));
			this.level.removeBlock(this.worldPosition, false);
		}
	}
	
	public BlockPos getOrigin(){
		return TemplateMultiblock.withSettingsAndOffset(this.worldPosition, BlockPos.ZERO.subtract(this.posInMultiblock), getIsMirrored(), this.multiblockInstance.untransformDirection(getFacing()));
	}
	
	public BlockPos getBlockPosForPos(BlockPos targetPos){
		BlockPos origin = getOrigin();
		return TemplateMultiblock.withSettingsAndOffset(origin, targetPos, getIsMirrored(), this.multiblockInstance.untransformDirection(getFacing()));
	}
	
	public void replaceStructureBlock(BlockPos pos, BlockState state, ItemStack stack, int h, int l, int w){
		if(state.getBlock() == this.getBlockState().getBlock())
			getLevelNonnull().removeBlock(pos, false);
		
		getLevelNonnull().setBlockAndUpdate(pos, state);
		BlockEntity tile = getLevelNonnull().getBlockEntity(pos);
		if(tile instanceof IReadOnPlacement readPlacement)
			readPlacement.readOnPlacement(null, stack);
	}
	
	// =================================
	// REDSTONE CONTROL
	// =================================
	public Set<BlockPos> getRedstonePos(){
		throw new UnsupportedOperationException("Tried to get RS position for a multiblock without RS control!");
	}
	
	public boolean isRedstonePos(){
		if(!this.hasRedstoneControl || getRedstonePos() == null)
			return false;
		for(BlockPos i:getRedstonePos())
			if(this.posInMultiblock.equals(i))
				return true;
		return false;
	}
	
	@Override
	public InteractionResult screwdriverUseSide(Direction side, Player player, InteractionHand hand, Vec3 hitVec){
		if(this.isRedstonePos() && this.hasRedstoneControl){
			if(!this.level.isClientSide){
				KineticMultiblockPartTileEntity<T> master = master();
				if(master != null){
					master.redstoneControlInverted = !master.redstoneControlInverted;
					ChatUtils.sendServerNoSpamMessages(player, new TranslatableComponent(Lib.CHAT_INFO + "rsControl." + (master.redstoneControlInverted ? "invertedOn" : "invertedOff")));
					this.updateMasterBlock(null, true);
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
	
	public boolean isRSDisabled(){
		Set<BlockPos> rsPositions = getRedstonePos();
		if(rsPositions == null || rsPositions.isEmpty())
			return false;
		KineticMultiblockPartTileEntity<?> master = master();
		if(master == null)
			master = this;
		if(master.computerControl.isAttached())
			return !master.computerControl.isEnabled();
		for(BlockPos rsPos:rsPositions){
			T tile = this.getEntityForPos(rsPos);
			if(tile != null){
				boolean b = tile.isRSPowered();
				if(this.redstoneControlInverted != b)
					return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public T getEntityForPos(BlockPos targetPosInMB){
		BlockPos target = getBlockPosForPos(targetPosInMB);
		BlockEntity tile = SafeChunkUtils.getSafeBE(getLevelNonnull(), target);
		if(this.getClass().isInstance(tile))
			return (T) tile;
		return null;
	}
	
	@Nonnull
	@Override
	public BlockPos getModelOffset(BlockState state, @Nullable Vec3i size){
		BlockPos mirroredPosInMB = this.posInMultiblock;
		if(size == null)
			size = this.multiblockInstance.getSize(this.level);
		if(getIsMirrored())
			mirroredPosInMB = new BlockPos(size.getX() - mirroredPosInMB.getX() - 1, mirroredPosInMB.getY(), mirroredPosInMB.getZ());
		return this.multiblockInstance.multiblockToModelPos(mirroredPosInMB);
	}
	
	public VoxelShape getShape(CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> cache){
		return cache.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	@Override
	public Stream<ComputerControlState> getAllComputerControlStates(){
		return Stream.of(this.computerControl);
	}
	
	public static <T extends MultiblockPartBlockEntity<?> & IComparatorOverride> void updateComparators(T tile, Collection<BlockPos> offsets, MutableInt cachedValue, int newValue){
		if(newValue == cachedValue.intValue())
			return;
		cachedValue.setValue(newValue);
		final Level world = tile.getLevelNonnull();
		for(BlockPos offset:offsets){
			final BlockPos worldPos = tile.getBlockPosForPos(offset);
			final BlockState stateAt = world.getBlockState(worldPos);
			world.updateNeighbourForOutputSignal(worldPos, stateAt.getBlock());
		}
	}
}
