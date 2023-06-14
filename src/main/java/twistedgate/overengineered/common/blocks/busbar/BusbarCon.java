package twistedgate.overengineered.common.blocks.busbar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.utils.enums.EnumBusbarShape;

public class BusbarCon{
	
	/**
	 * This, if not empty, shows that there is still a free spot to connect to
	 */
	final List<BlockPos> freePoints = new ArrayList<>();
	final Level level;
	final BlockPos pos;
	final Direction facing;
	EnumBusbarShape shape;
	BlockState state;
	
	public BusbarCon(Level level, BlockPos pos, BlockState state){
		this.level = level;
		this.pos = pos;
		this.state = state;
		this.shape = state.getValue(BusbarBlock.SHAPE);
		this.facing = this.shape.facing;
		
		updateConnections(this.shape);
	}
	
	public List<BlockPos> getConnections(){
		return this.freePoints;
	}
	
	public EnumBusbarShape getShape(){
		return this.shape;
	}
	
	public BlockState getState(){
		removeExistingConnections();
		
		BlockState ret = this.state;
		// Not sure if somethings gonna go between this yet
		return ret;
	}
	
	@SuppressWarnings("unused")
	public void onPlace(boolean placing){
		// @formatter:off
		final BlockPos posDown	= this.pos.relative(Direction.DOWN);
		final BlockPos posUp	= this.pos.relative(Direction.UP);
		final BlockPos posNorth	= this.pos.relative(Direction.NORTH);
		final BlockPos posEast	= this.pos.relative(Direction.EAST);
		final BlockPos posSouth	= this.pos.relative(Direction.SOUTH);
		final BlockPos posWest	= this.pos.relative(Direction.WEST);
		
		final boolean hasDown	= this.hasNeigborBus(posDown);
		final boolean hasUp		= this.hasNeigborBus(posUp);
		final boolean hasNorth	= this.hasNeigborBus(posNorth);
		final boolean hasSouth	= this.hasNeigborBus(posSouth);
		final boolean hasEast	= this.hasNeigborBus(posEast);
		final boolean hasWest	= this.hasNeigborBus(posWest);
		
		final boolean _UpOrDown		= hasUp    || hasDown;
		final boolean _NorthOrSouth	= hasNorth || hasSouth;
		final boolean _EastOrWest	= hasEast  || hasWest;
		
		final boolean _NorthAndEast = hasNorth && hasEast;
		final boolean _NorthAndWest = hasNorth && hasWest;
		final boolean _SouthAndEast = hasSouth && hasEast;
		final boolean _SouthAndWest = hasSouth && hasWest;
		
		OverEngineered.log.info(String.format("""
				onPlace([%d, %d, %d])
				down: %s,
				up: %s,
				north: %s,
				south: %s,
				east: %s,
				west: %s""", this.pos.getX(), this.pos.getY(), this.pos.getZ(),
				hasDown, hasUp, hasNorth, hasSouth, hasEast, hasWest));
		// @formatter:on
		
		EnumBusbarShape newShape = null;
		
		switch(this.facing){
			case UP:{
				break;
			}
			case DOWN:{
				if(_NorthOrSouth && !_EastOrWest){
					newShape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
				}
				if(_EastOrWest && !_NorthOrSouth){
					newShape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
				}
				
				if(_NorthAndWest && !_SouthAndEast){
					newShape = EnumBusbarShape.BEND_DOWN_WEST_NORTH;
				}
				if(_NorthAndEast && !_SouthAndWest){
					newShape = EnumBusbarShape.BEND_DOWN_NORTH_EAST;
				}
				
				if(_SouthAndEast && !_NorthAndWest){
					newShape = EnumBusbarShape.BEND_DOWN_EAST_SOUTH;
				}
				if(_SouthAndWest && !_NorthAndEast){
					newShape = EnumBusbarShape.BEND_DOWN_SOUTH_WEST;
				}
				break;
			}
			case NORTH:{
				break;
			}
			case EAST:{
				break;
			}
			case SOUTH:{
				break;
			}
			case WEST:{
				break;
			}
			default:
				break;
		}
		
		// TODO ???
		
		if(newShape != null)
			this.shape = newShape;
		
		// TODO ???
		updateConnections(this.shape);
		this.state = this.state.setValue(BusbarBlock.SHAPE, this.shape);
		if(placing || this.level.getBlockState(this.pos) != this.state){
			this.level.setBlock(this.pos, this.state, 3);
			
			for(BlockPos cPos:this.freePoints){
				BusbarCon bus = getBus(cPos);
				
				if(bus != null){
					bus.removeExistingConnections();
					
					if(bus.canConnectTo(this)){
						bus.connectTo(this);
					}
				}
			}
		}
	}
	
	public void connectTo(BusbarCon other){
		this.freePoints.add(other.pos);
		
		// @formatter:off
		final BlockPos posDown	= this.pos.relative(Direction.DOWN);
		final BlockPos posUp	= this.pos.relative(Direction.UP);
		final BlockPos posNorth	= this.pos.relative(Direction.NORTH);
		final BlockPos posEast	= this.pos.relative(Direction.EAST);
		final BlockPos posSouth	= this.pos.relative(Direction.SOUTH);
		final BlockPos posWest	= this.pos.relative(Direction.WEST);
		
		final boolean hasDown	= this.hasConnection(posDown);
		final boolean hasUp		= this.hasConnection(posUp);
		final boolean hasNorth	= this.hasConnection(posNorth);
		final boolean hasEast	= this.hasConnection(posEast);
		final boolean hasSouth	= this.hasConnection(posSouth);
		final boolean hasWest	= this.hasConnection(posWest);
		// @formatter:on
		
		OverEngineered.log.info(String.format("""
				[%d, %d, %d].connectTo([%d, %d, %d])
				down: %s,
				up: %s,
				north: %s,
				south: %s,
				east: %s,
				west: %s""", this.pos.getX(), this.pos.getY(), this.pos.getZ(),
				other.pos.getX(), other.pos.getY(), other.pos.getZ(),
				hasDown, hasUp, hasNorth, hasSouth, hasEast, hasWest));
		
		EnumBusbarShape shape = null;
		
		switch(this.facing){
			case UP:{
				break;
			}
			case DOWN:{
				if(hasNorth || hasSouth){
					shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
				}
				if(hasEast || hasWest){
					shape = EnumBusbarShape.INSULATORS_DOWN_EAST_WEST;
				}
				
				if(hasSouth && hasEast && !hasNorth && !hasWest){
					shape = EnumBusbarShape.BEND_DOWN_EAST_SOUTH;
				}
				if(hasSouth && hasWest && !hasNorth && !hasEast){
					shape = EnumBusbarShape.BEND_DOWN_SOUTH_WEST;
				}
				if(hasNorth && hasWest && !hasSouth && !hasEast){
					shape = EnumBusbarShape.BEND_DOWN_WEST_NORTH;
				}
				if(hasNorth && hasEast && !hasSouth && !hasWest){
					shape = EnumBusbarShape.BEND_DOWN_NORTH_EAST;
				}
				break;
			}
			case NORTH:{
				break;
			}
			case EAST:{
				break;
			}
			case SOUTH:{
				break;
			}
			case WEST:{
				break;
			}
			default:
				break;
		}
		
		//OverEngineered.log.info("Selecting: {}", shape != null ? shape.getSerializedName() : "Default");
		
		if(shape == null){
			shape = EnumBusbarShape.INSULATORS_DOWN_NORTH_SOUTH;
		}
		
		this.state = this.state.setValue(BusbarBlock.SHAPE, shape);
		this.level.setBlock(this.pos, this.state, 3);
	}
	
	public BusbarCon printDebugInfo(){
		OverEngineered.log.info(toString());
		return this;
	}
	
	public boolean hasOpenConnection(){
		return !this.freePoints.isEmpty();
	}
	
	public boolean canConnectTo(BusbarCon bus){
		return connectsTo(bus) || hasOpenConnection();
	}
	
	@Nullable
	public BusbarCon getBus(BlockPos pos){
		BlockState state = this.level.getBlockState(pos);
		return create(this.level, pos, state);
	}
	
	@Nullable
	public static BusbarCon create(Level level, BlockPos pos, BlockState state){
		return BusbarBlock.isBusbar(state) ? new BusbarCon(level, pos, state) : null;
	}
	
	public void updateConnections(EnumBusbarShape shape){
		this.freePoints.clear();
		shape.connectionOffsets(this.freePoints, this.pos);
	}
	
	/**
	 * <pre>
	 * <b>This has to be called after instantiating.</b>
	 * </pre>
	 * 
	 * Remove connections that already exist between this and another buses.<br>
	 */
	public BusbarCon removeExistingConnections(){
		// This caues massive recursion when called inside the constructor, so has to be called seperately!
		for(int i = 0;i < this.freePoints.size();i++){
			BusbarCon bus = getBus(this.freePoints.get(i));
			if(bus != null && bus.connectsTo(this)){
				this.freePoints.set(i, bus.pos);
			}else{
				this.freePoints.remove(i--);
			}
		}
		return this;
	}
	
	public boolean connectsTo(BusbarCon bus){
		return hasConnection(bus.pos);
	}
	
	public boolean hasConnection(BlockPos pos){
		for(int i = 0;i < this.freePoints.size();i++){
			BlockPos p = this.freePoints.get(i);
			if(p.equals(pos)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasNeigborBus(BlockPos pos){
		BusbarCon bus = getBus(pos);
		if(bus == null)
			return false;
		
		bus.removeExistingConnections();
		return bus.canConnectTo(this);
	}
	
	@Override
	public String toString(){
		return String.format("""
				BusbarCon(){
					Level: %s
					Pos: %s
					State: %s
					Connections: %s
				}""", this.level, this.pos, this.state, this.freePoints);
	}
}
