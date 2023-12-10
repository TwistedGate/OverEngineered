package twistedgate.overengineered.common.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.BlockEntry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import twistedgate.overengineered.common.OEContent;
import twistedgate.overengineered.utils.ResourceUtils;

public class OERecipes extends RecipeProvider{
	private final Map<String, Integer> PATH_COUNT = new HashMap<>();
	
	public OERecipes(DataGenerator pGenerator){
		super(pGenerator);
	}
	
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> out){
		BlockEntry<BasicConnectorBlock<?>> lv_relay = IEBlocks.Connectors.getEnergyConnector(WireType.LV_CATEGORY, true);
		BlockEntry<BasicConnectorBlock<?>> mv_relay = IEBlocks.Connectors.getEnergyConnector(WireType.MV_CATEGORY, true);
		
		ShapedRecipeBuilder.shaped(OEContent.Blocks.BUSBAR.get(), 3)
			.group("oe_busbar")
			.pattern("CCC")
			.pattern("LRM")
			.define('C', Items.COPPER_INGOT)
			.define('R', Items.RED_DYE)
			.define('L', lv_relay)
			.define('M', mv_relay)
			.unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
			.unlockedBy("has_" + toPath(lv_relay), has(lv_relay))
			.unlockedBy("has_" + toPath(mv_relay), has(mv_relay))
			.save(out, rl("busbar"));
		
		ShapedRecipeBuilder.shaped(OEContent.Blocks.BUSBAR.get(), 3)
			.group("oe_busbar")
			.pattern("CCC")
			.pattern("MRL")
			.define('C', Items.COPPER_INGOT)
			.define('R', Items.RED_DYE)
			.define('L', lv_relay)
			.define('M', mv_relay)
			.unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
			.unlockedBy("has_" + toPath(lv_relay), has(lv_relay))
			.unlockedBy("has_" + toPath(mv_relay), has(mv_relay))
			.save(out, rl("busbar"));
	}
	
	private ResourceLocation rl(String str){
		if(PATH_COUNT.containsKey(str)){
			int count = PATH_COUNT.get(str) + 1;
			PATH_COUNT.put(str, count);
			return ResourceUtils.oe(str + count);
		}
		PATH_COUNT.put(str, 1);
		return ResourceUtils.oe(str);
	}
	
	private String toPath(ItemLike src){
		return src.asItem().getRegistryName().getPath();
	}
}
