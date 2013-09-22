package advancedbrewing;

import java.util.List;
import java.util.Random;

import advancedbrewing.entity.EntityArrowPotion;
import advancedbrewing.utils.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class HandlerBow {

	protected static final Random rand = new Random();

	@ForgeSubscribe
	public void onArrowNock(ArrowNockEvent event) {
		if (event.entityPlayer.inventory.hasItem(AdvancedBrewing.arrowPotionItem.itemID)) {
			event.entityPlayer.setItemInUse(event.result, Item.bow.getMaxItemUseDuration(event.result));
			event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onArrowLoose(ArrowLooseEvent event) {
		if (event.entityPlayer.inventory.hasItem(AdvancedBrewing.arrowPotionItem.itemID)) {
			int inventorySlot = -1;

			for (int i = 0; i < event.entityPlayer.inventory.mainInventory.length; ++i) {
				if (event.entityPlayer.inventory.mainInventory[i] != null && event.entityPlayer.inventory.mainInventory[i].itemID == AdvancedBrewing.arrowPotionItem.itemID) {
					inventorySlot = i;
					break;
				}
			}

			if (inventorySlot >= 0) {
				ItemStack itemStack = event.entityPlayer.inventory.getStackInSlot(inventorySlot);

				float charge = event.charge / 20.0F;
				charge = (charge * charge + charge * 2.0F) / 3.0F;

				if (charge < 0.1D) {
					return;
				}

				if (charge > 1.0F) {
					charge = 1.0F;
				}

				EntityArrowPotion entityarrow = new EntityArrowPotion(event.entityPlayer.worldObj, event.entityPlayer, charge * 2.0F);
				entityarrow.setPotionID(itemStack.getItemDamage());

				if (charge == 1.0F) {
					entityarrow.setIsCritical(true);
				}

				int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, event.bow);
				if (k > 0) {
					entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);
				}

				int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, event.bow);
				if (l > 0) {
					entityarrow.setKnockbackStrength(l);
				}

				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0) {
					entityarrow.setFire(100);
				}

				event.bow.damageItem(1, event.entityPlayer);
				event.entityPlayer.worldObj.playSoundAtEntity(event.entityPlayer, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + charge * 0.5F);
				event.entityPlayer.inventory.consumeInventoryItem(AdvancedBrewing.arrowPotionItem.itemID);

				if (!event.entityPlayer.worldObj.isRemote) {
					event.entityPlayer.worldObj.spawnEntityInWorld(entityarrow);
				}

				event.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
	@SuppressWarnings("unchecked")
	public void onLivingAttack(LivingAttackEvent event) {
		Entity sourceOfDamage = event.source.getSourceOfDamage();
		if (sourceOfDamage != null && sourceOfDamage instanceof EntityArrowPotion) {
			int potionID = ((EntityArrowPotion) sourceOfDamage).getPotionID();
			if (potionID >= 0) {
				event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
				if (!ItemPotion.isSplash(potionID)) {
					Utils.applyPotionEffects(potionID, event.entityLiving);
				}
				else {
					if (!event.entityLiving.worldObj.isRemote) {
						List<PotionEffect> potionEffects = Item.potion.getEffects(potionID);
						if (potionEffects != null && !potionEffects.isEmpty()) {
							AxisAlignedBB axisalignedbb = sourceOfDamage.boundingBox.expand(4.0D, 2.0D, 4.0D);
							List<EntityLivingBase> entityLivingBases = event.entityLiving.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
							if (entityLivingBases != null && !entityLivingBases.isEmpty()) {
								Utils.applyPotionEffects(potionID, entityLivingBases);
							}
						}
						event.entityLiving.worldObj.playAuxSFX(2002, (int) Math.round(sourceOfDamage.posX), (int) Math.round(sourceOfDamage.posY), (int) Math.round(sourceOfDamage.posZ), potionID);
					}
				}
				sourceOfDamage.setDead();
				event.setCanceled(true);
			}
		}
	}
}
