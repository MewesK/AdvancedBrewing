/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import advancedbrewing.utils.Utils;

public class HandlerBucket {

	@ForgeSubscribe
	public void onBucketFill(FillBucketEvent event) {
		int blockID = event.world.getBlockId(event.target.blockX, event.target.blockY, event.target.blockZ);
		PotionDefinition potionDefinition = Utils.getPotionDefinitionByBlock(blockID);
		if (potionDefinition != null && event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) == 0) {
			event.world.setBlock(event.target.blockX, event.target.blockY, event.target.blockZ, 0);
			event.result = new ItemStack(AdvancedBrewing.bucketPotionItem, 1, potionDefinition.getPotionID());
			event.setResult(Result.ALLOW);
		}
	}
}