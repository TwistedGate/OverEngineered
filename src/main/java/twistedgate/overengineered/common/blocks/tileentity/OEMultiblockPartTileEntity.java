package twistedgate.overengineered.common.blocks.tileentity;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class OEMultiblockPartTileEntity<T extends OEMultiblockPartTileEntity<T>> extends MultiblockPartBlockEntity<T>{
	
	public final AveragingEnergyStorage energyStorage;
	protected final MultiblockCapability<IEnergyStorage> energyCap;
	
	protected OEMultiblockPartTileEntity(IETemplateMultiblock multiblockInstance, int energyCapacity, boolean hasRSControl, BlockEntityType<? extends T> type, BlockPos pos, BlockState state){
		super(multiblockInstance, type, hasRSControl, pos, state);
		this.energyStorage = new AveragingEnergyStorage(energyCapacity);
		this.energyCap = MultiblockCapability.make(this,  be -> be.energyCap, OEMultiblockPartTileEntity::master, registerEnergyInput(this.energyStorage));
	}
}
