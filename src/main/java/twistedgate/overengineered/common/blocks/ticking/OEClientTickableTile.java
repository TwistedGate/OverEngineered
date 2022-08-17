package twistedgate.overengineered.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface OEClientTickableTile{
	void tickClient();
	
	static <T extends BlockEntity & OEClientTickableTile> BlockEntityTicker<T> makeTicker(){
		return (level, pos, state, te) -> te.tickClient();
	}
}
