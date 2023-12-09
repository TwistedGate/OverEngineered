package twistedgate.overengineered.common;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorTileEntity;

public class OETileTypes{
	
	public static final MultiblockBEType<UniversalMotorTileEntity> UNIVERSAL_MOTOR = OERegisters.registerMultiblockTE("universal_motor", UniversalMotorTileEntity::new, OEContent.Multiblock.UNIVERSAL_MOTOR);
	
	public static void forceClassLoad(){
	}
}
