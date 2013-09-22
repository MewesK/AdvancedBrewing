package advancedbrewing.gui;

import advancedbrewing.AdvancedBrewing;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBreweryEmptyContainer extends Slot {
	public SlotBreweryEmptyContainer(IInventory par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return isItemValidForSlot(par1ItemStack);
	}
	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	public static boolean isItemValidForSlot(ItemStack par1ItemStack) {
		if (par1ItemStack != null) {
			Item item = par1ItemStack.getItem();
			return item.itemID == Item.glassBottle.itemID || item.itemID == Item.bucketEmpty.itemID || item.itemID == AdvancedBrewing.autoPotionItem.itemID;
		}
		return false;
	}
}