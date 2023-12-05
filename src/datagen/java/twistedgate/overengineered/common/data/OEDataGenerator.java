package twistedgate.overengineered.common.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import twistedgate.overengineered.OverEngineered;

@EventBusSubscriber(modid = OverEngineered.MODID, bus = Bus.MOD)
public class OEDataGenerator{
	public static final Logger log = LogManager.getLogger(OverEngineered.MODID + "/DataGenerator");
	
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper exhelper = event.getExistingFileHelper();
		StaticTemplateManager.EXISTING_HELPER = exhelper;
		
		if(event.includeServer()){
			OEBlockTags blockTags = new OEBlockTags(generator, exhelper);
			generator.addProvider(blockTags);
			generator.addProvider(new OEItemTags(generator, blockTags, exhelper));
			generator.addProvider(new OERecipes(generator));
		}
		
		if(event.includeClient()){
			generator.addProvider(new OEBlockStates(generator, exhelper));
			generator.addProvider(new OEItemModels(generator, exhelper));
		}
		
	}
}
