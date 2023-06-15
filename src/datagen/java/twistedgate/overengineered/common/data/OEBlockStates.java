package twistedgate.overengineered.common.data;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;

public class OEBlockStates extends BlockStateProvider{
	final ExistingFileHelper exFileHelper;
	public OEBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper){
		super(gen, OverEngineered.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}
	
	@Override
	protected void registerStatesAndModels(){
	}
	
	private BlockModelBuilder objModel(Block b, String modelPath, @Nullable String postFix, ResourceLocation texture){
		if(postFix == null)
			postFix = "";
		
		BlockModelBuilder model = this.models().getBuilder(getPath(b) + postFix)
				.customLoader(OBJLoaderBuilder::begin).modelLocation(modLoc(modelPath)).flipV(true).end()
				.texture("texture", texture)
				.texture("particle", texture);
		return model;
	}
	
	private String getPath(Block b){
		return b.getRegistryName().getPath();
	}
}
