package twistedgate.overengineered.common.blocks;

import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ICollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ISelectionBounds;
import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import blusunrize.immersiveengineering.common.blocks.IEMultiblockBlock;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import twistedgate.overengineered.common.blocks.tileentity.CustomKineticBlockEntity;
import twistedgate.overengineered.common.blocks.tileentity.KineticMultiblockPartTileEntity;

/**
 * This has a lot of things copied-pasted from {@link IEMultiblockBlock} and {@link IEEntityBlock}, but only the important bits.
 * 
 * @author TwistedGate
 */
public abstract class CustomHorizontalAxisKineticBlock<T extends KineticMultiblockPartTileEntity<? super T>> extends HorizontalAxisKineticBlock implements EntityBlock{
	protected int lightOpacity;
	private final BiFunction<BlockPos, BlockState, T> makeEntity;
	protected final boolean notNormalBlock;
	public CustomHorizontalAxisKineticBlock(MultiblockBEType<T> entityType, Properties props){
		super(props);
		this.makeEntity = entityType;
		this.notNormalBlock = !defaultBlockState().canOcclude();
		
		this.registerDefaultState(getInitDefaultState());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE);
	}
	
	public CustomHorizontalAxisKineticBlock<T> setLightOpacity(int opacity){
		this.lightOpacity = opacity;
		return this;
	}
	
	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos){
		if(this.notNormalBlock)
			return 0;
		else if(state.isSolidRender(worldIn, pos))
			return this.lightOpacity;
		else
			return state.propagatesSkylightDown(worldIn, pos) ? 0 : 1;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos){
		return this.notNormalBlock ? 1 : super.getShadeBrightness(state, world, pos);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos){
		return this.notNormalBlock || super.propagatesSkylightDown(state, reader, pos);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int eventID, int eventParam){
		if(worldIn.isClientSide && eventID == 255){
			worldIn.sendBlockUpdated(pos, state, state, 3);
			return true;
		}
		return super.triggerEvent(state, worldIn, pos, eventID, eventParam);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		ItemStack activeStack = player.getItemInHand(hand);
		if(activeStack.is(IETags.hammers))
			return hammerUseSide(hit.getDirection(), player, hand, world, pos, hit);
		if(activeStack.is(IETags.screwdrivers))
			return screwdriverUseSide(hit.getDirection(), player, hand, world, pos, hit);
		return super.use(state, world, pos, player, hand, hit);
	}
	
	public InteractionResult hammerUseSide(Direction side, Player player, InteractionHand hand, Level w, BlockPos pos, BlockHitResult hit){
		return InteractionResult.PASS;
	}
	
	public InteractionResult screwdriverUseSide(Direction side, Player player, InteractionHand hand, Level w, BlockPos pos, BlockHitResult hit){
		return InteractionResult.PASS;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type){
		return false;
	}
	
	protected boolean canRotate(){
		// Basic heuristic: Multiblocks should not be rotated depending on state
		return !getStateDefinition().getProperties().contains(IEProperties.MULTIBLOCKSLAVE);
	}
	
	private static final List<BooleanProperty> DEFAULT_OFF = ImmutableList.of(IEProperties.MULTIBLOCKSLAVE, IEProperties.ACTIVE, IEProperties.MIRRORED);
	
	protected BlockState getInitDefaultState(){
		BlockState ret = this.stateDefinition.any();
		
		/*// ALL multiblocks i've seen thus far only have horizontal facing
		if(ret.hasProperty(IEProperties.FACING_ALL))
			ret = ret.setValue(IEProperties.FACING_ALL, getDefaultFacing());
		else*/
		if(ret.hasProperty(IEProperties.FACING_HORIZONTAL))
			ret = ret.setValue(IEProperties.FACING_HORIZONTAL, getDefaultFacing());
		
		if(ret.hasProperty(HorizontalAxisKineticBlock.HORIZONTAL_AXIS))
			ret = ret.setValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS, getDefaultFacing().getAxis());
		
		for(BooleanProperty defaultOff:DEFAULT_OFF)
			if(ret.hasProperty(defaultOff))
				ret = ret.setValue(defaultOff, false);
		return ret;
	}
	
	protected Direction getDefaultFacing(){
		return Direction.NORTH;
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.getBlock() != newState.getBlock()){
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if(blockEntity instanceof CustomKineticBlockEntity baseTE)
				baseTE.setOverrideState(state);
			if(blockEntity instanceof KineticMultiblockPartTileEntity<?> multiblockTE){
				// Remove the BE here before disassembling: The block is already gone, so setting the block state here
				// to a block providing a BE will produce strange results otherwise
				super.onRemove(state, world, pos, newState, isMoving);
				multiblockTE.disassemble();
				return;
			}
		}
		
		{
			BlockEntity te = world.getBlockEntity(pos);
			if(state.getBlock() != newState.getBlock()){
				if(te instanceof CustomKineticBlockEntity custom)
					custom.setOverrideState(state);
				if(te instanceof IHasDummyBlocks hasDummyBlocks)
					hasDummyBlocks.breakDummies(pos, state);
			}
		}
		
		super.onRemove(state, world, pos, newState, isMoving);
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving){
		if(!pLevel.isClientSide){
			BlockEntity te = pLevel.getBlockEntity(pPos);
			if(te instanceof CustomKineticBlockEntity custom){
				custom.onNeighborBlockChange(pFromPos);
			}
		}
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof KineticMultiblockPartTileEntity<?> mb)
			return Utils.getPickBlock(mb.getOriginalBlock(), target, player);
		return ItemStack.EMPTY;
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		if(state.getBlock()==this)
		{
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof ISelectionBounds bounds)
				return bounds.getSelectionShape(context);
		}
		return super.getShape(state, world, pos, context);
	}
	
	private LazyOptional<Boolean> lazyCol;
	private final boolean hasCustomCollisions(){
		if(this.lazyCol == null){
			this.lazyCol = LazyOptional.of(() -> {
				T tmp = this.makeEntity.apply(BlockPos.ZERO, getInitDefaultState());
				return tmp instanceof IEBlockInterfaces.ICollisionBounds;
			});
		}
		return this.lazyCol.orElse(false);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		if(hasCustomCollisions())
		{
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof ICollisionBounds collisionBounds)
				return collisionBounds.getCollisionShape(context);
			else
				// Temporary hack: The vanilla Entity#isInWall passes nonsense positions to this method (always the head
				// center rather than the actual block). This stops our blocks from suffocating people when this happens
				return Shapes.empty();
		}
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos)
	{
		if(world.getBlockState(pos).getBlock()==this)
		{
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof ISelectionBounds selectionBounds)
				return selectionBounds.getSelectionShape(null);
		}
		return super.getInteractionShape(state, world, pos);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		// Don't add multiblocks to the creative tab/JEI
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		return this.makeEntity.apply(pPos, pState);
	}
}
