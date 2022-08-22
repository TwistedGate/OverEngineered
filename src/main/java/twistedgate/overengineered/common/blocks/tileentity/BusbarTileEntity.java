package twistedgate.overengineered.common.blocks.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.common.blocks.ticking.OEClientTickableTile;
import twistedgate.overengineered.common.blocks.ticking.OEServerTickableTile;

public class BusbarTileEntity extends OETileEntityBase implements OEServerTickableTile, OEClientTickableTile{
	public BusbarTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(OETileTypes.BUS.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	protected void writeCustom(CompoundTag compound){
	}
	
	@Override
	protected void readCustom(CompoundTag compound){
	}
	
	@Override
	public void tickClient(){
	}
	
	@Override
	public void tickServer(){
	}
}
