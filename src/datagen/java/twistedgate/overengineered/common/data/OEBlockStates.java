package twistedgate.overengineered.common.data;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.NongeneratedModels.NongeneratedModel;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.OEContent;
import twistedgate.overengineered.common.multiblock.UniversalMotorMultiblock;

@SuppressWarnings("unused")
public class OEBlockStates extends BlockStateProvider{
	final ExistingFileHelper exFileHelper;
	private final NongeneratedModels nongeneratedModels;
	public OEBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper){
		super(gen, OverEngineered.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
		this.nongeneratedModels = new NongeneratedModels(gen, exFileHelper);
	}
	
	@Override
	protected void registerStatesAndModels(){
		// Multiblocks
		universalmotor();
		
		// Blocks
		//simpleBlockWithItem(OEContent.Blocks.CREATE_TEST.get());
	}
	
	private void universalmotor(){
		ResourceLocation texture = modLoc("multiblock/universal_motor");
		ResourceLocation modelNormal = modLoc("models/multiblock/obj/universal_motor.obj");
		ResourceLocation modelMirrored = modLoc("models/multiblock/obj/universal_motor_mirrored.obj");
		
		BlockModelBuilder normal = multiblockModel(OEContent.Multiblock.UNIVERSAL_MOTOR.get(), modelNormal, texture, "", UniversalMotorMultiblock.INSTANCE, false);
		BlockModelBuilder mirrored = multiblockModel(OEContent.Multiblock.UNIVERSAL_MOTOR.get(), modelMirrored, texture, "_mirrored", UniversalMotorMultiblock.INSTANCE, true);
		
		createMultiblock(OEContent.Multiblock.UNIVERSAL_MOTOR.get(), normal, mirrored, texture);
	}
	
	/**
	 * From {@link blusunrize.immersiveengineering.common.data.BlockStates}
	 */
	private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
		createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
	}
	
	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
		Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
		VariantBlockStateBuilder builder = getVariantBuilder(b);
		
		boolean[] possibleMirrorStates;
		if(mirroredState != null)
			possibleMirrorStates = new boolean[]{false, true};
		else
			possibleMirrorStates = new boolean[1];
		for(boolean mirrored:possibleMirrorStates)
			for(Direction dir:facing.getPossibleValues()){
				final int angleY;
				final int angleX;
				if(facing.getPossibleValues().contains(Direction.UP)){
					angleX = -90 * dir.getStepY();
					if(dir.getAxis() != Direction.Axis.Y)
						angleY = getAngle(dir, rotationOffset);
					else
						angleY = 0;
				}else{
					angleY = getAngle(dir, rotationOffset);
					angleX = 0;
				}
				
				ModelFile model = mirrored ? mirroredModel : masterModel;
				PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
						.with(facing, dir);
				
				if(mirroredState != null)
					partialState = partialState.with(mirroredState, mirrored);
				
				partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
			}
	}
	
	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private int getAngle(Direction dir, int offset){
		return (int) ((dir.toYRot() + offset) % 360);
	}
	
	private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
		UnaryOperator<BlockPos> transform = UnaryOperator.identity();
		if(mirror){
			Vec3i size = mb.getSize(null);
			transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
		}
		final Vec3i offset = mb.getMasterFromOriginOffset();
		
		Stream<Vec3i> partsStream = mb.getStructure(null).stream()
			.filter(info -> !info.state.isAir())
			.map(info -> info.pos)
			.map(transform)
			.map(p -> p.subtract(offset));
		
		String name = getMultiblockPath(block) + add;
		NongeneratedModel base = nongeneratedModels.withExistingParent(name, mcLoc("block"))
			.customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
			.texture("texture", texture)
			.texture("particle", texture);
		
		BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
			.customLoader(SplitModelBuilder::begin)
			.innerModel(base)
			.parts(partsStream.collect(Collectors.toList()))
			.dynamic(false).end();
		
		return split;
	}
	
	private String getMultiblockPath(Block b){
		return "multiblock/" + getPath(b);
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
	
	private void simpleBlockWithItem(Block block){
		ModelFile file = cubeAll(block);
		
		getVariantBuilder(block).partialState()
			.setModels(new ConfiguredModel(file));
		itemModelWithParent(block, file);
	}
	
	private void itemModelWithParent(Block block, ModelFile parent){
		getItemBuilder(block).parent(parent)
			.texture("particle", modLoc("block/" + getPath(block)));
	}
	
	private ItemModelBuilder getItemBuilder(Block block){
		return itemModels().getBuilder(modLoc("item/" + getPath(block)).toString());
	}
	
	private String getPath(Block b){
		return b.getRegistryName().getPath();
	}
}
