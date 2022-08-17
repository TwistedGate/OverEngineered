package twistedgate.overengineered.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface OEServerTickableTile{
	void tickServer();
	
	static <T extends BlockEntity & OEServerTickableTile> BlockEntityTicker<T> makeTicker(){
		return (level, pos, state, te) -> te.tickServer();
	}
}
