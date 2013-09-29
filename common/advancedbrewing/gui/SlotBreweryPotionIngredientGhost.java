package advancedbrewing.gui;

import net.minecraft.inventory.IInventory;

public class SlotBreweryPotionIngredientGhost extends SlotBreweryPotionIngredient {

	public SlotBreweryPotionIngredientGhost(IInventory par2IInventory, int par3, int par4, int par5) {
		super(par2IInventory, par3, par4, par5);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}