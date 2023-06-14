package twistedgate.overengineered.common.blocks.tileentity;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import twistedgate.overengineered.OverEngineered;

public class TileTypes{
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, OverEngineered.MODID);
	
	static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntitySupplier<T> factory, Supplier<Block> valid){
		return REGISTER.register(name, () -> new BlockEntityType<T>(factory, ImmutableSet.of(valid.get()), null));
	}
}
