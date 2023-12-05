package twistedgate.overengineered.common;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorSlaveTileEntity;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorTileEntity;

public class OETileTypes{
	
	public static final RegistryObject<BlockEntityType<UniversalMotorSlaveTileEntity>> CREATE_TEST = null;//OERegisters.registerTE("create_test", UniversalMotorSlaveTileEntity::new, OEContent.Blocks.CREATE_TEST);
	
	public static final MultiblockBEType<UniversalMotorTileEntity> UNIVERSAL_MOTOR = OERegisters.registerMultiblockTE("universal_motor", UniversalMotorTileEntity::new, OEContent.Multiblock.UNIVERSAL_MOTOR);
	
	public static void forceClassLoad(){
	}
}
