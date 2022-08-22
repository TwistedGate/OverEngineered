package twistedgate.overengineered.common.blocks.busbar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import twistedgate.overengineered.OverEngineered;
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
		
		this.isStraight = !EnumBusbarShape.Type.STRAIGHT_SEGMENTS.contains(shape);
		
		updateConnections(shape);
	}
	
	private boolean canConnectTo(BusbarState busState){
		return connectsTo(busState) || this.connections.size() != 2;
	}
	
	private boolean connectsTo(BusbarState busState){
		return hasConnection(busState.pos);
	}
	
	@Nullable
	private BusbarState getBus(BlockPos pos){
		BlockState blockState = this.level.getBlockState(pos);
		// TODO May need special cases for edges. (Left "original" below)
		return BusbarBlock.isBusbar(blockState) ? new BusbarState(this.level, pos, blockState) : null;
		
		/*
		if(BusbarBlock.isBusbar(blockState)){
			return new BusbarState(this.level, pos, blockState);
		}else{
			BlockPos other = pos.above();
			blockState = this.level.getBlockState(other);
			if(BusbarBlock.isBusbar(blockState)){
				return new BusbarState(this.level, other, blockState);
			}else{
				other = pos.below();
				blockState = this.level.getBlockState(other);
				return BusbarBlock.isBusbar(blockState) ? new BusbarState(this.level, other, blockState) : null;
			}
		}
		*/
	}
	
	public BlockState getState(){
		return this.state;
	}
	
	private boolean hasConnection(BlockPos pos){
		for(int i = 0;i < this.connections.size();++i){
			BlockPos blockpos = this.connections.get(i);
			if(blockpos.getX() == pos.getX() && blockpos.getZ() == pos.getZ()){
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
	
	public BusbarState place(boolean placing, EnumBusbarShape shapeIn){
		BlockPos posDown = this.pos.below();
		BlockPos posUp = this.pos.above();
		BlockPos posNorth = this.pos.north();
		BlockPos posEast = this.pos.east();
		BlockPos posSouth = this.pos.south();
		BlockPos posWest = this.pos.west();
		
		boolean hasDown = hasNeighborBus(posDown);
		boolean hasUp = hasNeighborBus(posUp);
		boolean hasNorth = hasNeighborBus(posNorth);
		boolean hasEast = hasNeighborBus(posEast);
		boolean hasSouth = hasNeighborBus(posSouth);
		boolean hasWest = hasNeighborBus(posWest);
		
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
	
	/** Temporary, just for peaking */
	@Deprecated(forRemoval = true)
	public void /*RailState*/ place(){
		boolean pPowered = false, pPlaceBlock = false, canMakeSlopes = false;
		RailShape pShape = null;
		// Above would be the method args
		//-------------------------------------------------------------------------
		
		BlockPos posNorth = this.pos.north();
		BlockPos posSouth = this.pos.south();
		BlockPos posWest = this.pos.west();
		BlockPos posEast = this.pos.east();
		boolean hasNorth = this.hasNeighborBus(posNorth);
		boolean hasSouth = this.hasNeighborBus(posSouth);
		boolean hasWest = this.hasNeighborBus(posWest);
		boolean hasEast = this.hasNeighborBus(posEast);
		boolean ns = hasNorth || hasSouth;
		boolean ew = hasWest || hasEast;
		
		RailShape railshape = null;
		
		if(ns && !ew){
			railshape = RailShape.NORTH_SOUTH;
		}
		
		if(ew && !ns){
			railshape = RailShape.EAST_WEST;
		}
		
		boolean se = hasSouth && hasEast;
		boolean sw = hasSouth && hasWest;
		boolean ne = hasNorth && hasEast;
		boolean nw = hasNorth && hasWest;
		if(!this.isStraight){
			if(se && !hasNorth && !hasWest){
				railshape = RailShape.SOUTH_EAST;
			}
			
			if(sw && !hasNorth && !hasEast){
				railshape = RailShape.SOUTH_WEST;
			}
			
			if(nw && !hasSouth && !hasEast){
				railshape = RailShape.NORTH_WEST;
			}
			
			if(ne && !hasSouth && !hasWest){
				railshape = RailShape.NORTH_EAST;
			}
		}
		
		if(railshape == null){
			if(ns && ew){
				railshape = pShape;
			}else if(ns){
				railshape = RailShape.NORTH_SOUTH;
			}else if(ew){
				railshape = RailShape.EAST_WEST;
			}
			
			if(!this.isStraight){
				if(pPowered){
					if(se){
						railshape = RailShape.SOUTH_EAST;
					}
					
					if(sw){
						railshape = RailShape.SOUTH_WEST;
					}
					
					if(ne){
						railshape = RailShape.NORTH_EAST;
					}
					
					if(nw){
						railshape = RailShape.NORTH_WEST;
					}
				}else{
					if(nw){
						railshape = RailShape.NORTH_WEST;
					}
					
					if(ne){
						railshape = RailShape.NORTH_EAST;
					}
					
					if(sw){
						railshape = RailShape.SOUTH_WEST;
					}
					
					if(se){
						railshape = RailShape.SOUTH_EAST;
					}
				}
			}
		}
		
		if(railshape == RailShape.NORTH_SOUTH && canMakeSlopes){
			if(BaseRailBlock.isRail(this.level, posNorth.above())){
				railshape = RailShape.ASCENDING_NORTH;
			}
			
			if(BaseRailBlock.isRail(this.level, posSouth.above())){
				railshape = RailShape.ASCENDING_SOUTH;
			}
		}
		
		if(railshape == RailShape.EAST_WEST && canMakeSlopes){
			if(BaseRailBlock.isRail(this.level, posEast.above())){
				railshape = RailShape.ASCENDING_EAST;
			}
			
			if(BaseRailBlock.isRail(this.level, posWest.above())){
				railshape = RailShape.ASCENDING_WEST;
			}
		}
		
		if(railshape == null){
			railshape = pShape;
		}
		
		//updateConnections(railshape);
		this.state = this.state.setValue(RailBlock.SHAPE, railshape);
		if(pPlaceBlock || this.level.getBlockState(this.pos) != this.state){
			this.level.setBlock(this.pos, this.state, 3);
			
			for(int i = 0;i < this.connections.size();++i){
				BusbarState busState = getBus(this.connections.get(i));
				if(busState != null){
					busState.removeSoftConnections();
					if(busState.canConnectTo(this)){
						busState.connectTo(this);
					}
				}
			}
		}
		
		//return this;
	}
	
	private void connectTo(BusbarState state){
		this.connections.add(state.pos);
		
		BlockPos posDown = this.pos.below();
		BlockPos posUp = this.pos.above();
		BlockPos posNorth = this.pos.north();
		BlockPos posEast = this.pos.east();
		BlockPos posSouth = this.pos.south();
		BlockPos posWest = this.pos.west();
		
		boolean conDown = hasConnection(posDown);
		boolean conUp = hasConnection(posUp);
		boolean conNorth = hasConnection(posNorth);
		boolean conSouth = hasConnection(posSouth);
		boolean conWest = hasConnection(posWest);
		boolean conEast = hasConnection(posEast);
		
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
		
		OverEngineered.log.debug(
			"DEBUG\r\nconnectTo(\r\n\t" +
			"Level: {}\r\n\t" +
			"BlockState: {}\r\n\t" +
			"Pos: {}\r\n\t" +
			"Connections: {}\r\n)"+
			"{\r\n\t" +
			"Level: {}\r\n\t" +
			"State: {}\r\n\t" +
			"Pos: {}\r\n\t" +
			"Connections: {}\r\n\t" +
			"Shape: {}\r\n" +
			"}",
				state.level,
				state.state,
				state.pos,
				state.connections,
				
				this.level,
				this.state,
				this.pos,
				this.connections,
				shape
		);
		
		this.state = this.state.setValue(BusbarBlock.SHAPE, shape);
		this.level.setBlock(this.pos, this.state, 3);
	}

	/** Temporary, just for peaking */
	@SuppressWarnings({"null", "unused"})
	@Deprecated(forRemoval = true)
	private void connectTo(){
		BusbarState pState = null;
		// Above would be the method args
		//-------------------------------------------------------------------------
		
		this.connections.add(pState.pos);
		BlockPos posNorth = this.pos.north();
		BlockPos posSouth = this.pos.south();
		BlockPos posWest = this.pos.west();
		BlockPos posEast = this.pos.east();
		boolean conNorth = hasConnection(posNorth);
		boolean conSouth = hasConnection(posSouth);
		boolean conWest = hasConnection(posWest);
		boolean conEast = hasConnection(posEast);
		RailShape shape = null;
		if(conNorth || conSouth){
			shape = RailShape.NORTH_SOUTH;
		}
		
		if(conWest || conEast){
			shape = RailShape.EAST_WEST;
		}
		
		if(!this.isStraight){
			if(conSouth && conEast && !conNorth && !conWest){
				shape = RailShape.SOUTH_EAST;
			}
			
			if(conSouth && conWest && !conNorth && !conEast){
				shape = RailShape.SOUTH_WEST;
			}
			
			if(conNorth && conWest && !conSouth && !conEast){
				shape = RailShape.NORTH_WEST;
			}
			
			if(conNorth && conEast && !conSouth && !conWest){
				shape = RailShape.NORTH_EAST;
			}
		}
		
		/*
		if(railshape == RailShape.NORTH_SOUTH && canMakeSlopes){
			if(BaseRailBlock.isRail(this.level, blockpos.above())){
				railshape = RailShape.ASCENDING_NORTH;
			}
			
			if(BaseRailBlock.isRail(this.level, blockpos1.above())){
				railshape = RailShape.ASCENDING_SOUTH;
			}
		}
		
		if(railshape == RailShape.EAST_WEST && canMakeSlopes){
			if(BaseRailBlock.isRail(this.level, blockpos3.above())){
				railshape = RailShape.ASCENDING_EAST;
			}
			
			if(BaseRailBlock.isRail(this.level, blockpos2.above())){
				railshape = RailShape.ASCENDING_WEST;
			}
		}
		*/
		
		if(shape == null){
			shape = RailShape.NORTH_SOUTH;
		}
		
//		this.state = this.state.setValue(BusbarBlock.SHAPE, railshape);
//		this.level.setBlock(this.pos, this.state, 3);
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
