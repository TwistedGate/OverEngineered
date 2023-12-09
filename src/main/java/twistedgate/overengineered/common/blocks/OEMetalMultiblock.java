package twistedgate.overengineered.common.blocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import twistedgate.overengineered.common.blocks.ticking.OECommonTickableTile;
import twistedgate.overengineered.experimental.common.blocks.CustomHorizontalAxisKineticBlock;
import twistedgate.overengineered.experimental.common.blocks.tileentity.KineticMultiblockPartTileEntity;

public abstract class OEMetalMultiblock<T extends KineticMultiblockPartTileEntity<T> & OECommonTickableTile> extends CustomHorizontalAxisKineticBlock<T>{
	private final MultiblockBEType<T> multiblockBEType;
	
	public OEMetalMultiblock(MultiblockBEType<T> entityType, Block.Properties props){
		super(entityType, props.strength(3, 15).requiresCorrectToolForDrops().isViewBlocking((state, blockReader, pos) -> false).noOcclusion());
		this.multiblockBEType = entityType;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.MIRRORED);
	}
	
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level world, BlockState state, BlockEntityType<E> type){
		return OEBlockBase.createTickerHelper(world.isClientSide, type, this.multiblockBEType.master());
	}
}
