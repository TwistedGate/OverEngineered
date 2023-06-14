package twistedgate.overengineered.common;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.blocks.tileentity.TileTypes;

public class OEContent{
	private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, OverEngineered.MODID);
	private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, OverEngineered.MODID);
	
	public static final void addRegistersToEventBus(IEventBus eventBus){
		BLOCK_REGISTER.register(eventBus);
		ITEM_REGISTER.register(eventBus);
		TileTypes.REGISTER.register(eventBus);
	}
	
	protected static final <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> constructor){
		return BLOCK_REGISTER.register(name, constructor);
	}
	
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
	
	public static void populate(){
		Blocks.forceClassLoad();
		Items.forceClassLoad();
		
		Multiblock.forceClassLoad();
	}
	
	public static void init(ParallelDispatchEvent event){
	}
}
