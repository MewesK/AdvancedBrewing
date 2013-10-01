/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.proxy;

import net.minecraftforge.client.MinecraftForgeClient;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.renderer.ItemAutoPotionRenderer;
import advancedbrewing.renderer.ItemGlintOverlayRenderer;
import advancedbrewing.renderer.ItemMachineRenderer;

public class ProxyClient extends Proxy {	
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.arrowPotionItem.itemID, new ItemGlintOverlayRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.autoPotionItem.itemID, new ItemAutoPotionRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.bucketPotionItem.itemID, new ItemGlintOverlayRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.breweryIdleBlock.blockID, new ItemMachineRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.infuserIdleBlock.blockID, new ItemMachineRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.vaporizerIdleBlock.blockID, new ItemMachineRenderer());
	}
}