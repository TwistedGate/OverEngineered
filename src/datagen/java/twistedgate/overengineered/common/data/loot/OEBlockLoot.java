package twistedgate.overengineered.common.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import twistedgate.overengineered.common.OEContent;

public class OEBlockLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>{
	private BiConsumer<ResourceLocation, LootTable.Builder> out;
	
	@Override
	public void accept(BiConsumer<ResourceLocation, Builder> out){
		this.out = out;
		
		registerMultiblock(OEContent.Multiblock.UNIVERSAL_MOTOR.get());
	}
	
	private void registerMultiblock(Block b){
		register(b, dropOriginalBlock());
	}
	
	private LootPool.Builder dropOriginalBlock(){
		return createPoolBuilder().add(MBOriginalBlockLootEntry.builder());
	}
	
	/*
	private LootPool.Builder dropInv(){
		return createPoolBuilder().add(DropInventoryLootEntry.builder());
	}
	*/
	
	private void register(Block b, LootPool.Builder... pools){
		LootTable.Builder builder = LootTable.lootTable();
		for(LootPool.Builder pool:pools)
			builder.withPool(pool);
		register(b, builder);
	}
	
	private void register(Block b, LootTable.Builder table){
		register(b.getRegistryName(), table);
	}
	
	private void register(ResourceLocation name, LootTable.Builder table){
		out.accept(toTableLoc(name), table);
	}
	
	private LootPool.Builder createPoolBuilder(){
		return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
	}
	
	private ResourceLocation toTableLoc(ResourceLocation in){
		return new ResourceLocation(in.getNamespace(), "blocks/" + in.getPath());
	}
}
