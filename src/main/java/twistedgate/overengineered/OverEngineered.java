package twistedgate.overengineered;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import twistedgate.overengineered.client.ClientProxy;
import twistedgate.overengineered.common.CommonProxy;
import twistedgate.overengineered.common.OEContent;
import twistedgate.overengineered.common.OERegisters;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.utils.ExternalModContent;

@Mod(OverEngineered.MODID)
public class OverEngineered{
	public static final String MODID = "overengineered";
	
	public static final Logger log = LogManager.getLogger(MODID);
	
	public static final CreativeModeTab creativeTab = new CreativeModeTab(MODID){
		@Override
		@Nonnull
		public ItemStack makeIcon(){
			return new ItemStack(OEContent.Blocks.BUSBAR.get());
		}
	};
	
	// Complete hack: DistExecutor::safeRunForDist intentionally tries to access the "wrong" supplier in dev, which
	// throws an error (rather than an exception) on J16 due to trying to load a client-only class. So we need to
	// replace the error with an exception in dev.
	public static <T> Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in){
		if(FMLLoader.isProduction())
			return in;
		return () -> {
			try{
				return in.get();
			}catch(BootstrapMethodError e){
				throw new RuntimeException(e);
			}
		};
	}
	
	public static final CommonProxy proxy = DistExecutor.safeRunForDist(bootstrapErrorToXCPInDev(() -> ClientProxy::new), bootstrapErrorToXCPInDev(() -> CommonProxy::new));
	
	public OverEngineered(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		
		IEventBus eBus = FMLJavaModLoadingContext.get().getModEventBus();
		OERegisters.addRegistersToEventBus(eBus);
		
		OEContent.populate();
		OETileTypes.forceClassLoad();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void setup(final FMLCommonSetupEvent event){
		proxy.setup();
		
		proxy.preInit();
		
		OEContent.init(event);
		
		proxy.init();
		
		proxy.postInit();
		
		ExternalModContent.init();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.completed(event);
	}
	
	public void serverAboutToStart(ServerAboutToStartEvent event){
		proxy.serverAboutToStart();
	}
	
	public void serverStarting(ServerStartingEvent event){
		proxy.serverStarting();
	}
	
	public void serverStarted(ServerStartedEvent event){
		proxy.serverStarted();
	}
	
	private void enqueueIMC(final InterModEnqueueEvent event){
		/*
		InterModComms.sendTo("examplemod", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
		*/
	}
	
	private void processIMC(final InterModProcessEvent event){
		/*
		LOGGER.info("Got IMC {}", event.getIMCStream().map(m -> m.messageSupplier().get()).collect(Collectors.toList()));
		*/
	}
}
