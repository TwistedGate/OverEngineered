package twistedgate.overengineered.common.data;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;


public class OEItemModels extends ItemModelProvider{
	public OEItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper){
		super(generator, OverEngineered.MODID, existingFileHelper);
	}
	
	@Override
	protected void registerModels(){
//		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
//		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, new Vector3f(0, 0, 0), new Vector3f(0, 45, 0), 1.0F);
//		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, new Vector3f(0, 0, 0), new Vector3f(0, 45, 0), 1.0F);
//		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, null, null, 1.0F);
//		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 1.0F);
//		doTransform(trans, TransformType.HEAD, new Vector3f(0, 0, 0), null, 1.0F);
//		doTransform(trans, TransformType.GUI, new Vector3f(0, 2, 0), new Vector3f(30, 45, 0), 0.75F);
//		doTransform(trans, TransformType.GROUND, new Vector3f(0, 0, 0), null, 1.0F);
//		doTransform(trans, TransformType.FIXED, new Vector3f(0, 0, -7), new Vector3f(-90, 0, 0), 1.0F);
	}
	
	private void doTransform(ModelBuilder<?>.TransformsBuilder transform, TransformType type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale){
		ModelBuilder<?>.TransformsBuilder.TransformVecBuilder trans = transform.transform(type);
		if(translation != null)
			trans.translation(translation.x(), translation.y(), translation.z());
		if(rotationAngle != null)
			trans.rotation(rotationAngle.x(), rotationAngle.y(), rotationAngle.z());
		trans.scale(scale);
		trans.end();
	}
	
	private ItemModelBuilder obj(ItemLike item, String model){
		return getBuilder(item.asItem().getRegistryName().toString())
				.customLoader(OBJLoaderBuilder::begin)
				.modelLocation(modLoc("models/" + model)).flipV(true).end();
	}
}
