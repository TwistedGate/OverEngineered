package twistedgate.overengineered.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ExternalModContent{
	public static final RegistryObject<Item> IE_ITEM_HAMMER = RegistryObject.create(ResourceUtils.ie("hammer"), ForgeRegistries.ITEMS);
	public static final RegistryObject<Item> IE_ITEM_WIRECUTTER = RegistryObject.create(ResourceUtils.ie("wirecutter"), ForgeRegistries.ITEMS);
	public static final RegistryObject<Item> IE_ITEM_SCREWDRIVER = RegistryObject.create(ResourceUtils.ie("screwdriver"), ForgeRegistries.ITEMS);
	
	public static void init(){
	}
	
	public static boolean isIEHammer(ItemStack stack){
		return isIEHammer(stack.getItem());
	}
	
	public static boolean isIEHammer(Item item){
		return item.equals(IE_ITEM_HAMMER.get());
	}
	
	public static boolean isIEScrewdriver(ItemStack stack){
		return isIEHammer(stack.getItem());
	}
	
	public static boolean isIEScrewdriver(Item item){
		return item.equals(IE_ITEM_SCREWDRIVER.get());
	}
	
	public static boolean isIEWirecutter(ItemStack stack){
		return isIEHammer(stack.getItem());
	}
	
	public static boolean isIEWirecutter(Item item){
		return item.equals(IE_ITEM_WIRECUTTER.get());
	}
}