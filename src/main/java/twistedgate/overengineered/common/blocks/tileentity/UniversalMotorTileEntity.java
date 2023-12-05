package twistedgate.overengineered.common.blocks.tileentity;

import java.util.Set;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
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
	
	public UniversalMotorTileEntity(BlockEntityType<UniversalMotorTileEntity> type, BlockPos pos, BlockState state){
		super(UniversalMotorMultiblock.INSTANCE, 1024, true, type, pos, state);
	}
	
	public static final int rotationTopSpeed = 256;
	
	public int rotationSpeed = 2;
	public int rotation = 0;
	
	@Override
	public void tickClient(){
		this.rotation = (this.rotation + rotationSpeed) % 360;
	}
	
	@Override
	public void tickServer(){
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
	public Set<MultiblockFace> getEnergyPos(){
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
}
