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

import org.lwjgl.input.Keyboard;

import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.utils.Localization;
import advancedbrewing.utils.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemAutoPotion extends ItemBucketPotion {
	public static int MAX_FLUIDAMOUNT = FluidContainerRegistry.BUCKET_VOLUME * 64;

	@SideOnly(Side.CLIENT)
	protected Icon iconItemOn;

	@SideOnly(Side.CLIENT)
	protected Icon iconItemOff;

	public ItemAutoPotion(int i) {
		super(i);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamageForRenderPass(int par1, int par2) {
		return par2 == 0 ? this.iconOverlay : par2 == 1 ? this.iconItemOff : this.iconItemOn;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.drink;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 32;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "item.autopotion.name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.iconItemOn = iconRegister.registerIcon("advancedbrewing:autopotion.on");
		this.iconItemOff = iconRegister.registerIcon("advancedbrewing:autopotion.off");
		this.iconOverlay = iconRegister.registerIcon("advancedbrewing:autopotion.overlay");
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			return itemStack;
		}

		Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
		int fluidAmount = tag.getInteger("FluidAmount");

		if (fluid != null && fluidAmount - FluidContainerRegistry.BUCKET_VOLUME >= 0) {
			PotionDefinition potionDefinition = Utils.getPotionDefinitionByFluid(fluid);
			if (potionDefinition != null) {
				tag.setInteger("FluidAmount", fluidAmount - FluidContainerRegistry.BUCKET_VOLUME);
				itemStack.setTagCompound(tag);

				if (!world.isRemote) {
					Utils.applyPotionEffects(potionDefinition.getPotionID(), entityPlayer);
				}

				if (fluidAmount - FluidContainerRegistry.BUCKET_VOLUME <= 0) {
					return new ItemStack(AdvancedBrewing.autoPotionItem);
				}
			}
		}

		return itemStack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			return itemStack;
		}

		boolean activated = tag.getBoolean("Activated");

		if ((Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
			tag.setBoolean("Activated", !activated);
			itemStack.setTagCompound(tag);
		}
		else if (!activated) {
			Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
			if (fluid != null && tag.getInteger("FluidAmount") - FluidContainerRegistry.BUCKET_VOLUME >= 0) {
				entityPlayer.setItemInUse(itemStack, Item.potion.getMaxItemUseDuration(itemStack));
			}
		}

		return itemStack;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote && entity instanceof EntityPlayer) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				return;
			}

			boolean activated = tag.getBoolean("Activated");
			if (activated) {
				Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
				if (fluid != null && tag.getInteger("FluidAmount") - FluidContainerRegistry.BUCKET_VOLUME >= 0) {
					PotionDefinition potionDefinition = Utils.getPotionDefinitionByFluid(fluid);
					if (potionDefinition != null) {
    					List<PotionEffect> list = Item.potion.getEffects(new ItemStack(Item.potion, 1, potionDefinition.getPotionID()));
    					if (list != null && list.size() > 0) {
    						boolean willHaveEffect = false;
    
    						for (PotionEffect potioneffect : list) {
    							if (!((EntityLivingBase) entity).isPotionActive(potioneffect.getPotionID())) {
    								willHaveEffect = true;
    								break;
    							}
    						}
    
    						if (willHaveEffect) {
    							onEaten(itemStack, world, (EntityPlayer) entity);
    						}
    					}
					}
				}
			}
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List par3List, boolean par4) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			itemStack.writeToNBT(tag);
		}

		Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));

		par3List.add("Activated: " + tag.getBoolean("Activated"));
		par3List.add("Fluid: " + (fluid != null ? Localization.get(fluid.getName()) : ""));
		par3List.add("Amount: " + tag.getInteger("FluidAmount"));
		
		if (fluid != null) {
			par3List.add("");
		}

		super.addInformation(itemStack, entityPlayer, par3List, par4);
	}
}
