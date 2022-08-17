package twistedgate.overengineered.common;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

public class CommonProxy{
	/** Fired at {@link FMLCommonSetupEvent} */
	public void setup(){
	}
	
	public void preInit(){
	}
	
	public void init(){
	}
	
	public void postInit(){
	}
	
	/** Fired at {@link FMLLoadCompleteEvent} */
	public void completed(ParallelDispatchEvent event){
	}
	
	public void serverAboutToStart(){
	}
	
	public void serverStarting(){
	}
	
	public void serverStarted(){
	}
}
