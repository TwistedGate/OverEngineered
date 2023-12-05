package twistedgate.overengineered.client;

import java.util.function.Supplier;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.client.render.UniversalMotorRenderer;
import twistedgate.overengineered.common.OETileTypes;

@Mod.EventBusSubscriber(modid = OverEngineered.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientModBusEventHandlers{
	@SubscribeEvent
	public static void registerRenders(RegisterRenderers ev){
		registerBERender(ev, OETileTypes.UNIVERSAL_MOTOR.master(), UniversalMotorRenderer::new);
	}
	
	private static <T extends BlockEntity> void registerBERender(RegisterRenderers ev, BlockEntityType<T> type, Supplier<BlockEntityRenderer<T>> factory){
		ev.registerBlockEntityRenderer(type, ctx -> factory.get());
	}
}
