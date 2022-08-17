package twistedgate.overengineered.utils;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;
import twistedgate.overengineered.OverEngineered;

public class ResourceUtils{
	public static ResourceLocation oe(String str){
		return new ResourceLocation(OverEngineered.MODID, str);
	}
	
	public static ResourceLocation ct(String str){
		return new ResourceLocation(CraftTweakerConstants.MOD_ID, str);
	}
	
	public static ResourceLocation ie(String str){
		return new ResourceLocation(Lib.MODID, str);
	}
	
	public static ResourceLocation forge(String str){
		return new ResourceLocation(ForgeVersion.MOD_ID, str);
	}
	
	public static ResourceLocation mc(String str){
		return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, str);
	}
}
