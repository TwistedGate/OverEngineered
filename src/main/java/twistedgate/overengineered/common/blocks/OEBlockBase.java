package twistedgate.overengineered.common.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.common.blocks.ticking.OEClientTickableTile;
import twistedgate.overengineered.common.blocks.ticking.OEServerTickableTile;

public class OEBlockBase extends Block{
	public OEBlockBase(Properties props){
		super(props);
	}
	
	@Nullable
	protected static <E extends BlockEntity & OEServerTickableTile & OEClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(boolean isClient, BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected){
		return createTickerHelper(isClient, actual, expected.get());
	}
	
	@Nullable
	protected static <E extends BlockEntity & OEServerTickableTile & OEClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected){
		if(isClient){
			return createClientTickerHelper(actual, expected);
		}else{
			return createServerTickerHelper(actual, expected);
		}
	}
	
	@Nullable
	protected static <E extends BlockEntity & OEClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createClientTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, OEClientTickableTile::makeTicker);
	}
	
	@Nullable
	protected static <E extends BlockEntity & OEServerTickableTile, A extends BlockEntity> BlockEntityTicker<A> createServerTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, OEServerTickableTile::makeTicker);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker){
		return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
	}
}
