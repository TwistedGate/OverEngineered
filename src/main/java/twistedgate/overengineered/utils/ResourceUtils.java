package twistedgate.overengineered.utils;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;
import twistedgate.overengineered.OverEngineered;

public class ResourceUtils{
	/** OverEngineeered namespace */
	public static ResourceLocation oe(String str){
		return new ResourceLocation(OverEngineered.MODID, str);
	}
	
	/** CraftTweaker namespace */
	public static ResourceLocation ct(String str){
		return new ResourceLocation(CraftTweakerConstants.MOD_ID, str);
	}
	
	/** ImmersiveEngineering namespace */
	public static ResourceLocation ie(String str){
		return new ResourceLocation(Lib.MODID, str);
	}
	
	/** Forge namespace */
	public static ResourceLocation forge(String str){
		return new ResourceLocation(ForgeVersion.MOD_ID, str);
	}
	
	/** Minecraft namespace */
	public static ResourceLocation mc(String str){
		return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, str);
	}
}
