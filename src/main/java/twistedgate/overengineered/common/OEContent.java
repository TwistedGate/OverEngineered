package twistedgate.overengineered.common;

import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

public class OEContent{
	public static class Blocks{
		
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
	}
	
	public static void init(ParallelDispatchEvent event){
	}
}
