/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.utils.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArrowPotion extends ItemBucketPotion {

	public ItemArrowPotion(int itemID) {
		super(itemID);
		this.setContainerItem(null);
		this.setMaxStackSize(64);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			if (potionDefinition.getName().equals("water")) {
				continue;
			}
			par3List.add(new ItemStack(this.itemID, 1, potionDefinition.getPotionID()));
		}
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitionsSplash) {
			if (potionDefinition.getName().equals("water")) {
				continue;
			}
			par3List.add(new ItemStack(this.itemID, 1, potionDefinition.getPotionID()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "item.arrow." + Utils.getPotionDefinitionByPotionID(itemstack.getItemDamage(), true).getName() + ".name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.iconItem = iconRegister.registerIcon("advancedbrewing:arrow");
		this.iconOverlay = iconRegister.registerIcon("advancedbrewing:arrow.overlay");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		return par1ItemStack;
	}
}