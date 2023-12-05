package twistedgate.overengineered.common.multiblock;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
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
	
	@Override
	protected void replaceStructureBlock(StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
		super.replaceStructureBlock(info, world, actualPos, mirrored, clickDirection, offsetFromMaster);
	}
}
