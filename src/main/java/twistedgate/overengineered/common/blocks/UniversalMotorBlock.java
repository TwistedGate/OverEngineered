package twistedgate.overengineered.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.common.blocks.tileentity.UniversalMotorTileEntity;

public class UniversalMotorBlock extends OEMetalMultiblock<UniversalMotorTileEntity>{
	public UniversalMotorBlock(){
		super(OETileTypes.UNIVERSAL_MOTOR, Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3, 15));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(world.isClientSide && player.getItemInHand(hand).isEmpty()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof UniversalMotorTileEntity motor && motor.posInMultiblock.equals(new BlockPos(0, 0, 0))){
				motor = motor.master();
				
				if(!player.isCrouching()){
					if((++motor.rotationSpeed) > UniversalMotorTileEntity.rotationTopSpeed)
						motor.rotationSpeed = UniversalMotorTileEntity.rotationTopSpeed;
				}else{
					if((--motor.rotationSpeed) < -UniversalMotorTileEntity.rotationTopSpeed)
						motor.rotationSpeed = -UniversalMotorTileEntity.rotationTopSpeed;
				}
				player.displayClientMessage(new TextComponent(Integer.toString(motor.rotationSpeed)), true);
			}
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		// TODO Somehow switch to UniversalMotorSlaveTileEntity twice for input/output shaft
		return super.newBlockEntity(pPos, pState);
	}
}
