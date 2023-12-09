package twistedgate.overengineered.common.data;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.OEContent;

public class OEItemModels extends ModelProvider<TRSRModelBuilder>{
	public OEItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper){
		super(generator, OverEngineered.MODID, ITEM_FOLDER, TRSRModelBuilder::new, existingFileHelper);
	}
	
	@Override
	protected void registerModels(){
		universalmotorItem();
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
	
	private void universalmotorItem(){
		TRSRModelBuilder model = obj(OEContent.Multiblock.UNIVERSAL_MOTOR.get(), "multiblock/obj/universal_motor.obj")
			.texture("texture", modLoc("multiblock/universal_motor"));
	
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.03125F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, -5, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, -5, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, TransformType.HEAD, new Vector3f(1.5F, 8, 1.5F), null, 0.2F);
		doTransform(trans, TransformType.GUI, new Vector3f(-1, -6, 0), new Vector3f(30, 225, 0), 0.0625F);
		doTransform(trans, TransformType.GROUND, new Vector3f(1, 0, 1), null, 0.0625F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.0625F);
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
	
	private TRSRModelBuilder obj(ItemLike item, String model){
		return getBuilder(item.asItem().getRegistryName().toString())
				.customLoader(OBJLoaderBuilder::begin)
				.modelLocation(modLoc("models/" + model)).flipV(true).end();
	}
	
	@Override
	public String getName(){
		return "Item Models";
	}
}
