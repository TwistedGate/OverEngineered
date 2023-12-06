package twistedgate.overengineered.common.blocks.tileentity;

import java.util.Set;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import twistedgate.overengineered.common.blocks.ticking.OECommonTickableTile;
import twistedgate.overengineered.common.multiblock.UniversalMotorMultiblock;

/**
 * Primary Generator Multiblock TileEntity.<br>
 * <br>
 * Communicates with {@link UniversalMotorSlaveTileEntity} and vice versa.
 * 
 * @author TwistedGate
 */
public class UniversalMotorTileEntity extends PoweredMultiblockBlockEntity<UniversalMotorTileEntity, MultiblockRecipe> implements OECommonTickableTile{
	public static final int maxSpeed = 256;
	
	public static final BlockPos AXLE_IO_A = new BlockPos(1, 1, 2);
	public static final BlockPos AXLE_IO_B = new BlockPos(1, 1, 0);
	
	public static final Set<BlockPos> REDSTONE_POS = Set.of(new BlockPos(0, 1, 2));
	public static final Set<MultiblockFace> ENERGY_POS = Set.of(new MultiblockFace(0, 1, 2, RelativeBlockFace.UP));
	
	public UniversalMotorTileEntity(BlockEntityType<UniversalMotorTileEntity> type, BlockPos pos, BlockState state){
		super(UniversalMotorMultiblock.INSTANCE, 1024, true, type, pos, state);
	}
	
	private float speed = 2;
	
	public void setSpeed(float speed){
		this.speed = Mth.clamp(speed, -maxSpeed, maxSpeed);
	}
	
	public float getSpeed(){
		return this.speed;
	}
	
	@Override
	public void tickClient(){
	}
	
	@Override
	public void tickServer(){
	}
	
	@Override
	public Set<MultiblockFace> getEnergyPos(){
		return ENERGY_POS;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return REDSTONE_POS;
	}
	
	@Override
	public NonNullList<ItemStack> getInventory(){
		return null;
	}
	
	@Override
	public boolean isStackValid(int slot, ItemStack stack){
		return false;
	}
	
	@Override
	public int getSlotLimit(int slot){
		return 0;
	}
	
	@Override
	public void doGraphicalUpdates(){
	}
	
	@Override
	protected MultiblockRecipe getRecipeForId(Level level, ResourceLocation id){
		return null;
	}
	
	@Override
	public IFluidTank[] getInternalTanks(){
		return null;
	}
	
	@Override
	public MultiblockRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	@Override
	public int[] getOutputSlots(){
		return null;
	}
	
	@Override
	public int[] getOutputTanks(){
		return null;
	}
	
	@Override
	public boolean additionalCanProcessCheck(MultiblockProcess<MultiblockRecipe> process){
		return false;
	}
	
	@Override
	public void doProcessOutput(ItemStack output){
	}
	
	@Override
	public void doProcessFluidOutput(FluidStack output){
	}
	
	@Override
	public void onProcessFinish(MultiblockProcess<MultiblockRecipe> process){
	}
	
	@Override
	public int getMaxProcessPerTick(){
		return 0;
	}
	
	@Override
	public int getProcessQueueMaxLength(){
		return 0;
	}
	
	@Override
	public float getMinProcessDistance(MultiblockProcess<MultiblockRecipe> process){
		return 0;
	}
	
	@Override
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	public static enum Mode{
		MOTOR, GENERATOR;
	}
}
