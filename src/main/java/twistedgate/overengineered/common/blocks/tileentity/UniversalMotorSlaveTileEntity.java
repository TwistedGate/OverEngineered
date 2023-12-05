package twistedgate.overengineered.common.blocks.tileentity;

import java.util.List;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import twistedgate.overengineered.common.OETileTypes;

/**
 * Used where the Rotor Axis would make contact with a Create Shaft.<br>
 * <br>
 * Communicates with {@link UniversalMotorTileEntity} and vice versa.
 * 
 * @author TwistedGate
 */
public class UniversalMotorSlaveTileEntity extends GeneratingKineticBlockEntity{
	
	protected ScrollValueBehaviour generatedSpeed;
	
	public UniversalMotorSlaveTileEntity(BlockPos pos, BlockState state){
		super(OETileTypes.CREATE_TEST.get(), pos, state);
	}
	
	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours){
		super.addBehaviours(behaviours);
		
		this.generatedSpeed = new KineticScrollValueBehaviour(new TextComponent("Change Speed"), this, new ValueBox())
				.between(-32, 32)
				.withCallback(b -> updateGeneratedRotation());
		this.generatedSpeed.value = 16;
		behaviours.add(this.generatedSpeed);
	}
	
	@Override
	public void initialize(){
		super.initialize();
		if(!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}
	
	@Override
	public void tick(){
		super.tick();
	}
	
	@Override
	public float getGeneratedSpeed(){
		return convertToDirection(this.generatedSpeed.getValue(), Direction.EAST);
	}
	
	private class ValueBox extends ValueBoxTransform.Sided{
		@Override
		protected Vec3 getSouthLocation(){
			return VecHelper.voxelSpace(8, 8, 12.5);
		}
	}
}
