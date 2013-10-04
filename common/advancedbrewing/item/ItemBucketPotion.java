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

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.utils.Localization;
import advancedbrewing.utils.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBucketPotion extends Item {

	@SideOnly(Side.CLIENT)
	protected Icon iconItem;
	@SideOnly(Side.CLIENT)
	protected Icon iconOverlay;

	public ItemBucketPotion(int i) {
		super(i);
		this.setCreativeTab(CreativeTabs.tabBrewing);
		this.setContainerItem(Item.bucketEmpty);
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		Item.potion.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int par1) {
		return PotionHelper.func_77915_a(par1, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return par2 > 0 ? 16777215 : this.getColorFromDamage(par1ItemStack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		return this.iconItem;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamageForRenderPass(int par1, int par2) {
		return par2 == 0 ? this.iconOverlay : super.getIconFromDamageForRenderPass(par1, par2);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return Localization.get(this.getUnlocalizedName(itemstack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		boolean first = true;
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			if (first) {
				first = false;
				continue;
			}
			par3List.add(new ItemStack(this.itemID, 1, potionDefinition.getPotionID()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "item.bucket." + Utils.getPotionDefinitionByPotionID(itemstack.getItemDamage()).getName() + ".name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemStack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.iconItem = iconRegister.registerIcon("advancedbrewing:bucket");
		this.iconOverlay = iconRegister.registerIcon("advancedbrewing:bucket.overlay");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, false);

		if (movingobjectposition == null) {
			return par1ItemStack;
		}
		else {
			FillBucketEvent event = new FillBucketEvent(par3EntityPlayer, par1ItemStack, par2World, movingobjectposition);
			if (MinecraftForge.EVENT_BUS.post(event)) {
				return par1ItemStack;
			}

			if (event.getResult() == Event.Result.ALLOW) {
				if (par3EntityPlayer.capabilities.isCreativeMode) {
					return par1ItemStack;
				}

				if (--par1ItemStack.stackSize <= 0) {
					return event.result;
				}

				if (!par3EntityPlayer.inventory.addItemStackToInventory(event.result)) {
					par3EntityPlayer.dropPlayerItem(event.result);
				}

				return par1ItemStack;
			}

			if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY;
				int k = movingobjectposition.blockZ;

				if (!par2World.canMineBlock(par3EntityPlayer, i, j, k)) {
					return par1ItemStack;
				}

				if (movingobjectposition.sideHit == 0) {
					--j;
				}

				if (movingobjectposition.sideHit == 1) {
					++j;
				}

				if (movingobjectposition.sideHit == 2) {
					--k;
				}

				if (movingobjectposition.sideHit == 3) {
					++k;
				}

				if (movingobjectposition.sideHit == 4) {
					--i;
				}

				if (movingobjectposition.sideHit == 5) {
					++i;
				}

				if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack)) {
					return par1ItemStack;
				}

				if (this.tryPlaceContainedLiquid(par2World, i, j, k, par1ItemStack) && !par3EntityPlayer.capabilities.isCreativeMode) {
					return new ItemStack(Item.bucketEmpty);
				}
			}

			return par1ItemStack;
		}
	}

	private boolean tryPlaceContainedLiquid(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack) {

		Material material = par1World.getBlockMaterial(par2, par3, par4);
		boolean flag = !material.isSolid();

		if (!par1World.isAirBlock(par2, par3, par4) && !flag) {
			return false;
		}
		else {
			if (par1World.provider.isHellWorld) {
				par1World.playSoundEffect(par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

				for (int l = 0; l < 8; ++l) {
					par1World.spawnParticle("largesmoke", par2 + Math.random(), par3 + Math.random(), par4 + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			}
			else {
				if (!par1World.isRemote && flag && !material.isLiquid()) {
					par1World.destroyBlock(par2, par3, par4, true);
				}
				PotionDefinition potionDefinition = Utils.getPotionDefinitionByPotionID(par5ItemStack.getItemDamage());
				if (potionDefinition != null && potionDefinition.getBlock() != null) {
					par1World.setBlock(par2, par3, par4, potionDefinition.getBlock().blockID, 0, 3);
				}
			}

			return true;
		}
	}
}
