package twistedgate.overengineered.client;

import java.util.function.Predicate;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.OEContent;

@EventBusSubscriber(modid = OverEngineered.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class BlockRenderLayers{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event){
		setRenderLayer(OEContent.Blocks.BUSBAR, t -> t == RenderType.cutout() || t == RenderType.solid());
	}
	
	private static void setRenderLayer(RegistryObject<? extends Block> block, Predicate<RenderType> types){
		ItemBlockRenderTypes.setRenderLayer(block.get(), types);
	}
}
