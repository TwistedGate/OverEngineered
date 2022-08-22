package twistedgate.overengineered.common.data;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.OEContent;
import twistedgate.overengineered.common.blocks.busbar.BusbarBlock;
import twistedgate.overengineered.utils.enums.EnumBusbarShape;

public class OEBlockStates extends BlockStateProvider{
	final ExistingFileHelper exFileHelper;
	public OEBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper){
		super(gen, OverEngineered.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}
	
	@Override
	protected void registerStatesAndModels(){
		busbar();
	}
	
	private void busbar(){
		// Insulators
		BlockModelBuilder model_insulators_normal = busModel("models/block/obj/busbar/busbar_a.obj", "_with_insulators");
		BlockModelBuilder model_insulators_rotated = busModel("models/block/obj/busbar/busbar_a_alt.obj", "_with_insulators_pre_y_rotated");
		
		// Floating
		BlockModelBuilder model_floating_normal = busModel("models/block/obj/busbar/busbar_b.obj", "_without_insulators");
		BlockModelBuilder model_floating_rotated = busModel("models/block/obj/busbar/busbar_b_alt.obj", "_without_insulators_pre_y_rotated");
		
		BlockModelBuilder model_bend = busModel("models/block/obj/busbar/busbar_bend.obj", "_bend");
		BlockModelBuilder model_edge_inside = busModel("models/block/obj/busbar/busbar_edge_inside.obj", "_horizontal_up");
		BlockModelBuilder model_edge_outside = busModel("models/block/obj/busbar/busbar_edge_outside.obj", "_horizontal_down");
		
		model_insulators_normal.assertExistence();
		model_insulators_rotated.assertExistence();
		
		model_floating_normal.assertExistence();
		model_floating_rotated.assertExistence();
		
		model_bend.assertExistence();
		model_edge_inside.assertExistence();
		model_edge_outside.assertExistence();
		
		bus(EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH, model_insulators_normal, 0, 0);
		bus(EnumBusbarShape.INSULATORS_DOWN_EAST_WEST, model_insulators_normal, 0, 90);
		bus(EnumBusbarShape.INSULATORS_UP_NORTH_SOUTH, model_insulators_normal, 180, 0);
		bus(EnumBusbarShape.INSULATORS_UP_EAST_WEST, model_insulators_normal, 180, 90);
		
		bus(EnumBusbarShape.FLOATING_DOWN_NORTH_SOUTH, model_floating_normal, 0, 0);
		bus(EnumBusbarShape.FLOATING_DOWN_EAST_WEST, model_floating_normal, 0, 90);
		bus(EnumBusbarShape.FLOATING_UP_NORTH_SOUTH, model_floating_normal, 180, 0);
		bus(EnumBusbarShape.FLOATING_UP_EAST_WEST, model_floating_normal, 180, 90);
		
		EnumBusbarShape.Type.STRAIGHT_INSULATORS_WALL_NORMAL.forEachIndexed((i, shape) -> bus(shape, model_insulators_normal, -90, 90 * i));
		EnumBusbarShape.Type.STRAIGHT_INSULATORS_WALL_ROTATED.forEachIndexed((i, shape) -> bus(shape, model_insulators_rotated, -90, 90 * i));
		
		EnumBusbarShape.Type.STRAIGHT_FLOATING_WALL_NORMAL.forEachIndexed((i, shape) -> bus(shape, model_floating_normal, -90, 90 * i));
		EnumBusbarShape.Type.STRAIGHT_FLOATING_WALL_ROTATED.forEachIndexed((i, shape) -> bus(shape, model_floating_rotated, -90, 90 * i));
		
		EnumBusbarShape.Type.BENDS_FLOOR.forEachIndexed((i, shape) -> bus(shape, model_bend, 0, 90 + 90 * i));
		EnumBusbarShape.Type.BENDS_CEILING.forEachIndexed((i, shape) -> bus(shape, model_bend, 180, 180 + 90 * i));
		EnumBusbarShape.Type.BENDS_WALLS.forEachIndexed((i, shape) -> bus(shape, model_bend, -90, 90 * i));
		
		EnumBusbarShape.Type.EDGE_INSIDE_FLOOR.forEachIndexed((i, shape) -> bus(shape, model_edge_inside, 0, 90 * i));
		EnumBusbarShape.Type.EDGE_INSIDE_CEILING.forEachIndexed((i, shape) -> bus(shape, model_edge_inside, 180, 90 * i));
		EnumBusbarShape.Type.EDGE_OUTSIDE_FLOOR.forEachIndexed((i, shape) -> bus(shape, model_edge_outside, 0, 90 * i));
		EnumBusbarShape.Type.EDGE_OUTSIDE_CEILING.forEachIndexed((i, shape) -> bus(shape, model_edge_outside, 180, 90 * i));
	}
	
	private PartialBlockstate bus(EnumBusbarShape shape, BlockModelBuilder model, int x_rot, int y_rot){
		VariantBlockStateBuilder builder = getVariantBuilder(OEContent.Blocks.BUSBAR.get());
		PartialBlockstate state = builder.partialState();
		
		return state.with(BusbarBlock.SHAPE, shape)
				.addModels(new ConfiguredModel(model, x_rot % 360, y_rot % 360, false));
	}
	
	// Could still be used for specific stuff
	@SuppressWarnings("unused")
	private PartialBlockstate bus(Function<PartialBlockstate, PartialBlockstate> f, BlockModelBuilder model, int x_rot, int y_rot){
		VariantBlockStateBuilder builder = getVariantBuilder(OEContent.Blocks.BUSBAR.get());
		return f.apply(builder.partialState()).addModels(new ConfiguredModel(model, x_rot, y_rot, false));
	}
	
	@SuppressWarnings("unused")
	@Deprecated(forRemoval = true)
	private PartialBlockstate bus(PartialBlockstate state, BlockModelBuilder m0, BlockModelBuilder m1, boolean floating, int x_rot, int y_rot){
		return state.addModels(new ConfiguredModel(floating ? m1 : m0, x_rot, y_rot, false));
	}
	
	private BlockModelBuilder busModel(String modelPath, @Nullable String postFix){
		return objModel(OEContent.Blocks.BUSBAR.get(), modelPath, postFix, modLoc("block/obj/busbar"));
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
