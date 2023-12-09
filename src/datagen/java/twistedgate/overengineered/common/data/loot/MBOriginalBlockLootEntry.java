package twistedgate.overengineered.common.data.loot;

import java.util.function.Consumer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import twistedgate.overengineered.experimental.common.blocks.tileentity.KineticMultiblockPartTileEntity;
import twistedgate.overengineered.utils.ResourceUtils;

public class MBOriginalBlockLootEntry extends LootPoolSingletonContainer{
	public static final ResourceLocation ID = ResourceUtils.oe("multiblock_original_block");
	
	protected MBOriginalBlockLootEntry(int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions){
		super(pWeight, pQuality, pConditions, pFunctions);
	}
	
	@Override
	protected void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext){
		if(pLootContext.hasParam(LootContextParams.BLOCK_ENTITY))
		{
			BlockEntity te = pLootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
			if(te instanceof KineticMultiblockPartTileEntity<?> mbTile){
				Utils.getDrops(mbTile.getOriginalBlock(),
						new LootContext.Builder(pLootContext.getLevel())
								.withOptionalParameter(LootContextParams.TOOL, pLootContext.getParamOrNull(LootContextParams.TOOL))
								.withOptionalParameter(LootContextParams.ORIGIN, pLootContext.getParamOrNull(LootContextParams.ORIGIN))
				).forEach(pStackConsumer);
			}
		}
	}
	
	public static LootPoolSingletonContainer.Builder<?> builder(){
		return simpleBuilder(MBOriginalBlockLootEntry::new);
	}
	
	@Override
	public LootPoolEntryType getType(){
		return OELootFunctions.multiblockOriginalBlock;
	}
	
	public static class Serializer extends LootPoolSingletonContainer.Serializer<MBOriginalBlockLootEntry>{
		@Override
		protected MBOriginalBlockLootEntry deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, LootItemFunction[] pFunctions){
			return new MBOriginalBlockLootEntry(pWeight, pQuality, pConditions, pFunctions);
		}
	}
}
