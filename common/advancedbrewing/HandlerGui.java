/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import advancedbrewing.gui.ContainerBrewery;
import advancedbrewing.gui.ContainerInfuser;
import advancedbrewing.gui.ContainerVaporizer;
import advancedbrewing.gui.GuiBrewery;
import advancedbrewing.gui.GuiInfuser;
import advancedbrewing.gui.GuiVaporizer;
import advancedbrewing.tileentity.TileEntityBrewery;
import advancedbrewing.tileentity.TileEntityInfuser;
import advancedbrewing.tileentity.TileEntityVaporizer;
import cpw.mods.fml.common.network.IGuiHandler;

public class HandlerGui implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityBrewery) {
			System.out.println("Is multiblock structure: " + ((TileEntityBrewery) tileEntity).checkIfProperlyFormed());
			return new ContainerBrewery(player.inventory, (TileEntityBrewery) tileEntity);
		}
		if (tileEntity instanceof TileEntityInfuser) {
			return new ContainerInfuser(player.inventory, (TileEntityInfuser) tileEntity);
		}
		if (tileEntity instanceof TileEntityVaporizer) {
			return new ContainerVaporizer(player.inventory, (TileEntityVaporizer) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityBrewery) {
			((TileEntityBrewery) tileEntity).checkIfProperlyFormed();
			return new GuiBrewery(player.inventory, (TileEntityBrewery) tileEntity);
		}
		if (tileEntity instanceof TileEntityInfuser) {
			return new GuiInfuser(player.inventory, (TileEntityInfuser) tileEntity);
		}
		if (tileEntity instanceof TileEntityVaporizer) {
			return new GuiVaporizer(player.inventory, (TileEntityVaporizer) tileEntity);
		}
		return null;
	}
}