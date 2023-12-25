package twistedgate.overengineered.common;

import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.common.blocks.busbar.BusbarBlock;

public class OEContent{
	public static class Blocks{
		public static final RegistryObject<BusbarBlock> BUSBAR = OERegisters.registerBlock("busbar", BusbarBlock::new, BusbarBlock.Item::new);
		
		private static void forceClassLoad(){
		}
	}
	
	public static class Items{
		
		private static void forceClassLoad(){
		}
	}
	
	// Not even sure OE is going to even have any MB at all
	public static class Multiblock{
		
		private static void forceClassLoad(){
		}
	}
	
	public static void modConstruction(){
		Blocks.forceClassLoad();
		Items.forceClassLoad();
		
		Multiblock.forceClassLoad();
		
		OETileTypes.forceClassLoad();
	}
	
	public static void init(ParallelDispatchEvent event){
	}
}
