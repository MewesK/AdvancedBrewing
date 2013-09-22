package advancedbrewing.item;

import advancedbrewing.utils.Localization;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockLocalized extends ItemBlock {

	public ItemBlockLocalized(int id) {
		super(id);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return Localization.get(getUnlocalizedName(itemstack));
	}
}