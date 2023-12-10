package twistedgate.overengineered.common.blocks.busbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import twistedgate.overengineered.OverEngineered;
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
	public BlockState getStateForPlacement(BlockPlaceContext context){
		Direction facing = context.getClickedFace();
		Direction direction = context.getHorizontalDirection();
		boolean crouching = context.getPlayer() != null ? context.getPlayer().isCrouching() : false;
		
		// @formatter:off
		EnumBusbarShape shape = switch(facing){
			case DOWN -> (direction == Direction.EAST || direction == Direction.WEST) != crouching ? EnumBusbarShape.INSULATORS_UP_EAST_WEST : EnumBusbarShape.INSULATORS_UP_NORTH_SOUTH;
			case UP -> (direction == Direction.EAST || direction == Direction.WEST) != crouching ? EnumBusbarShape.INSULATORS_DOWN_EAST_WEST : EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
			case NORTH -> crouching ? EnumBusbarShape.INSULATORS_SOUTH_EAST_WEST : EnumBusbarShape.INSULATORS_SOUTH_UP_DOWN;
			case EAST -> crouching ? EnumBusbarShape.INSULATORS_WEST_NORTH_SOUTH : EnumBusbarShape.INSULATORS_WEST_UP_DOWN;
			case SOUTH -> crouching ? EnumBusbarShape.INSULATORS_NORTH_EAST_WEST : EnumBusbarShape.INSULATORS_NORTH_UP_DOWN;
			case WEST -> crouching ? EnumBusbarShape.INSULATORS_EAST_NORTH_SOUTH : EnumBusbarShape.INSULATORS_EAST_UP_DOWN;
			default -> EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		};
		// @formatter:on
		
		return this.defaultBlockState().setValue(SHAPE, shape);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
		ItemStack held = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(ExternalModContent.isIEHammer(held)){
			BlockPos lastPos = null;
			BlockPos currentPos = pPos;
			
			retLabel:for(int i = 0;i < 32;i++){
				BlockState state = pLevel.getBlockState(currentPos);
				if(state.getBlock() == this){
					EnumBusbarShape shape = state.getValue(SHAPE);
					
					List<BlockPos> list = new ArrayList<>();
					shape.connectionOffsets(list, currentPos);
					
					for(BlockPos p:list){
						if((lastPos == null || p != lastPos) && isBusbar(pLevel, p)){
							if(p == pPos) break retLabel;
							lastPos = currentPos;
							currentPos = p;
							break;
						}
					}
				}
			}

			DustParticleOptions src = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFF0000)), 1.0F);
			DustParticleOptions dst = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x00FF00)), 1.0F);

			pLevel.addParticle(src, pPos.getX() + .5, pPos.getY() + .5, pPos.getZ() + .5, 0.0, 0.0, 0.0);
			pLevel.addParticle(dst, currentPos.getX() + .5, currentPos.getY() + .5, currentPos.getZ() + .5, 0.0, 0.0, 0.0);
			OverEngineered.log.info("End appears to be at {}", currentPos);
			
			return InteractionResult.SUCCESS;
		}
		
		if(ExternalModContent.isIEScrewdriver(held)){
			final BusbarCon bus = BusbarCon.create(pLevel, pPos, pState);
			if(bus != null){
				bus.removeExistingConnections();
				OverEngineered.log.info("connections: {}", bus.freePoints.size());
				
				DustParticleOptions dust = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x00FF00)), 1.0F);
				for(BlockPos p:bus.freePoints){
					pLevel.addParticle(dust, p.getX() + .5, p.getY() + .5, p.getZ() + .5, 0.0, 0.0, 0.0);
				}
			}
			
			return InteractionResult.SUCCESS;
		}
		
		if(held.isEmpty()){
			EnumBusbarShape shape = pState.getValue(SHAPE);
			
			List<BlockPos> connections;
			{
				List<BlockPos> list = new ArrayList<>();
				shape.connectionOffsets(list, pPos);
				for(int i = 0;i < list.size();i++){
					BlockState state = pLevel.getBlockState(list.get(i));
					if(state.getBlock() != this){
						list.remove(i--);
					}
				}
				connections = list;
			}
			
			DustParticleOptions dust = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x0000FF)), 1.0F);
			for(int i = 0;i < connections.size();i++){
				BlockPos currentPos = connections.get(i);
				pLevel.addParticle(dust, currentPos.getX() + .5, currentPos.getY() + .5, currentPos.getZ() + .5, 0.0, 0.0, 0.0);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
	}
	
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving){
		if(!oldState.is(state.getBlock())){
			final BusbarCon bus = BusbarCon.create(level, pos, state);
			if(bus != null){
				bus.onPlace(true);
			}
			
//			state = updateDir(level, pos, state, true);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block lastBlock, BlockPos fromPos, boolean isMoving){
		if(!level.isClientSide && level.getBlockState(pos).is(this)){
//			state = updateDir(level, pos, state, false);
		}
	}
	
	@Deprecated
	@SuppressWarnings("unused")
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
	
	static final VoxelShape MISSING_SHAPE = Shapes.create(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
	static final Map<EnumBusbarShape, VoxelShape> SHAPES_CACHE = makeShapeCache();
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
		return SHAPES_CACHE.getOrDefault(pState.getValue(SHAPE), MISSING_SHAPE);
	}
	
	private static Map<EnumBusbarShape, VoxelShape> makeShapeCache(){
		Map<EnumBusbarShape, VoxelShape> map = new HashMap<>();
		for(EnumBusbarShape shape:EnumBusbarShape.values()){
			
			// @formatter:off
			VoxelShape vShape = switch(shape){
				case INSULATORS_DOWN_NORTH_SOUTH -> Shapes.create(0.125, 0.0, 0.0, 0.875, 0.25, 1.0);
				case INSULATORS_DOWN_EAST_WEST -> Shapes.create(0.0, 0.0, 0.125, 1.0, 0.25, 0.875);
				case INSULATORS_UP_NORTH_SOUTH -> Shapes.create(0.125, 0.75, 0.0, 0.875, 1.0, 1.0);
				case INSULATORS_UP_EAST_WEST -> Shapes.create(0.0, 0.75, 0.125, 1.0, 1.0, 0.875);
				
				case INSULATORS_NORTH_UP_DOWN -> Shapes.create(0.125, 0.0, 0.0, 0.875, 1.0, 0.25);
				case INSULATORS_EAST_UP_DOWN -> Shapes.create(0.75, 0.0, 0.125, 1.0, 1.0, 0.875);
				case INSULATORS_SOUTH_UP_DOWN -> Shapes.create(0.125, 0.0, 0.75, 0.875, 1.0, 1.0);
				case INSULATORS_WEST_UP_DOWN -> Shapes.create(0.0, 0.0, 0.125, 0.25, 1.0, 0.875);
				
				case INSULATORS_NORTH_EAST_WEST -> Shapes.create(0.0, 0.125, 0.0, 1.0, 0.875, 0.25);
				case INSULATORS_SOUTH_EAST_WEST -> Shapes.create(0.0, 0.125, 0.75, 1.0, 0.875, 1.0);
				case INSULATORS_EAST_NORTH_SOUTH -> Shapes.create(0.75, 0.125, 0.0, 1.0, 0.875, 1.0);
				case INSULATORS_WEST_NORTH_SOUTH -> Shapes.create(0.0, 0.125, 0.0, 0.25, 0.875, 1.0);
				
				case EDGE_INSIDE_DOWN_NORTH_UP_SOUTH -> Shapes.or(Shapes.create(0.125, 0.0, 0.0, 0.875, 0.25, 1.0), Shapes.create(0.125, 0.0, 0.0, 0.875, 1.0, 0.25)).optimize();
				case EDGE_INSIDE_DOWN_SOUTH_UP_NORTH -> Shapes.or(Shapes.create(0.125, 0.0, 0.0, 0.875, 0.25, 1.0), Shapes.create(0.125, 0.0, 0.75, 0.875, 1.0, 1.0)).optimize();
				case EDGE_INSIDE_DOWN_EAST_UP_WEST -> Shapes.or(Shapes.create(0.0, 0.0, 0.125, 1.0, 0.25, 0.875), Shapes.create(0.75, 0.0, 0.125, 1.0, 1.0, 0.875)).optimize();
				case EDGE_INSIDE_DOWN_WEST_UP_EAST -> Shapes.or(Shapes.create(0.0, 0.0, 0.125, 1.0, 0.25, 0.875), Shapes.create(0.0, 0.0, 0.125, 0.25, 1.0, 0.875)).optimize();
				
				case EDGE_INSIDE_UP_NORTH_DOWN_SOUTH -> Shapes.or(Shapes.create(0.125, 0.75, 0.0, 0.875, 1.0, 1.0), Shapes.create(0.125, 0.0, 0.0, 0.875, 1.0, 0.25)).optimize();
				case EDGE_INSIDE_UP_SOUTH_DOWN_NORTH -> Shapes.or(Shapes.create(0.125, 0.75, 0.0, 0.875, 1.0, 1.0), Shapes.create(0.125, 0.0, 0.75, 0.875, 1.0, 1.0)).optimize();
				case EDGE_INSIDE_UP_EAST_DOWN_WEST -> Shapes.or(Shapes.create(0.0, 0.75, 0.125, 1.0, 1.0, 0.875), Shapes.create(0.75, 0.0, 0.125, 1.0, 1.0, 0.875)).optimize();
				case EDGE_INSIDE_UP_WEST_DOWN_EAST -> Shapes.or(Shapes.create(0.0, 0.75, 0.125, 1.0, 1.0, 0.875), Shapes.create(0.0, 0.0, 0.125, 0.25, 1.0, 0.875)).optimize();
				
				case BEND_DOWN_NORTH_EAST -> Shapes.create(0.125, 0.0, 0.0, 1.0, 0.25, 0.875);
				case BEND_DOWN_EAST_SOUTH -> Shapes.create(0.125, 0.0, 0.125, 1.0, 0.25, 1.0);
				case BEND_DOWN_SOUTH_WEST -> Shapes.create(0.0, 0.0, 0.125, 0.875, 0.25, 1.0);
				case BEND_DOWN_WEST_NORTH -> Shapes.create(0.0, 0.0, 0.0, 0.875, 0.25, 0.875);
				
				case BEND_UP_NORTH_EAST -> Shapes.create(0.125, 0.75, 0.0, 1.0, 1.0, 0.875);
				case BEND_UP_EAST_SOUTH -> Shapes.create(0.125, 0.75, 0.125, 1.0, 1.0, 1.0);
				case BEND_UP_SOUTH_WEST -> Shapes.create(0.0, 0.75, 0.125, 0.875, 1.0, 1.0);
				case BEND_UP_WEST_NORTH -> Shapes.create(0.0, 0.75, 0.0, 0.875, 1.0, 0.875);
				
				case BEND_NORTH_DOWN_EAST, BEND_EAST_DOWN_SOUTH, BEND_SOUTH_DOWN_WEST, BEND_WEST_DOWN_NORTH -> Shapes.create(0.0, 0.125, 0.0, 0.875, 1.0, 0.25);
				case BEND_NORTH_EAST_UP, BEND_EAST_SOUTH_UP, BEND_SOUTH_WEST_UP, BEND_WEST_NORTH_UP -> Shapes.create(0.75, 0.125, 0.0, 1.0, 1.0, 0.875);
				case BEND_NORTH_UP_WEST, BEND_EAST_UP_NORTH, BEND_SOUTH_UP_EAST, BEND_WEST_UP_SOUTH -> Shapes.create(0.125, 0.125, 0.75, 1.0, 1.0, 1.0);
				case BEND_NORTH_WEST_DOWN, BEND_EAST_NORTH_DOWN, BEND_SOUTH_EAST_DOWN, BEND_WEST_SOUTH_DOWN -> Shapes.create(0.0, 0.125, 0.125, 0.25, 1.0, 1.0);
				
				case EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH -> Shapes.create(0.125, 0.0, 0.75, 0.875, 0.25, 1.0);
				case EDGE_OUTSIDE_DOWN_EAST_UP_WEST -> Shapes.create(0.0, 0.0, 0.125, 0.25, 0.25, 0.875);
				case EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH -> Shapes.create(0.125, 0.0, 0.0, 0.875, 0.25, 0.25);
				case EDGE_OUTSIDE_DOWN_WEST_UP_EAST -> Shapes.create(0.75, 0.0, 0.125, 1.0, 0.25, 0.875);
				
				case EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH -> Shapes.create(0.125, 0.75, 0.0, 0.875, 1.0, 0.25);
				case EDGE_OUTSIDE_UP_EAST_DOWN_WEST -> Shapes.create(0.75, 0.75, 0.125, 1.0, 1.0, 0.875);
				case EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH -> Shapes.create(0.125, 0.75, 0.75, 0.875, 1.0, 1.0);
				case EDGE_OUTSIDE_UP_WEST_DOWN_EAST -> Shapes.create(0.0, 0.75, 0.125, 0.25, 1.0, 0.875);
				
				default -> null;
			};
			// @formatter:on
			
			if(vShape != null){
				map.put(shape, vShape);
			}else{
				OverEngineered.log.info("Missing voxelshape for shape \"" + shape.toString().toLowerCase() + "\"");
				continue;
			}
		}
		
		return map;
	}
	
	public static boolean isBusbar(Level level, BlockPos pos){
		return isBusbar(level.getBlockState(pos));
	}
	
	public static boolean isBusbar(BlockState state){
		return state.getBlock() instanceof BusbarBlock;
	}
}
