package twistedgate.overengineered.common.blocks.busbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
		boolean crouching = !(context.getPlayer() != null ? context.getPlayer().isCrouching() : false);
		
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
	public InteractionResult use(BlockState selfState, Level level, BlockPos selfPos, Player player, InteractionHand hand, BlockHitResult hit){
		ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(ExternalModContent.isIEWirecutter(held)){
			
			boolean updateShapesDebug = false;
			if(updateShapesDebug){
				if(!updateShapes)
					updateShapes = true;
				return InteractionResult.SUCCESS;
			}
			
			try{
				BlockPos posNorth = selfPos.relative(Direction.NORTH);
				BlockPos posEast = selfPos.relative(Direction.EAST);
				BlockPos posSouth = selfPos.relative(Direction.SOUTH);
				BlockPos posWest = selfPos.relative(Direction.WEST);
				
				BlockState stateNorth = level.getBlockState(posNorth);
				BlockState stateEast = level.getBlockState(posEast);
				BlockState stateSouth = level.getBlockState(posSouth);
				BlockState stateWest = level.getBlockState(posWest);
				
				boolean north = stateNorth.is(this) && stateNorth.getValue(SHAPE).pointsBack(level, selfPos, posNorth);
				boolean east = stateEast.is(this) && stateEast.getValue(SHAPE).pointsBack(level, selfPos, posEast);
				boolean south = stateSouth.is(this) && stateSouth.getValue(SHAPE).pointsBack(level, selfPos, posSouth);
				boolean west = stateWest.is(this) && stateWest.getValue(SHAPE).pointsBack(level, selfPos, posWest);
				
				final DustParticleOptions dst = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x00FF00)), 1.0F);
				
				if(north) level.addParticle(dst, posNorth.getX() + .5, posNorth.getY() + .5, posNorth.getZ() + .5, 0.0, 0.0, 0.0);
				if(east) level.addParticle(dst, posEast.getX() + .5, posEast.getY() + .5, posEast.getZ() + .5, 0.0, 0.0, 0.0);
				if(south) level.addParticle(dst, posSouth.getX() + .5, posSouth.getY() + .5, posSouth.getZ() + .5, 0.0, 0.0, 0.0);
				if(west) level.addParticle(dst, posWest.getX() + .5, posWest.getY() + .5, posWest.getZ() + .5, 0.0, 0.0, 0.0);
				
				if(!level.isClientSide){
					OverEngineered.log.info("North:\t{}", north);
					OverEngineered.log.info("East:\t{}", east);
					OverEngineered.log.info("South:\t{}", south);
					OverEngineered.log.info("West:\t{}", west);
				}
				
			}catch(Exception e){
				OverEngineered.log.info("Something went kaputt..", e);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		if(ExternalModContent.isIEHammer(held)){
			BlockPos lastPos = null;
			BlockPos currentPos = selfPos;
			
			retLabel: for(int i = 0;i < 32;i++){
				BlockState state = level.getBlockState(currentPos);
				if(state.getBlock() == this){
					EnumBusbarShape shape = state.getValue(SHAPE);
					
					List<BlockPos> list = new ArrayList<>();
					shape.connectionOffsets(list, currentPos);
					
					for(BlockPos p:list){
						if((lastPos == null || p != lastPos) && isBusbar(level, p)){
							if(p == selfPos)
								break retLabel;
							lastPos = currentPos;
							currentPos = p;
							break;
						}
					}
				}
			}
			
			DustParticleOptions src = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFF0000)), 1.0F);
			DustParticleOptions dst = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x00FF00)), 1.0F);
			
			level.addParticle(src, selfPos.getX() + .5, selfPos.getY() + .5, selfPos.getZ() + .5, 0.0, 0.0, 0.0);
			level.addParticle(dst, currentPos.getX() + .5, currentPos.getY() + .5, currentPos.getZ() + .5, 0.0, 0.0, 0.0);
			OverEngineered.log.info("End appears to be at {}", currentPos);
			
			return InteractionResult.SUCCESS;
		}
		
		if(ExternalModContent.isIEScrewdriver(held)){
			EnumBusbarShape selfShape = selfState.getValue(SHAPE);
			List<BlockPos> list = new ArrayList<>();
			selfShape.connectionOffsets(list, selfPos);
			
			DustParticleOptions dust = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFF7F00)), 1.0F);
			for(BlockPos p:list){
				level.addParticle(dust, p.getX() + .5, p.getY() + .5, p.getZ() + .5, 0.0, 0.0, 0.0);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		if(held.isEmpty()){
			EnumBusbarShape shape = selfState.getValue(SHAPE);
			
			List<BlockPos> connections;
			{
				List<BlockPos> list = new ArrayList<>();
				shape.connectionOffsets(list, selfPos);
				for(int i = 0;i < list.size();i++){
					BlockState state = level.getBlockState(list.get(i));
					if(state.getBlock() != this){
						list.remove(i--);
					}
				}
				connections = list;
			}
			
			DustParticleOptions dust = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0x0000FF)), 1.0F);
			for(int i = 0;i < connections.size();i++){
				BlockPos currentPos = connections.get(i);
				level.addParticle(dust, currentPos.getX() + .5, currentPos.getY() + .5, currentPos.getZ() + .5, 0.0, 0.0, 0.0);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block lastBlock, BlockPos fromPos, boolean isMoving){
		if(!level.isClientSide && level.getBlockState(pos).is(this)){
			change(state, level, pos);
		}
	}
	
	private void change(final BlockState selfState, final Level level, final BlockPos selfPos){
		final EnumBusbarShape selfShape = selfState.getValue(SHAPE);
		if(!selfShape.hasFreeConnectionPoint(level, selfPos))
			return;
		
		final LocalPlane plane = LocalPlane.get(selfShape, selfPos);
		
		final BlockState stateNorth = level.getBlockState(plane.a());
		final BlockState stateEast = level.getBlockState(plane.b());
		final BlockState stateSouth = level.getBlockState(plane.c());
		final BlockState stateWest = level.getBlockState(plane.d());
		
		boolean north = stateNorth.is(this) && stateNorth.getValue(SHAPE).pointsBack(level, selfPos, plane.a());
		boolean east = stateEast.is(this) && stateEast.getValue(SHAPE).pointsBack(level, selfPos, plane.b());
		boolean south = stateSouth.is(this) && stateSouth.getValue(SHAPE).pointsBack(level, selfPos, plane.c());
		boolean west = stateWest.is(this) && stateWest.getValue(SHAPE).pointsBack(level, selfPos, plane.d());
		
		level.setBlock(selfPos, selfState, 3);
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
	static Map<EnumBusbarShape, VoxelShape> SHAPES_CACHE = makeShapeCache();
	
	public static boolean updateShapes = false;
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
		if(updateShapes){
			SHAPES_CACHE = makeShapeCache();
			updateShapes = false;
		}
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
				
				case BEND_NORTH_DOWN_EAST -> Shapes.create(0.125, 0.125, 0.0, 1.0, 1.0, 0.25);
				case BEND_NORTH_EAST_UP -> Shapes.create(0.125, 0.0, 0.0, 1.0, 0.875, 0.25);
				case BEND_NORTH_UP_WEST -> Shapes.create(0.0, 0.125, 0.0, 0.875, 1.0, 0.25);
				case BEND_NORTH_WEST_DOWN -> Shapes.create(0.0, 0.125, 0.0, 0.875, 1.0, 0.25);
				
				case BEND_EAST_DOWN_SOUTH -> Shapes.create(0.75, 0.125, 0.0, 1.0, 1.0, 0.875);
				case BEND_EAST_SOUTH_UP -> Shapes.create(0.75, 0.125, 0.0, 1.0, 1.0, 0.875);
				case BEND_EAST_UP_NORTH -> Shapes.create(0.75, 0.125, 0.0, 1.0, 1.0, 0.875);
				case BEND_EAST_NORTH_DOWN -> Shapes.create(0.75, 0.125, 0.0, 1.0, 1.0, 0.875);
				
				case BEND_SOUTH_DOWN_WEST -> Shapes.create(0.125, 0.125, 0.75, 1.0, 1.0, 1.0);
				case BEND_SOUTH_WEST_UP -> Shapes.create(0.125, 0.125, 0.75, 1.0, 1.0, 1.0);
				case BEND_SOUTH_UP_EAST -> Shapes.create(0.125, 0.125, 0.75, 1.0, 1.0, 1.0);
				case BEND_SOUTH_EAST_DOWN -> Shapes.create(0.125, 0.125, 0.75, 1.0, 1.0, 1.0);
				
				case BEND_WEST_DOWN_NORTH -> Shapes.create(0.0, 0.125, 0.125, 0.25, 1.0, 1.0);
				case BEND_WEST_NORTH_UP -> Shapes.create(0.0, 0.125, 0.125, 0.25, 1.0, 1.0);
				case BEND_WEST_UP_SOUTH -> Shapes.create(0.0, 0.125, 0.125, 0.25, 1.0, 1.0);
				case BEND_WEST_SOUTH_DOWN -> Shapes.create(0.0, 0.125, 0.125, 0.25, 1.0, 1.0);
				
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
	
	private static record LocalPlane(Direction facing, BlockPos a, BlockPos b, BlockPos c, BlockPos d){
		public static LocalPlane get(@Nonnull EnumBusbarShape shape, @Nonnull BlockPos origin){
			Objects.requireNonNull(shape, "Shape should not be Null");
			Objects.requireNonNull(origin, "Origin should not be Null");
			
			BlockPos north, east, south, west;
			switch(shape.facing){
				case DOWN, UP -> {
					north = origin.relative(Direction.NORTH);
					east = origin.relative(Direction.EAST);
					south = origin.relative(Direction.SOUTH);
					west = origin.relative(Direction.WEST);
				}
				case NORTH -> {
					north = origin.relative(Direction.UP);
					east = origin.relative(Direction.EAST);
					south = origin.relative(Direction.DOWN);
					west = origin.relative(Direction.WEST);
				}
				case EAST -> {
					north = origin.relative(Direction.UP);
					east = origin.relative(Direction.SOUTH);
					south = origin.relative(Direction.DOWN);
					west = origin.relative(Direction.NORTH);
				}
				case SOUTH -> {
					north = origin.relative(Direction.UP);
					east = origin.relative(Direction.WEST);
					south = origin.relative(Direction.DOWN);
					west = origin.relative(Direction.EAST);
				}
				case WEST -> {
					north = origin.relative(Direction.UP);
					east = origin.relative(Direction.NORTH);
					south = origin.relative(Direction.DOWN);
					west = origin.relative(Direction.SOUTH);
				}
				default -> throw new IllegalArgumentException(shape.facing + " is an unknown Direction");
			}
			
			return new LocalPlane(shape.facing, north, east, south, west);
		}
		
		private LocalPlane(Direction facing, BlockPos a, BlockPos b, BlockPos c, BlockPos d){
			this.facing = facing;
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}
	
	public static class Item extends BlockItem{
		public Item(BusbarBlock block){
			super(block, new Item.Properties().tab(OverEngineered.creativeTab));
		}
		
		@Override
		protected boolean placeBlock(BlockPlaceContext pContext, BlockState state){
			final Level level = pContext.getLevel();
			final BlockPos pos = pContext.getClickedPos();
			
			if(state.getBlock() instanceof BusbarBlock bus){
				if(!level.isClientSide())
					bus.change(state, level, pos);
				return true;
			}
			
			return super.placeBlock(pContext, state);
		}
	}
}
