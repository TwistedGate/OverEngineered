package twistedgate.overengineered.client.render;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorTileEntity;
import twistedgate.overengineered.utils.ResourceUtils;

@Mod.EventBusSubscriber(modid = OverEngineered.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class UniversalMotorRenderer implements BlockEntityRenderer<UniversalMotorTileEntity>{
	
	static final ResourceLocation UM_ROTOR_RL = ResourceUtils.oe("multiblock/dyn/universal_motor_rotor");
	static final Function<ResourceLocation, BakedModel> f = rl -> Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getModel(rl);
	
	@SubscribeEvent
	public static void init(ModelRegistryEvent event){
		ForgeModelBakery.addSpecialModel(UM_ROTOR_RL);
	}
	
	@Override
	public boolean shouldRenderOffScreen(@Nonnull UniversalMotorTileEntity te){
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(UniversalMotorTileEntity te, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn){
		if(!te.formed || te.isDummy() || !te.getLevelNonnull().hasChunkAt(te.getBlockPos())){
			return;
		}
		
		renderRotor(matrix, bufferIn, te, partialTicks, combinedLightIn, combinedOverlayIn);
	}
	
	static final Vector3f X_AXIS = new Vector3f(1.0F, 0.0F, 0.0F);
	static final Vector3f Z_AXIS = new Vector3f(0.0F, 0.0F, 1.0F);
	private void renderRotor(PoseStack matrix, MultiBufferSource buffer, UniversalMotorTileEntity te, float partialTicks, int light, int overlay){
		matrix.pushPose();
		{
			float angle = ((AnimationTickHolder.getRenderTime(te.getLevel()) * -KineticBlockEntity.convertToAngular(te.getSpeed())) % 360);
			
			int dir = switch(te.getFacing()){
				case EAST -> -90;
				case SOUTH -> 180;
				case WEST -> 90;
				default -> 0;
			};
			
			matrix.translate(0.5, 1.5, 0.5);
			matrix.mulPose(new Quaternion(Vector3f.YP, dir, true));
			matrix.mulPose(new Quaternion(Vector3f.ZP, angle, true));
			List<BakedQuad> quads = f.apply(UM_ROTOR_RL).getQuads(null, null, null, EmptyModelData.INSTANCE);
			Pose last = matrix.last();
			VertexConsumer solid = buffer.getBuffer(RenderType.solid());
			for(BakedQuad quad:quads){
				solid.putBulkData(last, quad, 0.80F, 0.80F, 0.80F, light, overlay);
			}
		}
		matrix.popPose();
	}
}
