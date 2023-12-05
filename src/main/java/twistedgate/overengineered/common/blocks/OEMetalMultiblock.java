package twistedgate.overengineered.common.blocks;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twistedgate.overengineered.common.blocks.ticking.OECommonTickableTile;

public abstract class OEMetalMultiblock<T extends MultiblockPartBlockEntity<T> & OECommonTickableTile> extends MetalMultiblockBlock<T>{
	private final MultiblockBEType<T> multiblockBEType;
	
	public OEMetalMultiblock(MultiblockBEType<T> entityType, Block.Properties props){
		super(entityType, props.requiresCorrectToolForDrops().isViewBlocking((state, blockReader, pos) -> false).noOcclusion());
		this.multiblockBEType = entityType;
	}
	
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level world, BlockState state, BlockEntityType<E> type){
		return OEBlockBase.createTickerHelper(world.isClientSide, type, this.multiblockBEType.master());
	}
}
