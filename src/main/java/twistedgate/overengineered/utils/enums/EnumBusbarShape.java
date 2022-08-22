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
	INSULATORS_DOWN_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	INSULATORS_DOWN_EAST_WEST(ConnectionsPoints.EAST_WEST),
	INSULATORS_UP_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	INSULATORS_UP_EAST_WEST(ConnectionsPoints.EAST_WEST),
	
	INSULATORS_NORTH_UP_DOWN(ConnectionsPoints.UP_DOWN),
	INSULATORS_EAST_UP_DOWN(ConnectionsPoints.UP_DOWN),
	INSULATORS_SOUTH_UP_DOWN(ConnectionsPoints.UP_DOWN),
	INSULATORS_WEST_UP_DOWN(ConnectionsPoints.UP_DOWN),
	INSULATORS_NORTH_EAST_WEST(ConnectionsPoints.EAST_WEST),
	INSULATORS_EAST_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	INSULATORS_SOUTH_EAST_WEST(ConnectionsPoints.EAST_WEST),
	INSULATORS_WEST_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	
	// Straights without Insulators (FACING_FROM_TO)
	FLOATING_DOWN_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	FLOATING_DOWN_EAST_WEST(ConnectionsPoints.EAST_WEST),
	FLOATING_UP_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	FLOATING_UP_EAST_WEST(ConnectionsPoints.EAST_WEST),
	
	FLOATING_NORTH_UP_DOWN(ConnectionsPoints.UP_DOWN),
	FLOATING_EAST_UP_DOWN(ConnectionsPoints.UP_DOWN),
	FLOATING_SOUTH_UP_DOWN(ConnectionsPoints.UP_DOWN),
	FLOATING_WEST_UP_DOWN(ConnectionsPoints.UP_DOWN),
	FLOATING_NORTH_EAST_WEST(ConnectionsPoints.EAST_WEST),
	FLOATING_EAST_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	FLOATING_SOUTH_EAST_WEST(ConnectionsPoints.EAST_WEST),
	FLOATING_WEST_NORTH_SOUTH(ConnectionsPoints.NORTH_SOUTH),
	
	// 90° Bends (FACING_FROM_TO)
	BEND_DOWN_NORTH_EAST(ConnectionsPoints.NORTH_EAST),
	BEND_DOWN_EAST_SOUTH(ConnectionsPoints.EAST_SOUTH),
	BEND_DOWN_SOUTH_WEST(ConnectionsPoints.SOUTH_WEST),
	BEND_DOWN_WEST_NORTH(ConnectionsPoints.WEST_NORTH),
	
	BEND_UP_NORTH_EAST(ConnectionsPoints.NORTH_EAST),
	BEND_UP_EAST_SOUTH(ConnectionsPoints.EAST_SOUTH),
	BEND_UP_SOUTH_WEST(ConnectionsPoints.SOUTH_WEST),
	BEND_UP_WEST_NORTH(ConnectionsPoints.WEST_NORTH),
	
	BEND_NORTH_DOWN_EAST(ConnectionsPoints.DOWN_EAST),
	BEND_NORTH_EAST_UP(ConnectionsPoints.UP_EAST),
	BEND_NORTH_UP_WEST(ConnectionsPoints.UP_WEST),
	BEND_NORTH_WEST_DOWN(ConnectionsPoints.DOWN_WEST),
	
	BEND_EAST_DOWN_SOUTH(ConnectionsPoints.DOWN_SOUTH),
	BEND_EAST_SOUTH_UP(ConnectionsPoints.UP_SOUTH),
	BEND_EAST_UP_NORTH(ConnectionsPoints.UP_NORTH),
	BEND_EAST_NORTH_DOWN(ConnectionsPoints.DOWN_NORTH),
	
	BEND_SOUTH_DOWN_WEST(ConnectionsPoints.DOWN_WEST),
	BEND_SOUTH_WEST_UP(ConnectionsPoints.UP_WEST),
	BEND_SOUTH_UP_EAST(ConnectionsPoints.UP_EAST),
	BEND_SOUTH_EAST_DOWN(ConnectionsPoints.DOWN_EAST),
	
	BEND_WEST_DOWN_NORTH(ConnectionsPoints.DOWN_NORTH),
	BEND_WEST_NORTH_UP(ConnectionsPoints.UP_NORTH),
	BEND_WEST_UP_SOUTH(ConnectionsPoints.UP_SOUTH),
	BEND_WEST_SOUTH_DOWN(ConnectionsPoints.DOWN_SOUTH),
	
	// Inside Edges (SIDE_FACING_FROM_TO)
	EDGE_INSIDE_DOWN_NORTH_UP_SOUTH(ConnectionsPoints.UP_SOUTH),
	EDGE_INSIDE_DOWN_EAST_UP_WEST(ConnectionsPoints.UP_WEST),
	EDGE_INSIDE_DOWN_SOUTH_UP_NORTH(ConnectionsPoints.UP_NORTH),
	EDGE_INSIDE_DOWN_WEST_UP_EAST(ConnectionsPoints.UP_EAST),
	EDGE_INSIDE_UP_NORTH_DOWN_SOUTH(ConnectionsPoints.DOWN_SOUTH),
	EDGE_INSIDE_UP_EAST_DOWN_WEST(ConnectionsPoints.DOWN_WEST),
	EDGE_INSIDE_UP_SOUTH_DOWN_NORTH(ConnectionsPoints.DOWN_NORTH),
	EDGE_INSIDE_UP_WEST_DOWN_EAST(ConnectionsPoints.DOWN_EAST),
	
	// Outside Edges (SIDE_FACING_FROM_TO)
	EDGE_OUTSIDE_DOWN_NORTH_UP_SOUTH(ConnectionsPoints.UP_SOUTH),
	EDGE_OUTSIDE_DOWN_EAST_UP_WEST(ConnectionsPoints.UP_WEST),
	EDGE_OUTSIDE_DOWN_SOUTH_UP_NORTH(ConnectionsPoints.UP_NORTH),
	EDGE_OUTSIDE_DOWN_WEST_UP_EAST(ConnectionsPoints.UP_EAST),
	EDGE_OUTSIDE_UP_NORTH_DOWN_SOUTH(ConnectionsPoints.DOWN_SOUTH),
	EDGE_OUTSIDE_UP_EAST_DOWN_WEST(ConnectionsPoints.DOWN_WEST),
	EDGE_OUTSIDE_UP_SOUTH_DOWN_NORTH(ConnectionsPoints.DOWN_NORTH),
	EDGE_OUTSIDE_UP_WEST_DOWN_EAST(ConnectionsPoints.DOWN_EAST),
	;
	// @formatter:on
	
	private final String serialname;
	private final Direction[] points;
	private EnumBusbarShape(Direction... points){
		this.serialname = name().toLowerCase();
		this.points = points;
	}
	private EnumBusbarShape(String name, Direction... points){
		this.serialname = name;
		this.points = points;
	}
	
	public void connectionOffsets(final List<BlockPos> list, BlockPos pos){
		for(Direction p:this.points){
			list.add(pos.relative(p));
		}
	}
	
	@Override
	public String getSerializedName(){
		return this.serialname;
	}
	
	public static class ConnectionsPoints{
		private static final Direction[] NORTH_SOUTH = {Direction.NORTH, Direction.SOUTH};
		private static final Direction[] EAST_WEST = {Direction.EAST, Direction.WEST};
		private static final Direction[] UP_DOWN = {Direction.UP, Direction.DOWN};
		
		private static final Direction[] NORTH_EAST = {Direction.NORTH, Direction.EAST};
		private static final Direction[] EAST_SOUTH = {Direction.EAST, Direction.SOUTH};
		private static final Direction[] SOUTH_WEST = {Direction.SOUTH, Direction.WEST};
		private static final Direction[] WEST_NORTH = {Direction.WEST, Direction.NORTH};
		
		private static final Direction[] UP_NORTH = {Direction.UP, Direction.NORTH};
		private static final Direction[] UP_EAST = {Direction.UP, Direction.EAST};
		private static final Direction[] UP_SOUTH = {Direction.UP, Direction.SOUTH};
		private static final Direction[] UP_WEST = {Direction.UP, Direction.WEST};
		
		private static final Direction[] DOWN_NORTH = {Direction.DOWN, Direction.NORTH};
		private static final Direction[] DOWN_EAST = {Direction.DOWN, Direction.EAST};
		private static final Direction[] DOWN_SOUTH = {Direction.DOWN, Direction.SOUTH};
		private static final Direction[] DOWN_WEST = {Direction.DOWN, Direction.WEST};
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
		
		STRAIGHT_FLOATING_FLOOR(FLOATING_DOWN_NORTH_SOUTH, FLOATING_DOWN_EAST_WEST),
		STRAIGHT_FLOATING_CEILING(FLOATING_UP_NORTH_SOUTH, FLOATING_UP_EAST_WEST),
		STRAIGHT_FLOATING_WALL_NORMAL(
			FLOATING_NORTH_UP_DOWN,
			FLOATING_EAST_UP_DOWN,
			FLOATING_SOUTH_UP_DOWN,
			FLOATING_WEST_UP_DOWN
		),
		STRAIGHT_FLOATING_WALL_ROTATED(
			FLOATING_NORTH_EAST_WEST,
			FLOATING_EAST_NORTH_SOUTH,
			FLOATING_SOUTH_EAST_WEST,
			FLOATING_WEST_NORTH_SOUTH
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
				
				list.addAll(Arrays.asList(STRAIGHT_FLOATING_FLOOR.shapes));
				list.addAll(Arrays.asList(STRAIGHT_FLOATING_CEILING.shapes));
				list.addAll(Arrays.asList(STRAIGHT_FLOATING_WALL_NORMAL.shapes));
				list.addAll(Arrays.asList(STRAIGHT_FLOATING_WALL_ROTATED.shapes));
				
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
