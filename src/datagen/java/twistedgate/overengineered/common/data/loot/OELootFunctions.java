package twistedgate.overengineered.common.data.loot;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import twistedgate.overengineered.OverEngineered;

@EventBusSubscriber(modid = OverEngineered.MODID, bus = Bus.MOD)
public class OELootFunctions{
	public static LootPoolEntryType multiblockOriginalBlock;
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event){
		multiblockOriginalBlock = registerEntry(MBOriginalBlockLootEntry.ID, new MBOriginalBlockLootEntry.Serializer());
	}
	
	private static LootPoolEntryType registerEntry(ResourceLocation id, Serializer<? extends LootPoolEntryContainer> serializer){
		return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, id, new LootPoolEntryType(serializer));
	}
}
