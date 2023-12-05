package twistedgate.overengineered.common.blocks;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorSlaveTileEntity;

public class CreateTestBlock extends DirectionalKineticBlock implements IBE<UniversalMotorSlaveTileEntity>{
	
	public CreateTestBlock(){
		this(BlockBehaviour.Properties.of(Material.STONE));
	}
	
	public CreateTestBlock(BlockBehaviour.Properties prop){
		super(prop);
	}
	
	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face){
		return face == state.getValue(FACING);
	}
	
	@Override
	public Axis getRotationAxis(BlockState state){
		return state.getValue(FACING).getAxis();
	}
	
	@Override
	public Class<UniversalMotorSlaveTileEntity> getBlockEntityClass(){
		return UniversalMotorSlaveTileEntity.class;
	}
	
	@Override
	public BlockEntityType<UniversalMotorSlaveTileEntity> getBlockEntityType(){
		return OETileTypes.CREATE_TEST.get();
	}
	
	/*
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType){
		return createTickerHelper(pLevel.isClientSide, pBlockEntityType, OETileTypes.CREATE_TEST);
	}
	*/
}
