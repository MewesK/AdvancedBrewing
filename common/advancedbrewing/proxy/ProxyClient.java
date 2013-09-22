package advancedbrewing.proxy;

import net.minecraftforge.client.MinecraftForgeClient;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.renderer.ItemAutoPotionRenderer;
import advancedbrewing.renderer.ItemGlintOverlayRenderer;

public class ProxyClient extends Proxy {	
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.bucketPotionItem.itemID, new ItemGlintOverlayRenderer());
		MinecraftForgeClient.registerItemRenderer(AdvancedBrewing.autoPotionItem.itemID, new ItemAutoPotionRenderer());
	}
}