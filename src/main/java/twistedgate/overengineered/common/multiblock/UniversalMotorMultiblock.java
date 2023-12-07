package twistedgate.overengineered.common.multiblock;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import twistedgate.overengineered.common.OEContent;
import twistedgate.overengineered.utils.ResourceUtils;

public class UniversalMotorMultiblock extends OETemplateMultiblock{
	public static final UniversalMotorMultiblock INSTANCE = new UniversalMotorMultiblock();
	
	private UniversalMotorMultiblock(){
		super(ResourceUtils.oe("multiblocks/universal_motor"), new BlockPos(1, 0, 1), new BlockPos(0, 0, 2), new BlockPos(3, 3, 3), OEContent.Multiblock.UNIVERSAL_MOTOR);
	}
	
	@Override
	public float getManualScale(){
		return 1.0F;
	}
	
	@Override
	public void initializeClient(Consumer<MultiblockManualData> consumer){
	}
	
	@Override
	public Component getDisplayName(){
		return new TextComponent("Universal Motor");
	}
}
