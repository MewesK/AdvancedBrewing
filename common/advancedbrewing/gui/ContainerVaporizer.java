package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import advancedbrewing.tileentity.TileEntityVaporizer;

public class ContainerVaporizer extends ContainerMachine<TileEntityVaporizer> {

	public ContainerVaporizer(InventoryPlayer inventoryPlayer, TileEntityVaporizer tileEntity) {
		super(inventoryPlayer, tileEntity, 0);

		// input slot
		this.addSlotToContainer(new SlotBreweryPotionContainer(tileEntity, 0, 54, 41));
	}
}