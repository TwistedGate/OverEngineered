package twistedgate.overengineered.common.multiblock;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public abstract class OETemplateMultiblock extends IETemplateMultiblock{
	private final Supplier<? extends Block> baseState;
	public OETemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, Supplier<? extends Block> baseState){
		super(loc, masterFromOrigin, triggerFromOrigin, size, new IEBlocks.BlockEntry<>(baseState.get()));
		this.baseState = baseState;
	}
	
	public Block getBaseBlock(){
		return this.baseState.get();
	}
}
