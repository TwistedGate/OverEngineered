package twistedgate.overengineered.common.blocks.busbar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import twistedgate.overengineered.common.OETileTypes;
import twistedgate.overengineered.common.blocks.OEBlockBase;
import twistedgate.overengineered.common.blocks.tileentity.BusbarTileEntity;
import twistedgate.overengineered.utils.ExternalModContent;
import twistedgate.overengineered.utils.enums.EnumBusbarShape;

public class BusbarBlock extends OEBlockBase implements EntityBlock{
	private static final Material MATERIAL = new Material(MaterialColor.METAL, false, false, true, true, false, false, PushReaction.BLOCK);
	
	public static final EnumProperty<EnumBusbarShape> SHAPE = EnumProperty.create("shape", EnumBusbarShape.class);
	
	public BusbarBlock(){
		super(Block.Properties.of(MATERIAL).strength(2.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
		
		registerDefaultState(getStateDefinition().any().setValue(SHAPE, EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		builder.add(SHAPE);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos){
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext){
		Direction direction = pContext.getHorizontalDirection();
		Direction face = pContext.getClickedFace();
		boolean crouching = pContext.getPlayer() != null ? pContext.getPlayer().isCrouching() : false;
		
		// @formatter:off
		EnumBusbarShape shape = switch(face){
			case DOWN -> (direction == Direction.EAST || direction == Direction.WEST) != crouching ? EnumBusbarShape.INSULATORS_UP_EAST_WEST : EnumBusbarShape.INSULATORS_UP_NORTH_SOUTH;
			case UP -> (direction == Direction.EAST || direction == Direction.WEST) != crouching ? EnumBusbarShape.INSULATORS_DOWN_EAST_WEST : EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
			case NORTH -> crouching ? EnumBusbarShape.INSULATORS_SOUTH_EAST_WEST : EnumBusbarShape.INSULATORS_SOUTH_UP_DOWN;
			case EAST -> crouching ? EnumBusbarShape.INSULATORS_WEST_NORTH_SOUTH : EnumBusbarShape.INSULATORS_WEST_UP_DOWN;
			case SOUTH -> crouching ? EnumBusbarShape.INSULATORS_NORTH_EAST_WEST : EnumBusbarShape.INSULATORS_NORTH_UP_DOWN;
			case WEST -> crouching ? EnumBusbarShape.INSULATORS_EAST_NORTH_SOUTH : EnumBusbarShape.INSULATORS_EAST_UP_DOWN;
			default -> EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		};
		// @formatter:on
		
		return defaultBlockState().setValue(SHAPE, shape);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
		if(ExternalModContent.isIEHammer(pPlayer.getItemInHand(pHand))){
			EnumBusbarShape shape = pState.getValue(SHAPE);
			if(EnumBusbarShape.Type.STRAIGHT_SEGMENTS.contains(shape)){
				EnumBusbarShape newShape = null;
				
				// @formatter:off
				if(shape == EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH)newShape = EnumBusbarShape.FLOATING_DOWN_NORTH_SOUTH;
				if(shape == EnumBusbarShape.FLOATING_DOWN_NORTH_SOUTH)	newShape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
				if(shape == EnumBusbarShape.INSULATORS_DOWN_EAST_WEST)	newShape = EnumBusbarShape.FLOATING_DOWN_EAST_WEST;
				if(shape == EnumBusbarShape.FLOATING_DOWN_EAST_WEST)	newShape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
				// @formatter:on
				
				if(newShape != null && newShape != shape){
					pLevel.setBlock(pPos, pState.setValue(SHAPE, newShape), 3);
					return InteractionResult.SUCCESS;
				}
			}
			
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
	}
	
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving){
		if(!oldState.is(state.getBlock())){
			state = updateDir(level, pos, state, true);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block lastBlock, BlockPos fromPos, boolean isMoving){
		if(!level.isClientSide && level.getBlockState(pos).is(this)){
			state = updateDir(level, pos, state, false);
		}
	}
	
	private BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean placing){
		if(level.isClientSide){
			return state;
		}
		
		EnumBusbarShape shape = state.getValue(SHAPE);
		return new BusbarState(level, pos, state).place(placing, shape).getState();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		BusbarTileEntity te = OETileTypes.BUS.get().create(pPos, pState);
		return te;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType){
		return createTickerHelper(pLevel.isClientSide, pBlockEntityType, OETileTypes.BUS);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
		// TODO Cache them, compress and reuse duplicates
		
		// @formatter:off
		switch(pState.getValue(SHAPE)){
			case INSULATORS_UP_NORTH_SOUTH: case FLOATING_UP_NORTH_SOUTH: {
				return Shapes.create(0.125, 0.75, 0.0, 0.875, 1.0, 1.0);
			}
			case INSULATORS_DOWN_EAST_WEST: case FLOATING_DOWN_EAST_WEST:{
				return Shapes.create(0.0, 0.0, 0.125, 1.0, 0.25, 0.875);
			}
			case INSULATORS_UP_EAST_WEST: case FLOATING_UP_EAST_WEST:{
				return Shapes.create(0.0, 0.75, 0.125, 1.0, 1.0, 0.875);
			}
			case INSULATORS_DOWN_NORTH_SOUTH: case FLOATING_DOWN_NORTH_SOUTH:{
				return Shapes.create(0.125, 0.0, 0.0, 0.875, 0.25, 1.0);
			}
			
			case INSULATORS_NORTH_UP_DOWN: return Shapes.create(0.125, 0.0, 0.0, 0.875, 1.0, 0.25);
			case INSULATORS_EAST_UP_DOWN: return Shapes.create(0.75, 0.0, 0.125, 1.0, 1.0, 0.875);
			case INSULATORS_SOUTH_UP_DOWN: return Shapes.create(0.125, 0.0, 0.75, 0.875, 1.0, 1.0);
			case INSULATORS_WEST_UP_DOWN: return Shapes.create(0.0, 0.0, 0.125, 0.25, 1.0, 0.875);
			
			case BEND_DOWN_NORTH_EAST: return Shapes.create(0.125, 0.0, 0.0, 1.0, 0.25, 0.875);
			case BEND_DOWN_EAST_SOUTH: return Shapes.create(0.125, 0.0, 0.125, 1.0, 0.25, 1.0);
			case BEND_DOWN_SOUTH_WEST: return Shapes.create(0.0, 0.0, 0.125, 0.875, 0.25, 1.0);
			case BEND_DOWN_WEST_NORTH: return Shapes.create(0.0, 0.0, 0.0, 0.875, 0.25, 0.875);
			
			case BEND_UP_NORTH_EAST: return Shapes.create(0.125, 0.75, 0.0, 1.0, 1.0, 0.875);
			case BEND_UP_EAST_SOUTH: return Shapes.create(0.125, 0.75, 0.125, 1.0, 1.0, 1.0);
			case BEND_UP_SOUTH_WEST: return Shapes.create(0.0, 0.75, 0.125, 0.875, 1.0, 1.0);
			case BEND_UP_WEST_NORTH: return Shapes.create(0.0, 0.75, 0.0, 0.875, 1.0, 0.875);
			
			default:{
				return Shapes.create(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
			}
		}
		// @formatter:on
	}
	
	public static boolean isBusbar(Level level, BlockPos pos){
		return isBusbar(level.getBlockState(pos));
	}
	
	public static boolean isBusbar(BlockState state){
		return state.getBlock() instanceof BusbarBlock;
	}
}
