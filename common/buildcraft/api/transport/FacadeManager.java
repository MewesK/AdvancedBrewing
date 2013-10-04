package buildcraft.api.transport;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

/**
 * You can use this if you wish, but FML InterModComms are recommended.
 * 
 * SYNTAX: add-facade:id@meta
 */
public class FacadeManager {
	private static Method addFacade;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addFacade(ItemStack is) {
		try {
			if (FacadeManager.addFacade == null) {
				Class facade = Class.forName("buildcraft.transport.ItemFacade");
				FacadeManager.addFacade = facade.getMethod("addFacade", ItemStack.class);
			}
			FacadeManager.addFacade.invoke(null, is);
		}
		catch (Exception ex) {
		}
	}
}
