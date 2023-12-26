package twistedgate.overengineered.utils.enums;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twistedgate.overengineered.common.blocks.busbar.BusbarBlock;

/**
 * @author TwistedGate
 */
public enum EnumBusbarShape implements StringRepresentable{
	// @formatter:off
	// Straights with Insulators (FACING_FROM_TO)
	INSULATORS_DOWN_NORTH_SOUTH(DOWN, Connections.NORTH_SOUTH,	new Vec3i(0, 0, 0)),
	INSULATORS_DOWN_EAST_WEST(DOWN, Connections.EAST_WEST,		new Vec3i(0, 90, 0)),
	INSULATORS_UP_NORTH_SOUTH(UP, Connections.NORTH_SOUTH,		new Vec3i(180, 0, 0)),
	INSULATORS_UP_EAST_WEST(UP, Connections.EAST_WEST,			new Vec3i(180, 90, 0)),
	
	INSULATORS_NORTH_UP_DOWN(NORTH, Connections.UP_DOWN,		new Vec3i(90, 0, 0)),
	INSULATORS_EAST_UP_DOWN(EAST, Connections.UP_DOWN,			new Vec3i(90, 270, 0)),
	INSULATORS_SOUTH_UP_DOWN(SOUTH, Connections.UP_DOWN,		new Vec3i(90, 180, 0)),
	INSULATORS_WEST_UP_DOWN(WEST, Connections.UP_DOWN,			new Vec3i(90, 90, 0)),
	INSULATORS_NORTH_EAST_WEST(NORTH, Connections.EAST_WEST,	new Vec3i(90, 0, 90)),
	INSULATORS_EAST_NORTH_SOUTH(EAST, Connections.NORTH_SOUTH,	new Vec3i(90, 270, 90)),
	INSULATORS_SOUTH_EAST_WEST(SOUTH, Connections.EAST_WEST,	new Vec3i(90, 180, 90)),
	INSULATORS_WEST_NORTH_SOUTH(WEST, Connections.NORTH_SOUTH,	new Vec3i(90, 90, 90)),
	
	// 90° Bends (FACING_FROM_TO)
	BEND_DOWN_NORTH_EAST(DOWN, Connections.NORTH_EAST,			new Vec3i(0, 270, 0)),
	BEND_DOWN_EAST_SOUTH(DOWN, Connections.EAST_SOUTH,			new Vec3i(0, 180, 0)),
	BEND_DOWN_SOUTH_WEST(DOWN, Connections.SOUTH_WEST,			new Vec3i(0, 90, 0)),
	BEND_DOWN_WEST_NORTH(DOWN, Connections.WEST_NORTH,			new Vec3i(0, 0, 0)),
	
	BEND_UP_NORTH_EAST(UP, Connections.NORTH_EAST,				new Vec3i(180, 180, 0)),
	BEND_UP_EAST_SOUTH(UP, Connections.EAST_SOUTH,				new Vec3i(180, 90, 0)),
	BEND_UP_SOUTH_WEST(UP, Connections.SOUTH_WEST,				new Vec3i(180, 0, 0)),
	BEND_UP_WEST_NORTH(UP, Connections.WEST_NORTH,				new Vec3i(180, 270, 0)),
	
	BEND_NORTH_DOWN_EAST(NORTH, Connections.DOWN_EAST,			new Vec3i(90, 0, 180)),
	BEND_NORTH_EAST_UP(NORTH, Connections.UP_EAST,				new Vec3i(90, 0, 270)),
	BEND_NORTH_UP_WEST(NORTH, Connections.UP_WEST,				new Vec3i(90, 0, 0)),
	BEND_NORTH_WEST_DOWN(NORTH, Connections.DOWN_WEST,			new Vec3i(90, 0, 90)),
	
	BEND_EAST_DOWN_SOUTH(EAST, Connections.DOWN_SOUTH,			new Vec3i(270, 90, 0)),
	BEND_EAST_SOUTH_UP(EAST, Connections.UP_SOUTH,				new Vec3i(270, 90, 270)),
	BEND_EAST_UP_NORTH(EAST, Connections.UP_NORTH,				new Vec3i(270, 90, 180)),
	BEND_EAST_NORTH_DOWN(EAST, Connections.DOWN_NORTH,			new Vec3i(270, 90, 90)),
	
	BEND_SOUTH_DOWN_WEST(SOUTH, Connections.DOWN_WEST,			new Vec3i(270, 0, 0)),
	BEND_SOUTH_WEST_UP(SOUTH, Connections.UP_WEST,				new Vec3i(270, 0, 270)),
	BEND_SOUTH_UP_EAST(SOUTH, Connections.UP_EAST,				new Vec3i(270, 0, 180)),
	BEND_SOUTH_EAST_DOWN(SOUTH, Connections.DOWN_EAST,			new Vec3i(270, 0, 90)),
	
	BEND_WEST_DOWN_NORTH(WEST, Connections.DOWN_NORTH,			new Vec3i(270, 270, 0)),
	BEND_WEST_NORTH_UP(WEST, Connections.UP_NORTH,				new Vec3i(270, 270, 270)),
	BEND_WEST_UP_SOUTH(WEST, Connections.UP_SOUTH,				new Vec3i(270, 270, 180)),
	BEND_WEST_SOUTH_DOWN(WEST, Connections.DOWN_SOUTH,			new Vec3i(270, 270, 90)),
	
	// Inside Edges (FACING_SIDE_FROM_TO)
	EDGE_INSIDE_DOWN_NORTH_UP_SOUTH	(DOWN, Connections.UP_SOUTH,new Vec3i(0, 180, 0)),
	EDGE_INSIDE_DOWN_EAST_UP_WEST	(DOWN, Connections.UP_WEST,	new Vec3i(0, 90, 0)),
	EDGE_INSIDE_DOWN_SOUTH_UP_NORTH	(DOWN, Connections.UP_NORTH,new Vec3i(0, 0, 0)),
	EDGE_INSIDE_DOWN_WEST_UP_EAST	(DOWN, Connections.UP_EAST,	new Vec3i(0, 270, 0)),
	EDGE_INSIDE_UP_NORTH_DOWN_SOUTH	(UP, Connections.DOWN_SOUTH,new Vec3i(180, 0, 0)),
	EDGE_INSIDE_UP_EAST_DOWN_WEST	(UP, Connections.DOWN_WEST,	new Vec3i(180, 270, 0)),
	EDGE_INSIDE_UP_SOUTH_DOWN_NORTH	(UP, Connections.DOWN_NORTH,new Vec3i(180, 180, 0)),
	EDGE_INSIDE_UP_WEST_DOWN_EAST	(UP, Connections.DOWN_EAST,	new Vec3i(180, 90, 0)),
	
	// Outside Edges (FACING_SIDE_FROM_TO)
	EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH(DOWN, Connections.UP_SOUTH,new Vec3i(0, 0, 0)),
	EDGE_OUTSIDE_DOWN_EAST_UP_WEST	(DOWN, Connections.UP_WEST,	new Vec3i(0, 270, 0)),
	EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH(DOWN, Connections.UP_NORTH,new Vec3i(0, 180, 0)),
	EDGE_OUTSIDE_DOWN_WEST_UP_EAST	(DOWN, Connections.UP_EAST,	new Vec3i(0, 90, 0)),
	EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH(UP, Connections.DOWN_SOUTH,new Vec3i(270, 180, 0)),
	EDGE_OUTSIDE_UP_EAST_DOWN_WEST	(UP, Connections.DOWN_WEST,	new Vec3i(270, 90, 0)),
	EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH(UP, Connections.DOWN_NORTH,new Vec3i(270, 0, 0)),
	EDGE_OUTSIDE_UP_WEST_DOWN_EAST	(UP, Connections.DOWN_EAST,	new Vec3i(270, 270, 0)),
	;
	// @formatter:on
	
	private final String serialname;
	public final Direction facing;
	public final Connections connections;
	private final Consumer<PoseStack> rotations;
	private EnumBusbarShape(Direction facing, Connections cons){
		this.serialname = name().toLowerCase();
		this.facing = facing;
		this.connections = cons;
		this.rotations = prepareRotations(null);
	}
	/**
	 * @param facing        - Effectively which side of the Block it attaches to.<br>
	 *                      (<code>North</code> meaning it attaches to the <code>South-Face</code>)
	 * @param cons          - Possible connection-sides
	 * @param modelRotation - How much to rotate by each axis. (Rotation order: YZX)
	 */
	private EnumBusbarShape(Direction facing, Connections cons, Vec3i modelRotation){
		this.serialname = name().toLowerCase();
		this.facing = facing;
		this.connections = cons;
		this.rotations = prepareRotations(modelRotation);
	}
	
	private static final BiConsumer<PoseStack, Quaternion> mulPose = (p, q) -> p.mulPose(q);
	private Consumer<PoseStack> prepareRotations(Vec3i modelRotation){
		final Consumer<PoseStack> doNothing = p -> {};
		
		if(modelRotation == null || (modelRotation != null && modelRotation.equals(Vec3i.ZERO)))
			return doNothing;
		
		final Quaternion qX = new Quaternion(Vector3f.XP, modelRotation.getX(), true);
		final Quaternion qY = new Quaternion(Vector3f.YP, modelRotation.getY(), true);
		final Quaternion qZ = new Quaternion(Vector3f.ZP, modelRotation.getZ(), true);
		
		final Consumer<PoseStack> x = modelRotation.getX() != 0 ? p -> mulPose.accept(p, qX) : doNothing;
		final Consumer<PoseStack> y = modelRotation.getY() != 0 ? p -> mulPose.accept(p, qY) : doNothing;
		final Consumer<PoseStack> z = modelRotation.getZ() != 0 ? p -> mulPose.accept(p, qZ) : doNothing;
		
		Objects.requireNonNull(x, String.format("Consumer for X is Null! (%s)", this));
		Objects.requireNonNull(y, String.format("Consumer for Y is Null! (%s)", this));
		Objects.requireNonNull(z, String.format("Consumer for Z is Null! (%s)", this));
		
		return(matrix -> {
			y.accept(matrix);
			z.accept(matrix);
			x.accept(matrix);
		});
	}
	
	@OnlyIn(Dist.CLIENT)
	public void applyModelRotation(PoseStack matrix){
		this.rotations.accept(matrix);
	}
	
	public boolean hasFreeConnectionPoint(Level level, BlockPos pos){
		boolean ret = false;
		for(Direction d:this.connections.points){
			BlockState state = level.getBlockState(pos.relative(d));
			if(!(state.getBlock() instanceof BusbarBlock)){
				ret |= true;
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param level
	 * @param source Position from which this method was called
	 * @param selfPos The current position
	 * @return if the current shape points back to source
	 */
	public boolean pointsBack(Level level, BlockPos source, BlockPos selfPos){
		BlockState state = level.getBlockState(selfPos);
		if(!(state.getBlock() instanceof BusbarBlock))
			return false;
		
		// Do any of the points, point back to source?
		for(Direction p:this.connections.points){
			if(selfPos.relative(p).equals(source))
				return true;
		}
		
		return false;
	}
	
	public void connectionOffsets(final List<BlockPos> list, BlockPos pos){
		for(Direction p:this.connections.points){
			list.add(pos.relative(p));
		}
	}
	
	@Override
	public String getSerializedName(){
		return this.serialname;
	}
	
	public boolean compatibleWith(EnumBusbarShape other){
		return this.connections.compatibleWith(other.connections);
	}
	
	// STATIC STUFF
	
	private static boolean checkBothWaysSided(Direction side, Direction from, Direction to, Direction a, Direction b, Direction c){
		return side == a && checkBothWays(from, to, a, b);
	}
	
	private static boolean checkBothWays(Direction from, Direction to, Direction a, Direction b){
		return (from == a && to == b) || (from == b && to == a);
	}
	
	@Nullable
	public static EnumBusbarShape get(Direction facing, Direction from, Direction to){
		return get(facing, null, from, to);
	}
	
	@Nullable
	public static EnumBusbarShape get(Direction facing, @Nullable Direction side, Direction from, Direction to){
		Objects.requireNonNull(facing, "\"Facing\" must not be Null.");
		Objects.requireNonNull(from, "\"From\" must not be Null.");
		Objects.requireNonNull(to, "\"To\" must not be Null.");
		
		// Straights with Insulators (FACING_FROM_TO)
		switch(facing){
			case UP -> {
				if(checkBothWays(from, to, NORTH, SOUTH)) return INSULATORS_UP_NORTH_SOUTH;
				if(checkBothWays(from, to, EAST, WEST)) return INSULATORS_UP_EAST_WEST;
			}
			case DOWN -> {
				if(checkBothWays(from, to, NORTH, SOUTH)) return INSULATORS_DOWN_NORTH_SOUTH;
				if(checkBothWays(from, to, EAST, WEST)) return INSULATORS_DOWN_EAST_WEST;
			}
			case NORTH -> {
				if(checkBothWays(from, to, UP, DOWN)) return INSULATORS_NORTH_UP_DOWN;
				if(checkBothWays(from, to, EAST, WEST)) return INSULATORS_NORTH_EAST_WEST;
			}
			case EAST -> {
				if(checkBothWays(from, to, UP, DOWN)) return INSULATORS_EAST_UP_DOWN;
				if(checkBothWays(from, to, NORTH, SOUTH)) return INSULATORS_EAST_NORTH_SOUTH;
			}
			case SOUTH -> {
				if(checkBothWays(from, to, UP, DOWN)) return INSULATORS_SOUTH_UP_DOWN;
				if(checkBothWays(from, to, EAST, WEST)) return INSULATORS_SOUTH_EAST_WEST;
			}
			case WEST -> {
				if(checkBothWays(from, to, UP, DOWN)) return INSULATORS_WEST_UP_DOWN;
				if(checkBothWays(from, to, NORTH, SOUTH)) return INSULATORS_WEST_NORTH_SOUTH;
			}
		}
		
		// 90° Bends (FACING_FROM_TO)
		switch(facing){
			case UP -> {
				if(checkBothWays(from, to, NORTH, EAST)) return BEND_UP_NORTH_EAST;
				if(checkBothWays(from, to, EAST, SOUTH)) return BEND_UP_EAST_SOUTH;
				if(checkBothWays(from, to, SOUTH, WEST)) return BEND_UP_SOUTH_WEST;
				if(checkBothWays(from, to, WEST, NORTH)) return BEND_UP_WEST_NORTH;
			}
			case DOWN -> {
				if(checkBothWays(from, to, NORTH, EAST)) return BEND_DOWN_NORTH_EAST;
				if(checkBothWays(from, to, EAST, SOUTH)) return BEND_DOWN_EAST_SOUTH;
				if(checkBothWays(from, to, SOUTH, WEST)) return BEND_DOWN_SOUTH_WEST;
				if(checkBothWays(from, to, WEST, NORTH)) return BEND_DOWN_WEST_NORTH;
			}
			case NORTH -> {
				if(checkBothWays(from, to, DOWN, EAST)) return BEND_NORTH_DOWN_EAST;
				if(checkBothWays(from, to, EAST, UP)) return BEND_NORTH_EAST_UP;
				if(checkBothWays(from, to, UP, WEST)) return BEND_NORTH_UP_WEST;
				if(checkBothWays(from, to, WEST, DOWN)) return BEND_NORTH_WEST_DOWN;
			}
			case EAST -> {
				if(checkBothWays(from, to, DOWN, SOUTH)) return BEND_EAST_DOWN_SOUTH;
				if(checkBothWays(from, to, SOUTH, UP)) return BEND_EAST_SOUTH_UP;
				if(checkBothWays(from, to, UP, NORTH)) return BEND_EAST_UP_NORTH;
				if(checkBothWays(from, to, NORTH, DOWN)) return BEND_EAST_NORTH_DOWN;
			}
			case SOUTH -> {
				if(checkBothWays(from, to, DOWN, WEST)) return BEND_SOUTH_DOWN_WEST;
				if(checkBothWays(from, to, WEST, UP)) return BEND_SOUTH_WEST_UP;
				if(checkBothWays(from, to, UP, EAST)) return BEND_SOUTH_UP_EAST;
				if(checkBothWays(from, to, EAST, DOWN)) return BEND_SOUTH_EAST_DOWN;
			}
			case WEST -> {
				if(checkBothWays(from, to, DOWN, NORTH)) return BEND_WEST_DOWN_NORTH;
				if(checkBothWays(from, to, NORTH, UP)) return BEND_WEST_NORTH_UP;
				if(checkBothWays(from, to, UP, SOUTH)) return BEND_WEST_UP_SOUTH;
				if(checkBothWays(from, to, SOUTH, DOWN)) return BEND_WEST_SOUTH_DOWN;
			}
		}
		
		// Inside/Outside Edges (FACING_SIDE_FROM_TO)
		if(side != null){
			switch(facing){
				case UP -> {
					if(checkBothWaysSided(side, from, to, NORTH, DOWN, SOUTH)) return EDGE_INSIDE_UP_NORTH_DOWN_SOUTH;
					if(checkBothWaysSided(side, from, to, EAST, DOWN, WEST)) return EDGE_INSIDE_UP_EAST_DOWN_WEST;
					if(checkBothWaysSided(side, from, to, SOUTH, DOWN, NORTH)) return EDGE_INSIDE_UP_SOUTH_DOWN_NORTH;
					if(checkBothWaysSided(side, from, to, WEST, DOWN, EAST)) return EDGE_INSIDE_UP_WEST_DOWN_EAST;
					
					if(checkBothWaysSided(side, from, to, NORTH, UP, SOUTH)) return EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH;
					if(checkBothWaysSided(side, from, to, EAST, UP, WEST)) return EDGE_OUTSIDE_DOWN_EAST_UP_WEST;
					if(checkBothWaysSided(side, from, to, SOUTH, UP, NORTH)) return EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH;
					if(checkBothWaysSided(side, from, to, WEST, UP, EAST)) return EDGE_OUTSIDE_DOWN_WEST_UP_EAST;
				}
				case DOWN -> {
					if(checkBothWaysSided(side, from, to, NORTH, UP, SOUTH)) return EDGE_INSIDE_DOWN_NORTH_UP_SOUTH;
					if(checkBothWaysSided(side, from, to, EAST, UP, WEST)) return EDGE_INSIDE_DOWN_EAST_UP_WEST;
					if(checkBothWaysSided(side, from, to, SOUTH, UP, NORTH)) return EDGE_INSIDE_DOWN_SOUTH_UP_NORTH;
					if(checkBothWaysSided(side, from, to, WEST, UP, EAST)) return EDGE_INSIDE_DOWN_WEST_UP_EAST;
					
					if(checkBothWaysSided(side, from, to, NORTH, DOWN, SOUTH)) return EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH;
					if(checkBothWaysSided(side, from, to, EAST, DOWN, WEST)) return EDGE_OUTSIDE_UP_EAST_DOWN_WEST;
					if(checkBothWaysSided(side, from, to, SOUTH, DOWN, NORTH)) return EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH;
					if(checkBothWaysSided(side, from, to, WEST, DOWN, EAST)) return EDGE_OUTSIDE_UP_WEST_DOWN_EAST;
				}
				case NORTH, EAST, SOUTH, WEST -> {
				}
			}
		}
		
		return null;
	}
	
	public static enum Connections{
		NORTH_SOUTH(NORTH, SOUTH),
		EAST_WEST(EAST, WEST),
		UP_DOWN(UP, DOWN),
		
		NORTH_EAST(NORTH, EAST),
		EAST_SOUTH(EAST, SOUTH),
		SOUTH_WEST(SOUTH, WEST),
		WEST_NORTH(WEST, NORTH),
		
		UP_NORTH(UP, NORTH),
		UP_EAST(UP, EAST),
		UP_SOUTH(UP, SOUTH),
		UP_WEST(UP, WEST),
		
		DOWN_NORTH(DOWN, NORTH),
		DOWN_EAST(DOWN, EAST),
		DOWN_SOUTH(DOWN, SOUTH),
		DOWN_WEST(DOWN, WEST)
		;
		
		final Direction[] points;
		private Connections(Direction... points){
			this.points = points;
		}
		
		public boolean compatibleWith(Connections other){
			return this.points[0] == other.points[1] || this.points[1] == other.points[0];
		}
	}
	
	public static enum Type implements Iterable<EnumBusbarShape>{
		// @formatter:off
		STRAIGHT_INSULATORS_FLOOR(INSULATORS_DOWN_NORTH_SOUTH, INSULATORS_DOWN_EAST_WEST),
		STRAIGHT_INSULATORS_CEILING(INSULATORS_UP_NORTH_SOUTH, INSULATORS_UP_EAST_WEST),
		STRAIGHT_INSULATORS_WALL_NORMAL(
			INSULATORS_NORTH_UP_DOWN,
			INSULATORS_EAST_UP_DOWN,
			INSULATORS_SOUTH_UP_DOWN,
			INSULATORS_WEST_UP_DOWN
		),
		STRAIGHT_INSULATORS_WALL_ROTATED(
			INSULATORS_NORTH_EAST_WEST,
			INSULATORS_EAST_NORTH_SOUTH,
			INSULATORS_SOUTH_EAST_WEST,
			INSULATORS_WEST_NORTH_SOUTH
		),
		
		BENDS_FLOOR(
			BEND_DOWN_NORTH_EAST,
			BEND_DOWN_EAST_SOUTH,
			BEND_DOWN_SOUTH_WEST,
			BEND_DOWN_WEST_NORTH
		),
		BENDS_CEILING(
			BEND_UP_NORTH_EAST,
			BEND_UP_EAST_SOUTH,
			BEND_UP_SOUTH_WEST,
			BEND_UP_WEST_NORTH
		),
		BENDS_WALLS(
			BEND_NORTH_DOWN_EAST, BEND_NORTH_EAST_UP, BEND_NORTH_UP_WEST, BEND_NORTH_WEST_DOWN,
			BEND_EAST_DOWN_SOUTH, BEND_EAST_SOUTH_UP, BEND_EAST_UP_NORTH, BEND_EAST_NORTH_DOWN,
			BEND_SOUTH_DOWN_WEST, BEND_SOUTH_WEST_UP, BEND_SOUTH_UP_EAST, BEND_SOUTH_EAST_DOWN,
			BEND_WEST_DOWN_NORTH, BEND_WEST_NORTH_UP, BEND_WEST_UP_SOUTH, BEND_WEST_SOUTH_DOWN
		),
		BENDS_WALL_NORTH(BEND_NORTH_DOWN_EAST, BEND_NORTH_EAST_UP, BEND_NORTH_UP_WEST, BEND_NORTH_WEST_DOWN),
		BENDS_WALL_EAST(BEND_EAST_DOWN_SOUTH, BEND_EAST_SOUTH_UP, BEND_EAST_UP_NORTH, BEND_EAST_NORTH_DOWN),
		BENDS_WALL_SOUTH(BEND_SOUTH_DOWN_WEST, BEND_SOUTH_WEST_UP, BEND_SOUTH_UP_EAST, BEND_SOUTH_EAST_DOWN),
		BENDS_WALL_WEST(BEND_WEST_DOWN_NORTH, BEND_WEST_NORTH_UP, BEND_WEST_UP_SOUTH, BEND_WEST_SOUTH_DOWN),
		
		EDGE_INSIDE_FLOOR(
			EDGE_INSIDE_DOWN_SOUTH_UP_NORTH,
			EDGE_INSIDE_DOWN_WEST_UP_EAST,
			EDGE_INSIDE_DOWN_NORTH_UP_SOUTH,
			EDGE_INSIDE_DOWN_EAST_UP_WEST
		),
		EDGE_INSIDE_CEILING(
			EDGE_INSIDE_UP_NORTH_DOWN_SOUTH,
			EDGE_INSIDE_UP_EAST_DOWN_WEST,
			EDGE_INSIDE_UP_SOUTH_DOWN_NORTH,
			EDGE_INSIDE_UP_WEST_DOWN_EAST
		),
		EDGE_OUTSIDE_FLOOR(
			EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH,
			EDGE_OUTSIDE_DOWN_EAST_UP_WEST,
			EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH,
			EDGE_OUTSIDE_DOWN_WEST_UP_EAST
		),
		EDGE_OUTSIDE_CEILING(
			EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH,
			EDGE_OUTSIDE_UP_EAST_DOWN_WEST,
			EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH,
			EDGE_OUTSIDE_UP_WEST_DOWN_EAST
		)
		;
		// @formatter:on
		public static final Set<EnumBusbarShape> SEGMENTS_STRAIGHT;
		public static final Set<EnumBusbarShape> SEGMENTS_BEND;
		public static final Set<EnumBusbarShape> SEGMENTS_EDGE_IN;
		public static final Set<EnumBusbarShape> SEGMENTS_EDGE_OUT;
		static{
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_FLOOR.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_CEILING.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_WALL_NORMAL.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_WALL_ROTATED.shapes));
				
				SEGMENTS_STRAIGHT = Collections.unmodifiableSet(list);
			}
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(BENDS_FLOOR.shapes));
				list.addAll(Arrays.asList(BENDS_CEILING.shapes));
				list.addAll(Arrays.asList(BENDS_WALLS.shapes));
				
				SEGMENTS_BEND = Collections.unmodifiableSet(list);
			}
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(EDGE_INSIDE_FLOOR.shapes));
				list.addAll(Arrays.asList(EDGE_INSIDE_CEILING.shapes));
				
				SEGMENTS_EDGE_IN = Collections.unmodifiableSet(list);
			}
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(EDGE_OUTSIDE_FLOOR.shapes));
				list.addAll(Arrays.asList(EDGE_OUTSIDE_CEILING.shapes));
				
				SEGMENTS_EDGE_OUT = Collections.unmodifiableSet(list);
			}
		}
		
		private final EnumBusbarShape[] shapes;
		private Type(EnumBusbarShape... shapes){
			this.shapes = shapes;
		}
		
		public void forEachIndexed(BiConsumer<Integer, EnumBusbarShape> consumer){
			for(int i = 0;i < this.shapes.length;i++){
				consumer.accept(i, shapes[i]);
			}
		}
		
		public boolean contains(EnumBusbarShape shape){
			for(int i = 0;i < this.shapes.length;i++){
				if(this.shapes[i] == shape){
					return true;
				}
			}
			return false;
		}
		
		@Override
		public Iterator<EnumBusbarShape> iterator(){
			return Iterators.forArray(this.shapes);
		}
		
		public Stream<EnumBusbarShape> stream(){
			return Arrays.stream(this.shapes);
		}
	}
}
