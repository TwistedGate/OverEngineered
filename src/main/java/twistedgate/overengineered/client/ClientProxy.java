package twistedgate.overengineered.client;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import twistedgate.overengineered.common.CommonProxy;
import twistedgate.overengineered.utils.ResourceUtils;

public class ClientProxy extends CommonProxy{
	@Override
	public void setup(){
	}
	
	@Override
	public void preInit(){
	}
	
	@Override
	public void init(){
	}
	
	@Override
	public void postInit(){
	}
	
	@Override
	public void completed(ParallelDispatchEvent event){
		// TODO Soon™
		//setupManualPages();
	}
	
	@Override
	public void serverAboutToStart(){
	}
	
	@Override
	public void serverStarting(){
	}
	
	@Override
	public void serverStarted(){
	}
	
	@SuppressWarnings("unused")
	private static InnerNode<ResourceLocation, ManualEntry> OE_CATEGORY;
	
	public void setupManualPages(){
		ManualInstance man = ManualHelper.getManual();
		
		OE_CATEGORY = man.getRoot().getOrCreateSubnode(ResourceUtils.oe("main"), 100);
	}
}
