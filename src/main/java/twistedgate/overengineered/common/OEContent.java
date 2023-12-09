package twistedgate.overengineered.common;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.common.blocks.UniversalMotorBlock;
import twistedgate.overengineered.common.multiblock.UniversalMotorMultiblock; 

public class OEContent{
	public static class Blocks{
		/*
		public static final BlockEntry<CreateTestBlock> CREATE_TEST = OERegisters.REGISTRATE.block("create_test", CreateTestBlock::new)
				.initialProperties(Material.METAL)
				.transform(BlockStressDefaults.setCapacity(64 / 256F))
				.transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
				//.item((b, u) -> new BlockItem(b, u.tab(OverEngineered.creativeTab)))
				//.transform(ModelGen.customItemModel())
				.register();
		*/
		private static void forceClassLoad(){
		}
	}
	
	public static class Items{
		
		private static void forceClassLoad(){
		}
	}
	
	public static class Multiblock{
		
		public static final RegistryObject<UniversalMotorBlock> UNIVERSAL_MOTOR = OERegisters.registerMultiblockBlock("universal_motor", UniversalMotorBlock::new);
		
		private static void forceClassLoad(){
		}
	}
	
	public static void modConstruction(){
		Blocks.forceClassLoad();
		Items.forceClassLoad();
		
		Multiblock.forceClassLoad();
	}
	
	public static void init(ParallelDispatchEvent event){
		MultiblockHandler.registerMultiblock(UniversalMotorMultiblock.INSTANCE);
	}
}
