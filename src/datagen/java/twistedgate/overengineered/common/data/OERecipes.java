package twistedgate.overengineered.common.data;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public class OERecipes extends RecipeProvider{
	public OERecipes(DataGenerator pGenerator){
		super(pGenerator);
	}
	
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> out){
		
	}
}
