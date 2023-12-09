package twistedgate.overengineered.common.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;
import twistedgate.overengineered.common.OEContent;

public class OEBlockTags extends BlockTagsProvider{
	public OEBlockTags(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper){
		super(pGenerator, OverEngineered.MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags(){
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
			OEContent.Multiblock.UNIVERSAL_MOTOR.get()
		);
	}
}
