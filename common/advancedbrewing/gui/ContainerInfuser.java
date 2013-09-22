package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import advancedbrewing.entity.TileEntityInfuser;

public class ContainerInfuser extends ContainerMachine<TileEntityInfuser> {

	public ContainerInfuser(InventoryPlayer inventoryPlayer, TileEntityInfuser tileEntity) {
		super(inventoryPlayer, tileEntity);

		this.addSlotToContainer(new Slot(tileEntity, 0, 106, 31)); // additive
		this.addSlotToContainer(new Slot(tileEntity, 1, 106, 52)); // ingredient
		this.addSlotToContainer(new Slot(tileEntity, 2, 132, 41)); // output
		this.addSlotToContainer(new SlotBreweryPotionContainer(tileEntity, 3, 54, 41));
	}
}