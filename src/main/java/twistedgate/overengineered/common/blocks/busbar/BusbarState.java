package twistedgate.overengineered.common.blocks.busbar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import twistedgate.overengineered.utils.enums.EnumBusbarShape;

public class BusbarState{
	private final List<BlockPos> connections = new ArrayList<>();
	private final Level level;
	private final BlockPos pos;
	private final boolean isStraight;
	private BlockState state;
	public BusbarState(Level pLevel, BlockPos pPos, BlockState pState){
		this.level = pLevel;
		this.pos = pPos;
		this.state = pState;
		
		EnumBusbarShape shape = this.state.getValue(BusbarBlock.SHAPE);
		
		this.isStraight = false;//!EnumBusbarShape.Type.STRAIGHT_SEGMENTS.contains(shape);
		
		updateConnections(shape);
	}
	
	@Nullable
	private BusbarState getBus(BlockPos pos){
		BlockState blockState = this.level.getBlockState(pos);
		// TODO May need special cases for edges
		return BusbarBlock.isBusbar(blockState) ? new BusbarState(this.level, pos, blockState) : null;
	}
	
	public BlockState getState(){
		return this.state;
	}
	
	private boolean canConnectTo(BusbarState busState){
		return connectsTo(busState) || this.connections.size() != 2;
	}
	
	private boolean connectsTo(BusbarState busState){
		return hasConnection(busState.pos);
	}
	
	private boolean hasConnection(BlockPos pos){
		for(int i = 0;i < this.connections.size();++i){
			BlockPos blockpos = this.connections.get(i);
			if(blockpos.equals(pos)){
				return true;
			}
		}
		return false;
	}
	
	private boolean hasNeighborBus(BlockPos pos){
		BusbarState busState = getBus(pos);
		if(busState == null){
			return false;
		}
		
		busState.removeSoftConnections();
		return busState.canConnectTo(this);
	}
	
	@SuppressWarnings("unused")
	public BusbarState place(boolean placing, EnumBusbarShape shapeIn){
		final BlockPos posDown = this.pos.below();
		final BlockPos posUp = this.pos.above();
		final BlockPos posNorth = this.pos.north();
		final BlockPos posEast = this.pos.east();
		final BlockPos posSouth = this.pos.south();
		final BlockPos posWest = this.pos.west();
		
		final boolean hasDown = hasNeighborBus(posDown);
		final boolean hasUp = hasNeighborBus(posUp);
		final boolean hasNorth = hasNeighborBus(posNorth);
		final boolean hasEast = hasNeighborBus(posEast);
		final boolean hasSouth = hasNeighborBus(posSouth);
		final boolean hasWest = hasNeighborBus(posWest);
		
		EnumBusbarShape shape = null;
		
		boolean ns = hasNorth || hasSouth;
		boolean ew = hasEast || hasWest;
		boolean ud = hasUp || hasDown;
		if(ns && !ew){
			shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		}
		if(ew && !ns){
			shape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
		}
		
		boolean se = hasSouth && hasEast;
		boolean sw = hasSouth && hasWest;
		boolean ne = hasNorth && hasEast;
		boolean nw = hasNorth && hasWest;
		if(!this.isStraight){
			if(se && !hasNorth && !hasWest){
				shape = EnumBusbarShape.BEND_DOWN_EAST_SOUTH;
			}
			
			if(sw && !hasNorth && !hasEast){
				shape = EnumBusbarShape.BEND_DOWN_SOUTH_WEST;
			}
			
			if(nw && !hasSouth && !hasEast){
				shape = EnumBusbarShape.BEND_DOWN_WEST_NORTH;
			}
			
			if(ne && !hasSouth && !hasWest){
				shape = EnumBusbarShape.BEND_DOWN_NORTH_EAST;
			}
		}
		
		if(shape == null){
			if(ns && ew){
				shape = shapeIn;
			}else if(ns){
				shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
			}else if(ew){
				shape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
			}
			
			if(!this.isStraight){
				if(nw){
					shape = EnumBusbarShape.BEND_DOWN_WEST_NORTH;
				}
				
				if(ne){
					shape = EnumBusbarShape.BEND_DOWN_NORTH_EAST;
				}
				
				if(sw){
					shape = EnumBusbarShape.BEND_DOWN_SOUTH_WEST;
				}
				
				if(se){
					shape = EnumBusbarShape.BEND_DOWN_EAST_SOUTH;
				}
			}
		}
		
		// -----------------------------------------------------------
		if(shape == null){
			shape = shapeIn;
		}
		
		updateConnections(shape);
		this.state = this.state.setValue(BusbarBlock.SHAPE, shape);
		if(placing || this.level.getBlockState(this.pos) != this.state){
			this.level.setBlock(this.pos, this.state, 3);
			
			for(int i = 0;i < this.connections.size();i++){
				BusbarState busState = getBus(this.connections.get(i));
				if(busState != null){
					busState.removeSoftConnections();
					if(busState.canConnectTo(this)){
						busState.connectTo(this);
					}
				}
			}
		}
		
		return this;
	}
	
	@SuppressWarnings("unused")
	private void connectTo(BusbarState state){
		this.connections.add(state.pos);
		
		final BlockPos posDown = this.pos.below();
		final BlockPos posUp = this.pos.above();
		final BlockPos posNorth = this.pos.north();
		final BlockPos posEast = this.pos.east();
		final BlockPos posSouth = this.pos.south();
		final BlockPos posWest = this.pos.west();
		
		final boolean conDown = hasConnection(posDown);
		final boolean conUp = hasConnection(posUp);
		final boolean conNorth = hasConnection(posNorth);
		final boolean conSouth = hasConnection(posSouth);
		final boolean conWest = hasConnection(posWest);
		final boolean conEast = hasConnection(posEast);
		
		EnumBusbarShape shape = null;
		
		if(conNorth || conSouth){
			shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		}
		if(conEast || conWest){
			shape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
		}
		
		if(!this.isStraight){
			if(conSouth && conEast && !conNorth && !conWest){
				shape = EnumBusbarShape.BEND_DOWN_EAST_SOUTH;
			}
			
			if(conSouth && conWest && !conNorth && !conEast){
				shape = EnumBusbarShape.BEND_DOWN_SOUTH_WEST;
			}
			
			if(conNorth && conWest && !conSouth && !conEast){
				shape = EnumBusbarShape.BEND_DOWN_WEST_NORTH;
			}
			
			if(conNorth && conEast && !conSouth && !conWest){
				shape = EnumBusbarShape.BEND_DOWN_NORTH_EAST;
			}
		}
		
		if(shape == null){
			shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		}
		
		this.state = this.state.setValue(BusbarBlock.SHAPE, shape);
		this.level.setBlock(this.pos, this.state, 3);
	}
	
	private void removeSoftConnections(){
		for(int i = 0;i < this.connections.size();i++){
			BusbarState busState = getBus(this.connections.get(i));
			if(busState != null && busState.connectsTo(this)){
				this.connections.set(i, busState.pos);
			}else{
				this.connections.remove(i--);
			}
		}
	}
	
	private void updateConnections(EnumBusbarShape shape){
		this.connections.clear();
		shape.connectionOffsets(this.connections, this.pos);
	}
}
