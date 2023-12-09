package twistedgate.overengineered.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorTileEntity;

public class UniversalMotorBlock extends OEMetalMultiblock<UniversalMotorTileEntity>{
	public UniversalMotorBlock(){
		super(OETileTypes.UNIVERSAL_MOTOR, Block.Properties.of(Material.METAL).sound(SoundType.METAL));
	}
}
