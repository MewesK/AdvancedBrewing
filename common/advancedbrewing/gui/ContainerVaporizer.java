package advancedbrewing.gui;

import net.minecraft.entity.player.InventoryPlayer;
import advancedbrewing.entity.TileEntityVaporizer;

public class ContainerVaporizer extends ContainerMachine<TileEntityVaporizer> {

	public ContainerVaporizer(InventoryPlayer inventoryPlayer, TileEntityVaporizer tileEntity) {
		super(inventoryPlayer, tileEntity);

		this.addSlotToContainer(new SlotBreweryPotionContainer(tileEntity, 0, 54, 41));
	}
}