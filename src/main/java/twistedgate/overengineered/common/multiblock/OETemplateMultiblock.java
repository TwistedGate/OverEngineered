package twistedgate.overengineered.common.multiblock;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.experimental.common.blocks.tileentity.KineticMultiblockPartTileEntity;

public abstract class OETemplateMultiblock extends IETemplateMultiblock{
	private final Supplier<? extends Block> baseState;
	public OETemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, Supplier<? extends Block> baseState){
		super(loc, masterFromOrigin, triggerFromOrigin, size, new IEBlocks.BlockEntry<>(baseState.get()));
		this.baseState = baseState;
	}
	
	@Override
	protected void replaceStructureBlock(StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
		BlockState state = baseState.get().defaultBlockState();
		if(!offsetFromMaster.equals(Vec3i.ZERO))
			state = state.setValue(IEProperties.MULTIBLOCKSLAVE, true);
		world.setBlockAndUpdate(actualPos, state);
		BlockEntity curr = world.getBlockEntity(actualPos);
		if(curr instanceof KineticMultiblockPartTileEntity<?> tile){
			tile.formed = true;
			tile.offsetToMaster = new BlockPos(offsetFromMaster);
			tile.posInMultiblock = info.pos;
			if(state.hasProperty(IEProperties.MIRRORED))
				tile.setMirrored(mirrored);
			tile.setFacing(transformDirection(clickDirection.getOpposite()));
			tile.setChanged();
			world.blockEvent(actualPos, world.getBlockState(actualPos).getBlock(), 255, 0);
		}else
			OverEngineered.log.error("Expected MB TE at {} during placement", actualPos);
	}
	
	@Override
	protected void prepareBlockForDisassembly(Level world, BlockPos pos){
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof KineticMultiblockPartTileEntity<?> multiblockBE)
			multiblockBE.formed = false;
		else if(be != null)
			OverEngineered.log.error("Expected multiblock TE at {}, got {}", pos, be);
	}
	
	public Block getBaseBlock(){
		return this.baseState.get();
	}
}
