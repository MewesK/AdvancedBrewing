/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.block;

import advancedbrewing.AdvancedBrewing;
import advancedbrewing.tileentity.TileEntityMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class BlockMachine<T extends TileEntityMachine> extends BlockContainer {

	public static final int DIR_SOUTH = 0x02;
	public static final int DIR_EAST = 0x04;
	public static final int DIR_NORTH = 0x03;
	public static final int DIR_WEST = 0x05;
	public static final int[] DIRS = new int[] { DIR_SOUTH, DIR_WEST, DIR_NORTH, DIR_EAST };

	protected final Random rand = new Random();
	protected final boolean isActive;
	protected static boolean keepInventory;

	@SideOnly(Side.CLIENT)
	protected Icon iconTop;
	@SideOnly(Side.CLIENT)
	protected Icon iconFront;

	public BlockMachine(int blockID, boolean isActive) {
		super(blockID, Material.iron);
		this.isActive = isActive;
		this.setHardness(3.5F);
		this.setStepSound(Block.soundMetalFootstep);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		int k = metadata & 7;
		return side == k ? this.iconFront : (k != 1 && k != 0 ? (side != 1 && side != 0 ? this.blockIcon : this.iconTop) : this.iconTop);
	}

	@Override
	public abstract int idDropped(int par1, Random par2Random, int par3);

	@Override
	@SideOnly(Side.CLIENT)
	public abstract int idPicked(World world, int par2, int par3, int par4);

	@Override
	@SuppressWarnings("unchecked")
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
		if (world.isRemote) {
			return true;
		}

		T tileEntity = (T) world.getBlockTileEntity(par2, par3, par4);

		if (tileEntity == null || entityPlayer.isSneaking()) {
			return false;
		}

		entityPlayer.openGui(AdvancedBrewing.instance, 0, world, par2, par3, par4);

		return true;
	}

	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4) {
		super.onBlockAdded(world, par2, par3, par4);
		if (!world.isRemote) {
			int l = world.getBlockId(par2, par3, par4 - 1);
			int i1 = world.getBlockId(par2, par3, par4 + 1);
			int j1 = world.getBlockId(par2 - 1, par3, par4);
			int k1 = world.getBlockId(par2 + 1, par3, par4);
			byte dir = DIR_NORTH;

			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
				dir = DIR_NORTH;
			}

			if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
				dir = DIR_SOUTH;
			}

			if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
				dir = DIR_WEST;
			}

			if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
				dir = DIR_EAST;
			}

			world.setBlockMetadataWithNotify(par2, par3, par4, dir, 2);
		}
	}

	public abstract void updateBlockState(boolean isBrewing, World par1World, int par2, int par3, int par4);

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

	@Override
	public abstract TileEntity createTileEntity(World world, int metadata);

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		int dir = DIRS[MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3];
		par1World.setBlockMetadataWithNotify(par2, par3, par4, dir, 2);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6) {
		if (!keepInventory) {
			T tileEntity = (T) world.getBlockTileEntity(par2, par3, par4);

			if (tileEntity != null) {
				for (int j1 = 0; j1 < tileEntity.getSizeInventory(); ++j1) {
					ItemStack itemstack = tileEntity.getStackInSlot(j1);

					if (itemstack != null) {
						float f = this.rand.nextFloat() * 0.8F + 0.1F;
						float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
						float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

						while (itemstack.stackSize > 0) {
							int k1 = this.rand.nextInt(21) + 10;

							if (k1 > itemstack.stackSize) {
								k1 = itemstack.stackSize;
							}

							itemstack.stackSize -= k1;
							EntityItem entityitem = new EntityItem(world, par2 + f, par3 + f1, par4 + f2, new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

							if (itemstack.hasTagCompound()) {
								entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
							}

							float f3 = 0.05F;
							entityitem.motionX = (float) this.rand.nextGaussian() * f3;
							entityitem.motionY = (float) this.rand.nextGaussian() * f3 + 0.2F;
							entityitem.motionZ = (float) this.rand.nextGaussian() * f3;
							world.spawnEntityInWorld(entityitem);
						}
					}
				}

				world.func_96440_m(par2, par3, par4, par5);
			}
		}

		super.breakBlock(world, par2, par3, par4, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int par2, int par3, int par4, Random par5Random) {
		if (this.isActive) {
			int metadata = world.getBlockMetadata(par2, par3, par4);
			float f = par2 + 0.5F;
			float f1 = par3 + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
			float f2 = par4 + 0.5F;
			float f3 = 0.52F;
			float f4 = par5Random.nextFloat() * 0.6F - 0.3F;

			if (metadata == 2) {
				world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
			}
			else if (metadata == 3) {
				world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
			}
			else if (metadata == 4) {
				world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
			}
			else if (metadata == 5) {
				world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract void registerIcons(IconRegister par1IconRegister);
}
