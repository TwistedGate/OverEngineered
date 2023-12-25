package twistedgate.overengineered.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import twistedgate.overengineered.common.blocks.tileentity.BusbarTileEntity;
import twistedgate.overengineered.utils.ResourceUtils;

public class BusbarRenderer implements BlockEntityRenderer<BusbarTileEntity>{
	static final ResourceLocation MODEL_BUSBAR_STRAIGHT = ResourceUtils.oe("block/busbar/json/busbar_straight");
	static final ResourceLocation MODEL_BUSBAR_BEND = ResourceUtils.oe("block/busbar/json/busbar_bend");
	
	static final ResourceLocation MODEL_BUSBAR_EDGE_IN = ResourceUtils.oe("block/busbar/json/busbar_edge_inside");
	static final ResourceLocation MODEL_BUSBAR_EDGE_OUT = ResourceUtils.oe("block/busbar/json/busbar_edge_outside");
	
	static final ResourceLocation MODEL_BUSBAR_CON_LV_A = ResourceUtils.oe("block/busbar/json/busconnector_lv_a");
	static final ResourceLocation MODEL_BUSBAR_CON_LV_B = ResourceUtils.oe("block/busbar/json/busconnector_lv_b");
	static final ResourceLocation MODEL_BUSBAR_CON_LV_C = ResourceUtils.oe("block/busbar/json/busconnector_lv_c");
	
	static final ResourceLocation MODEL_BUSBAR_CON_MV_A = ResourceUtils.oe("block/busbar/json/busconnector_mv_a");
	static final ResourceLocation MODEL_BUSBAR_CON_MV_B = ResourceUtils.oe("block/busbar/json/busconnector_mv_b");
	static final ResourceLocation MODEL_BUSBAR_CON_MV_C = ResourceUtils.oe("block/busbar/json/busconnector_mv_c");
	
	public static void init(){
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_STRAIGHT);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_BEND);
		
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_EDGE_IN);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_EDGE_OUT);
		
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_LV_A);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_LV_B);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_LV_C);
		
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_MV_A);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_MV_B);
		ForgeModelBakery.addSpecialModel(MODEL_BUSBAR_CON_MV_C);
	}
	
	@Override
	public void render(BusbarTileEntity blockEntity, float partialTick, PoseStack matrix, MultiBufferSource buffer, int light, int overlay){
		matrix.pushPose();
		{
			// TODO EnumBusbarShape dependant Rotation (& Translation since origin is not centered)
			
			matrix.translate(0, 0, 1);
			matrix.mulPose(new Quaternion(Vector3f.YP, 90.0F, true));
			
			renderModel(MODEL_BUSBAR_STRAIGHT, matrix, buffer, light, overlay);
		}
		matrix.popPose();
	}
	
	private static void renderModel(ResourceLocation modelRL, PoseStack matrix, MultiBufferSource buffer, int light, int overlay){
		List<BakedQuad> quads = Model.getCachedQuads(modelRL);
		renderModel(quads, matrix, buffer, light, overlay);
	}
	
	private static void renderModel(List<BakedQuad> quads, PoseStack matrix, MultiBufferSource buffer, int light, int overlay){
		Pose last = matrix.last();
		VertexConsumer solid = buffer.getBuffer(RenderType.solid());
		for(BakedQuad quad:quads){
			float f = switch(quad.getDirection()){
				case DOWN -> 0.75F;
				case NORTH, SOUTH -> 0.5F;
				case EAST, WEST -> 0.6F;
				default -> 1.0F;
			};
			
			solid.putBulkData(last, quad, f, f, f, light, overlay);
		}
	}
	
	public static class Model{
		private static final Random rand = new Random(42L);
		private static final Function<ResourceLocation, BakedModel> model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager()::getModel;
		private static final Map<ResourceLocation, List<BakedQuad>> CACHE = new HashMap<>();
		private static boolean clearRequest = false;
		
		public static void requestCacheClear(){
			clearRequest = true;
		}
		
		private static List<BakedQuad> getCachedQuads(ResourceLocation modelRL){
			if(clearRequest){
				CACHE.clear();
				clearRequest = false;
			}
			
			return CACHE.computeIfAbsent(modelRL, Model::get);
		}
		
		private static List<BakedQuad> get(ResourceLocation modelRL){
			rand.setSeed(42L);
			
			ArrayList<BakedQuad> quads = new ArrayList<>(get(MODEL_BUSBAR_STRAIGHT, null, null, rand, EmptyModelData.INSTANCE));
			for(Direction side:Direction.values()){
				// For some reason not all faces are returned, this gets the rest of them
				quads.addAll(get(MODEL_BUSBAR_STRAIGHT, null, side, rand, EmptyModelData.INSTANCE));
			}
			quads.trimToSize();
			return quads;
		}
		
		private static List<BakedQuad> get(ResourceLocation modelRL, @Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData){
			return model.apply(modelRL).getQuads(state, side, rand, extraData);
		}
	}
}
