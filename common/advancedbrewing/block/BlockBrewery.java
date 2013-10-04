/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.block;

import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.tileentity.TileEntityBrewery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBrewery extends BlockMachine<TileEntityBrewery> {

	public BlockBrewery(int blockID, boolean isActive) {
		super(blockID, isActive);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return AdvancedBrewing.breweryIdleBlock.blockID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return AdvancedBrewing.breweryIdleBlock.blockID;
	}

	@Override
	public void updateBlockState(boolean isBrewing, World par1World, int par2, int par3, int par4) {
		int metadata = par1World.getBlockMetadata(par2, par3, par4);
		TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
		BlockMachine.keepInventory = true;
		par1World.setBlock(par2, par3, par4, isBrewing ? AdvancedBrewing.breweryBurningBlock.blockID : AdvancedBrewing.breweryIdleBlock.blockID);
		BlockMachine.keepInventory = false;
		par1World.setBlockMetadataWithNotify(par2, par3, par4, metadata, 2);
		if (tileentity != null) {
			tileentity.validate();
			par1World.setBlockTileEntity(par2, par3, par4, tileentity);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityBrewery();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("advancedbrewing:brewery.side");
		this.iconFront = par1IconRegister.registerIcon(this.isActive ? "advancedbrewing:brewery.front.burning" : "advancedbrewing:brewery.front.idle");
		this.iconTop = par1IconRegister.registerIcon("advancedbrewing:brewery.top");
	}
}
