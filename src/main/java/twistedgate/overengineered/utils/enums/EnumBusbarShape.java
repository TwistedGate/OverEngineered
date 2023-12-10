package twistedgate.overengineered.utils.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.google.common.collect.Iterators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

/**
 * @author TwistedGate
 */
public enum EnumBusbarShape implements StringRepresentable{
	// @formatter:off
	// Straights with Insulators (FACING_FROM_TO)
	INSULATORS_DOWN_NORTH_SOUTH(Direction.DOWN, Connections.NORTH_SOUTH),
	INSULATORS_DOWN_EAST_WEST(Direction.DOWN, Connections.EAST_WEST),
	INSULATORS_UP_NORTH_SOUTH(Direction.UP, Connections.NORTH_SOUTH),
	INSULATORS_UP_EAST_WEST(Direction.UP, Connections.EAST_WEST),
	
	INSULATORS_NORTH_UP_DOWN(Direction.NORTH, Connections.UP_DOWN),
	INSULATORS_EAST_UP_DOWN(Direction.EAST, Connections.UP_DOWN),
	INSULATORS_SOUTH_UP_DOWN(Direction.SOUTH, Connections.UP_DOWN),
	INSULATORS_WEST_UP_DOWN(Direction.WEST, Connections.UP_DOWN),
	INSULATORS_NORTH_EAST_WEST(Direction.NORTH, Connections.EAST_WEST),
	INSULATORS_EAST_NORTH_SOUTH(Direction.EAST, Connections.NORTH_SOUTH),
	INSULATORS_SOUTH_EAST_WEST(Direction.SOUTH, Connections.EAST_WEST),
	INSULATORS_WEST_NORTH_SOUTH(Direction.WEST, Connections.NORTH_SOUTH),
	
	// 90° Bends (FACING_FROM_TO)
	BEND_DOWN_NORTH_EAST(Direction.DOWN, Connections.NORTH_EAST),
	BEND_DOWN_EAST_SOUTH(Direction.DOWN, Connections.EAST_SOUTH),
	BEND_DOWN_SOUTH_WEST(Direction.DOWN, Connections.SOUTH_WEST),
	BEND_DOWN_WEST_NORTH(Direction.DOWN, Connections.WEST_NORTH),
	
	BEND_UP_NORTH_EAST(Direction.UP, Connections.NORTH_EAST),
	BEND_UP_EAST_SOUTH(Direction.UP, Connections.EAST_SOUTH),
	BEND_UP_SOUTH_WEST(Direction.UP, Connections.SOUTH_WEST),
	BEND_UP_WEST_NORTH(Direction.UP, Connections.WEST_NORTH),
	
	BEND_NORTH_DOWN_EAST(Direction.NORTH, Connections.DOWN_EAST),
	BEND_NORTH_EAST_UP(Direction.NORTH, Connections.UP_EAST),
	BEND_NORTH_UP_WEST(Direction.NORTH, Connections.UP_WEST),
	BEND_NORTH_WEST_DOWN(Direction.NORTH, Connections.DOWN_WEST),
	
	BEND_EAST_DOWN_SOUTH(Direction.EAST, Connections.DOWN_SOUTH),
	BEND_EAST_SOUTH_UP(Direction.EAST, Connections.UP_SOUTH),
	BEND_EAST_UP_NORTH(Direction.EAST, Connections.UP_NORTH),
	BEND_EAST_NORTH_DOWN(Direction.EAST, Connections.DOWN_NORTH),
	
	BEND_SOUTH_DOWN_WEST(Direction.SOUTH, Connections.DOWN_WEST),
	BEND_SOUTH_WEST_UP(Direction.SOUTH, Connections.UP_WEST),
	BEND_SOUTH_UP_EAST(Direction.SOUTH, Connections.UP_EAST),
	BEND_SOUTH_EAST_DOWN(Direction.SOUTH, Connections.DOWN_EAST),
	
	BEND_WEST_DOWN_NORTH(Direction.WEST, Connections.DOWN_NORTH),
	BEND_WEST_NORTH_UP(Direction.WEST, Connections.UP_NORTH),
	BEND_WEST_UP_SOUTH(Direction.WEST, Connections.UP_SOUTH),
	BEND_WEST_SOUTH_DOWN(Direction.WEST, Connections.DOWN_SOUTH),
	
	// Inside Edges (FACING_SIDE_FROM_TO)
	EDGE_INSIDE_DOWN_NORTH_UP_SOUTH	(Direction.DOWN, Connections.UP_SOUTH),
	EDGE_INSIDE_DOWN_EAST_UP_WEST	(Direction.DOWN, Connections.UP_WEST),
	EDGE_INSIDE_DOWN_SOUTH_UP_NORTH	(Direction.DOWN, Connections.UP_NORTH),
	EDGE_INSIDE_DOWN_WEST_UP_EAST	(Direction.DOWN, Connections.UP_EAST),
	EDGE_INSIDE_UP_NORTH_DOWN_SOUTH	(Direction.UP, Connections.DOWN_SOUTH),
	EDGE_INSIDE_UP_EAST_DOWN_WEST	(Direction.UP, Connections.DOWN_WEST),
	EDGE_INSIDE_UP_SOUTH_DOWN_NORTH	(Direction.UP, Connections.DOWN_NORTH),
	EDGE_INSIDE_UP_WEST_DOWN_EAST	(Direction.UP, Connections.DOWN_EAST),
	
	// Outside Edges (FACING_SIDE_FROM_TO)
	EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH(Direction.DOWN, Connections.UP_SOUTH),
	EDGE_OUTSIDE_DOWN_EAST_UP_WEST	(Direction.DOWN, Connections.UP_WEST),
	EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH(Direction.DOWN, Connections.UP_NORTH),
	EDGE_OUTSIDE_DOWN_WEST_UP_EAST	(Direction.DOWN, Connections.UP_EAST),
	EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH(Direction.UP, Connections.DOWN_SOUTH),
	EDGE_OUTSIDE_UP_EAST_DOWN_WEST	(Direction.UP, Connections.DOWN_WEST),
	EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH(Direction.UP, Connections.DOWN_NORTH),
	EDGE_OUTSIDE_UP_WEST_DOWN_EAST	(Direction.UP, Connections.DOWN_EAST),
	;
	// @formatter:on
	
	private final String serialname;
	public final Direction facing;
	public final Connections connections;
	private EnumBusbarShape(Direction facing, Connections cons){
		this.serialname = name().toLowerCase();
		this.facing = facing;
		this.connections = cons;
	}
	
	private EnumBusbarShape(String name, Direction facing, Connections cons){
		this.serialname = name;
		this.facing = facing;
		this.connections = cons;
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
	
	public static enum Connections{
		NORTH_SOUTH(Direction.NORTH, Direction.SOUTH),
		EAST_WEST(Direction.EAST, Direction.WEST),
		UP_DOWN(Direction.UP, Direction.DOWN),
		
		NORTH_EAST(Direction.NORTH, Direction.EAST),
		EAST_SOUTH(Direction.EAST, Direction.SOUTH),
		SOUTH_WEST(Direction.SOUTH, Direction.WEST),
		WEST_NORTH(Direction.WEST, Direction.NORTH),
		
		UP_NORTH(Direction.UP, Direction.NORTH),
		UP_EAST(Direction.UP, Direction.EAST),
		UP_SOUTH(Direction.UP, Direction.SOUTH),
		UP_WEST(Direction.UP, Direction.WEST),
		
		DOWN_NORTH(Direction.DOWN, Direction.NORTH),
		DOWN_EAST(Direction.DOWN, Direction.EAST),
		DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
		DOWN_WEST(Direction.DOWN, Direction.WEST)
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
		public static final Set<EnumBusbarShape> STRAIGHT_SEGMENTS;
		public static final Set<EnumBusbarShape> BEND_SEGMENTS;
		public static final Set<EnumBusbarShape> EDGE_SEGMENTS;
		static{
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_FLOOR.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_CEILING.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_WALL_NORMAL.shapes));
				list.addAll(Arrays.asList(STRAIGHT_INSULATORS_WALL_ROTATED.shapes));
				
				STRAIGHT_SEGMENTS = Collections.unmodifiableSet(list);
			}
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();
				
				list.addAll(Arrays.asList(BENDS_FLOOR.shapes));
				list.addAll(Arrays.asList(BENDS_CEILING.shapes));
				list.addAll(Arrays.asList(BENDS_WALLS.shapes));
				
				BEND_SEGMENTS = Collections.unmodifiableSet(list);
			}
			{
				HashSet<EnumBusbarShape> list = new HashSet<>();

				list.addAll(Arrays.asList(EDGE_INSIDE_FLOOR.shapes));
				list.addAll(Arrays.asList(EDGE_INSIDE_CEILING.shapes));
				list.addAll(Arrays.asList(EDGE_OUTSIDE_FLOOR.shapes));
				list.addAll(Arrays.asList(EDGE_OUTSIDE_CEILING.shapes));
				
				EDGE_SEGMENTS = Collections.unmodifiableSet(list);
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
