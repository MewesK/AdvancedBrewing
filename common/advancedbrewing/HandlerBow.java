package advancedbrewing;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
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
		boolean flag = event.entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, event.bow) > 0;

		if (flag || event.entityPlayer.inventory.hasItem(AdvancedBrewing.arrowPotionItem.itemID)) {
			int charge = event.charge;
			float f = (float) charge / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D) {
				return;
			}

			if (f > 1.0F) {
				f = 1.0F;
			}

			EntityArrow entityarrow = new EntityArrow(event.entityPlayer.worldObj, event.entityPlayer, f * 2.0F);

			if (f == 1.0F) {
				entityarrow.setIsCritical(true);
			}

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, event.bow);
			if (k > 0) {
				entityarrow.setDamage(entityarrow.getDamage() + (double) k * 0.5D + 0.5D);
			}

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, event.bow);
			if (l > 0) {
				entityarrow.setKnockbackStrength(l);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0) {
				entityarrow.setFire(100);
			}

			event.bow.damageItem(1, event.entityPlayer);
			event.entityPlayer.worldObj.playSoundAtEntity(event.entityPlayer, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (flag) {
				entityarrow.canBePickedUp = 2;
			}
			else {
				event.entityPlayer.inventory.consumeInventoryItem(AdvancedBrewing.arrowPotionItem.itemID);
			}

			if (!event.entityPlayer.worldObj.isRemote) {
				event.entityPlayer.worldObj.spawnEntityInWorld(entityarrow);
			}

    		event.setCanceled(true);
    		event.charge = charge;
		}
	}
}
