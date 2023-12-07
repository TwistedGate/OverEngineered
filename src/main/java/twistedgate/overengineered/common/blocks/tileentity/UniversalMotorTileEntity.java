package twistedgate.overengineered.common.blocks.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import twistedgate.overengineered.common.blocks.ticking.OECommonTickableTile;
import twistedgate.overengineered.common.multiblock.UniversalMotorMultiblock;
import twistedgate.overengineered.experimental.common.blocks.tileentity.KineticMultiblockPartTileEntity;
import twistedgate.overengineered.utils.AABBUtils;

/**
 * <i><b>Warning: Highly Experimental and not finished in any way.</b></i><br>
 * <br>
 * Primary Generator Multiblock TileEntity.<br>
 * <br>
 * Communicates with {@link UniversalMotorSlaveTileEntity} and vice versa.
 * 
 * @author TwistedGate
 */
public class UniversalMotorTileEntity extends KineticMultiblockPartTileEntity<UniversalMotorTileEntity> implements OECommonTickableTile, IEBlockInterfaces.IBlockBounds{
	public static final int maxSpeed = 256;
	
	public static final BlockPos AXLE_IO_A = new BlockPos(1, 1, 2);
	public static final BlockPos AXLE_IO_B = new BlockPos(1, 1, 0);
	
	public static final Set<BlockPos> REDSTONE_POS = Set.of(new BlockPos(0, 1, 2));
	public static final BlockPos ENERGY_POS = new BlockPos(0, 1, 2);
	public static final BlockPos STATOR_ENERGY_POS = new BlockPos(2, 0, 2);
	
	protected Mode mode = Mode.MOTOR;
	
	/** Serves as both Input (Motor Mode) and Output (Generator Mode) */
	protected final AveragingEnergyStorage energyMain;
	protected final MultiblockCapability<IEnergyStorage> energyMainCap;
	
	/** Only used when in Generator Mode */
	protected final AveragingEnergyStorage energyStator;
	protected final MultiblockCapability<IEnergyStorage> energyStatorCap;
	
	public UniversalMotorTileEntity(BlockEntityType<UniversalMotorTileEntity> type, BlockPos pos, BlockState state){
		super(UniversalMotorMultiblock.INSTANCE, type, true, pos, state);
		
		this.energyMain = new AveragingEnergyStorage(2048);
		this.energyMainCap = MultiblockCapability.make(
			this, be -> be.energyMainCap, KineticMultiblockPartTileEntity::master,
			
			registerEnergyIO(this.energyMain, this::isMotor, this::isGenerator)
		);
		
		this.energyStator = new AveragingEnergyStorage(1024);
		this.energyStatorCap = MultiblockCapability.make(
			this, be -> be.energyStatorCap, KineticMultiblockPartTileEntity::master, registerEnergyInput(this.energyStator) 
		);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY && (side == null || isEnergyPos(ENERGY_POS, side))){
			return this.energyMainCap.getAndCast();
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || isEnergyPos(STATOR_ENERGY_POS, side))){
			return this.energyStatorCap.getAndCast();
		}
		return super.getCapability(cap, side);
	}
	
	public boolean isEnergyPos(BlockPos pos, Direction side){
		return this.posInMultiblock.equals(pos) && side == Direction.UP;
	}
	
	@Override
	public void readCustom(CompoundTag tag, boolean clientPacket){
		super.readCustom(tag, clientPacket);
		
		CompoundTag energy = tag.getCompound("energy");
		this.energyMain.deserializeNBT(energy.get("main"));
		this.energyStator.deserializeNBT(energy.get("stator"));
		
		this.mode = Mode.values()[tag.getInt("mode")];
	}
	
	@Override
	public void writeCustom(CompoundTag tag, boolean clientPacket){
		super.writeCustom(tag, clientPacket);
		
		CompoundTag energy = new CompoundTag();
		energy.put("main", this.energyMain.serializeNBT());
		energy.put("stator", this.energyStator.serializeNBT());
		tag.put("energy", energy);
		
		tag.putInt("mode", this.mode.id());
	}
	
	@Override
	public void setSpeed(float speed){
		this.speed = Mth.clamp(speed, -maxSpeed, maxSpeed);
	}
	
	public void changeMode(Mode newMode){
		if(newMode == null || newMode == this.mode)
			return;
		
		this.mode = newMode;
		
		// Dump energy on mode switch
		this.energyMain.setStoredEnergy(0);
		
		setChanged();
	}
	
	public Mode getMode(){
		return this.mode;
	}
	
	private boolean isGenerator(){
		return this.mode == Mode.GENERATOR;
	}
	
	private boolean isMotor(){
		return this.mode == Mode.MOTOR;
	}
	
	@Override
	public void tickClient(){
	}
	
	private int generatedSpeed = 0;
	
	@Override
	public void tickServer(){
		
		changeMode(isRSDisabled() ? Mode.GENERATOR : Mode.MOTOR);
		
		switch(this.mode){
			case GENERATOR -> asGenerator();
			case MOTOR -> asMotor();
		}
	}
	
	private void asGenerator(){
		if(this.generatedSpeed != 0){
			this.generatedSpeed = 0;
			updateGeneratedRotation();
		}
		
		int amount = this.energyStator.extractEnergy(this.energyStator.getMaxEnergyStored(), false);
		setChanged();
	}
	
	private void asMotor(){
		int amount = this.energyMain.extractEnergy(this.energyMain.getMaxEnergyStored(), false);
		
		float f = maxSpeed * (amount / (float) this.energyMain.getMaxEnergyStored());
		if(this.generatedSpeed != (int) f){
			this.generatedSpeed = (int) f;
			
			UniversalMotorTileEntity axleA = getAxleTileEntity(AXLE_IO_A);
			UniversalMotorTileEntity axleB = getAxleTileEntity(AXLE_IO_B);
			
			axleA.updateGeneratedRotation();
			axleB.updateGeneratedRotation();
		}
		
		setChanged();
	}
	
	@Override
	public float getGeneratedSpeed(){
		float generated = 0.0F;
		if(isAxleA() || isAxleB()){
			UniversalMotorTileEntity master = master();
			
			if(master != null && master.mode == Mode.MOTOR){
				generated = convertToDirection(master.generatedSpeed, master.getFacing());
			}
		}
		return generated;
	}
	
	@Override
	public void onSpeedChanged(float previousSpeed){
		super.onSpeedChanged(previousSpeed);
	}
	
	@Override
	public void updateGeneratedRotation(){
		super.updateGeneratedRotation();
	}
	
	private static float stressSupplier = 0.0625F;
	
	@Override
	public float calculateStressApplied(){
		float stress = 0.0F;
		if(isAxleA() || isAxleB()){
			UniversalMotorTileEntity master = master();
			if(master != null){
				stress = switch(master.mode){
					case GENERATOR -> stressSupplier;
					case MOTOR -> 0.0F;
				};
			}
		}
		
		this.lastStressApplied = stress;
		return stress;
	}
	
	@Override
	public float calculateAddedStressCapacity(){
		float stress = 0.0F;
		if(isAxleA() || isAxleB()){
			UniversalMotorTileEntity master = master();
			if(master != null){
				stress = switch(master.mode){
					case MOTOR -> stressSupplier;
					case GENERATOR -> 0.0F;
				};
			}
		}
		
		this.lastCapacityProvided = stress;
		return stress;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return REDSTONE_POS;
	}
	
	public UniversalMotorTileEntity getAxleTileEntity(BlockPos axlePos){
		if(this.posInMultiblock.equals(axlePos))
			return this;
		
		return getEntityForPos(axlePos);
	}
	
	public boolean isAxleA(){
		return this.posInMultiblock.equals(AXLE_IO_A);
	}
	
	public boolean isAxleB(){
		return this.posInMultiblock.equals(AXLE_IO_B);
	}
	
	private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(UniversalMotorTileEntity::getShape);
	
	@Override
	public VoxelShape getBlockBounds(CollisionContext ctx){
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	public static List<AABB> getShape(BlockPos posInMultiblock){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		List<AABB> main = new ArrayList<>();
		
		// TODO
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(AABBUtils.FULL);
		}
		return main;
	}
	
	// Static stuff (classes, methods and so on)
	
	public static enum Mode{
		/** Electrical -> Mechanical */
		MOTOR,
		/** Mechanical -> Electrical */
		GENERATOR;
		
		public int id(){
			return this.ordinal();
		}
	}
}
