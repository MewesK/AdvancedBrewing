package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import advancedbrewing.entity.TileEntityBrewery;

public class ContainerBrewery extends ContainerMachine<TileEntityBrewery> {

	public ContainerBrewery(InventoryPlayer inventoryPlayer, TileEntityBrewery tileEntity) {
		super(inventoryPlayer, tileEntity);

		this.addSlotToContainer(new SlotBreweryPotionIngredient(tileEntity, 0, 80, 20));
		this.addSlotToContainer(new SlotBreweryBasePotionContainer(tileEntity, 1, 18, 41));
		this.addSlotToContainer(new SlotBreweryEmptyContainer(tileEntity, 2, 142, 41));
	}
}