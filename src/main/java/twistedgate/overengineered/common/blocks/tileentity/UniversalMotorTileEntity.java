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
		
		this.energyMain = new AveragingEnergyStorage(4096);
		this.energyMainCap = MultiblockCapability.make(
			this, be -> be.energyMainCap, KineticMultiblockPartTileEntity::master, registerEnergyIO(this.energyMain, this::isMotor, this::isGenerator)
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
	public Set<BlockPos> getRedstonePos(){
		return REDSTONE_POS;
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
	
	public boolean isMaster(){
		return this.offsetToMaster.equals(BlockPos.ZERO);
	}
	
	@Override
	public void tickClient(){
	}
	
	private int generatedSpeed = 0;
	
	@Override
	public void tickServer(){
		switch(this.mode){
			case GENERATOR -> asGenerator();
			case MOTOR -> asMotor();
		}
		
		changeMode(isRSDisabled() ? Mode.GENERATOR : Mode.MOTOR);
	}
	
	private void asGenerator(){
		if(this.generatedSpeed != 0){
			this.generatedSpeed = 0;
			updateGeneratedRotation();
		}
		
		@SuppressWarnings("unused")
		int amount = this.energyStator.extractEnergy(this.energyStator.getMaxEnergyStored(), false);
		
		// TODO Rotation dependant power generation
		
		setChanged();
	}
	
	private void asMotor(){
		int amount = this.energyMain.extractEnergy(this.energyMain.getMaxEnergyStored(), false);
		
		float f = maxSpeed * (amount / (float) this.energyMain.getMaxEnergyStored());
		if(this.generatedSpeed != (int) f){
			this.generatedSpeed = (int) f;
			
			updateGeneratedRotation();
		}
		
		setChanged();
	}
	
	public UniversalMotorTileEntity getAxleTileEntity(BlockPos axlePos){
		if(this.posInMultiblock.equals(axlePos))
			return this;
		
		return getEntityForPos(axlePos);
	}
	
	@Override
	public float getGeneratedSpeed(){
		if(!isMaster())
			return 0.0F;
		
		float generated = 0.0F;
		
		if(this.mode == Mode.MOTOR){
			generated = convertToDirection(this.generatedSpeed, this.getFacing());
		}
		
		return generated;
	}
	
	/** Stress capacity added to kinetics in Motor-Mode */
	private static final float STRESS_CAPACITY = 16384 / 256F;
	/** Stress impacted on kinetics in Generator-Mode */
	private static final float STRESS_IMPACT = 32768 / 256F;
	
	@Override
	public float calculateAddedStressCapacity(){
		if(!isMaster())
			return 0.0F;
		
		float stress = switch(this.mode){
			case MOTOR -> STRESS_CAPACITY;
			case GENERATOR -> 0.0F;
		};
		
		this.lastCapacityProvided = stress;
		return stress;
	}
	
	@Override
	public float calculateStressApplied(){
		if(!isMaster())
			return 0.0F;
		
		float stress = switch(this.mode){
			case GENERATOR -> STRESS_IMPACT;
			case MOTOR -> 0.0F;
		};
		
		this.lastStressApplied = stress;
		return stress;
	}
	
	private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(UniversalMotorTileEntity::getShape);
	
	@Override
	public VoxelShape getBlockBounds(CollisionContext ctx){
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	public static List<AABB> getShape(BlockPos posInMultiblock){
		final int x = posInMultiblock.getX();
		final int y = posInMultiblock.getY();
		final int z = posInMultiblock.getZ();
		
		List<AABB> main = new ArrayList<>();
		
		if(y == 0){
			// Baseplate
			if(!((x == 0 && z == 2) || (x == 2 && z == 2))){
				AABBUtils.box16(main, 0, 0, 0, 16, 8, 16);
			}
		}
		
		if(y == 1){
			// Axle
			if(x == 1 && z >= 0 && z <= 2){
				AABBUtils.box16(main, 5, 5, 0, 11, 11, 16);
			}
			
			if(x == 1){
				// Bearings
				if(z == 0 || z == 2){
					AABBUtils.box16(main, 4, 4, 6, 12, 12, 10);
				}
				
				// Carbon Brush holder
				if(z == 0){
					AABBUtils.box16(main, 0, 6, 6, 4, 10, 16);
					AABBUtils.box16(main, 12, 6, 6, 16, 10, 16);
				}
			}
		}
		
		// Supports
		if(y == 0 || y == 1){
			if(y == 0){
				if(x == 1 && (z == 0 || z == 2)){
					AABBUtils.box16(main, 2, 8, 6, 6, 16, 10);
					AABBUtils.box16(main, 10, 8, 6, 14, 16, 10);
				}
			}
			if(y == 1){
				if(x == 1 && (z == 0 || z == 2)){
					AABBUtils.box16(main, 3, 0, 6, 6, 4, 10);
					AABBUtils.box16(main, 10, 0, 6, 13, 4, 10);
				}
			}
		}
		
		// Stator
		if(z == 1){
			if(!((x == 0 && y == 2) || (x == 2 && y == 2))){
				if(x == 1 && y == 2){
					AABBUtils.box16(main, 0, 0, 4, 16, 14, 12);
				}else{
					AABBUtils.box16(main, 0, 0, 4, 16, 16, 12);
				}
			}else{
				AABBUtils.box16(main, 0, 0, 4, 16, 8, 12);
				if(x == 0 && y == 2) AABBUtils.box16(main, 8, 8, 4, 16, 14, 12);
				if(x == 2 && y == 2) AABBUtils.box16(main, 0, 8, 4, 8, 14, 12);
			}
		}
		
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
