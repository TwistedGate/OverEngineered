package twistedgate.overengineered.common.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import twistedgate.overengineered.OverEngineered;

public class OEItemTags extends ItemTagsProvider{
	public OEItemTags(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper){
		super(pGenerator, pBlockTagsProvider, OverEngineered.MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags(){
		
	}
}
