package twistedgate.overengineered.common;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.common.blocks.tileentity.BusbarTileEntity;

public class OETileTypes{
	public static final RegistryObject<BlockEntityType<BusbarTileEntity>> BUS = OERegisters.registerTE("tile_bus", BusbarTileEntity::new, OEContent.Blocks.BUSBAR);
	
	public static void forceClassLoad(){
	}
}
