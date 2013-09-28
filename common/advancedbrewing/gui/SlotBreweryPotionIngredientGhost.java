package advancedbrewing.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBreweryPotionIngredientGhost extends Slot {

	public SlotBreweryPotionIngredientGhost(IInventory par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return isItemValidForSlot(par1ItemStack);
	}

	@Override
	public int getSlotStackLimit() {
		return 64;
	}

	public static boolean isItemValidForSlot(ItemStack par1ItemStack) {
		return par1ItemStack != null ? Item.itemsList[par1ItemStack.itemID].isPotionIngredient(par1ItemStack) : false;
	}
}