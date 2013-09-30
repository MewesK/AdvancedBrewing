/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import advancedbrewing.PotionDefinition;
import advancedbrewing.utils.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPotion extends BlockFluidClassic {

	private static int MAX_SOURCEBLOCK_DISTANCE = 16;

	@SideOnly(Side.CLIENT)
	protected Icon iconStill;
	@SideOnly(Side.CLIENT)
	protected Icon iconFlow;

	public BlockPotion(int id, Fluid fluid, Material material) {
		super(id, fluid, material);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		PotionDefinition potionDefinition = Utils.getPotionDefinitionByBlock(this);
        return potionDefinition != null ? potionDefinition.getColor() : super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		return side != 0 && side != 1 ? this.iconFlow : this.iconStill;
	}
    
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.iconStill = iconRegister.registerIcon("advancedbrewing:still.potion");
		this.iconFlow = iconRegister.registerIcon("advancedbrewing:flow.potion");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (!world.isRemote && entity instanceof EntityLivingBase) {
			PotionDefinition potionDefinition = Utils.getPotionDefinitionByBlock(this);
			List<PotionEffect> list = Item.potion.getEffects(new ItemStack(Item.potion, 1, potionDefinition.getPotionID()));
			if (list != null && list.size() > 0) {
				boolean willHaveEffect = false;
				
				for (PotionEffect potioneffect : list) {
					if (!((EntityLivingBase) entity).isPotionActive(potioneffect.getPotionID())) {
						willHaveEffect = true;
						break;
					}
				}
				
				if (willHaveEffect) {
    				int[] sourceBlockCoords = this.findSourceBlockRecursive(world, x, y, z, MAX_SOURCEBLOCK_DISTANCE);
    
    				if (sourceBlockCoords != null) {
						Utils.applyPotionEffects(potionDefinition.getPotionID(), (EntityLivingBase) entity);
    
    					world.setBlockToAir(sourceBlockCoords[0], sourceBlockCoords[1], sourceBlockCoords[2]);
    				}
				}
			}
		}
	}

	private int[] findSourceBlockRecursive(World world, int x, int y, int z, int maxDistance) {
		int curBlockMetadata = world.getBlockMetadata(x, y, z);

		int[] nBlockCoords1 = this._findSourceBlockRecursive(world, blockID, x, y, z, x + 1, y, z, curBlockMetadata, maxDistance);
		if (nBlockCoords1 != null) {
			return nBlockCoords1;
		}
		int[] nBlockCoords2 = this._findSourceBlockRecursive(world, blockID, x, y, z, x - 1, y, z, curBlockMetadata, maxDistance);
		if (nBlockCoords2 != null) {
			return nBlockCoords2;
		}
		int[] nBlockCoords3 = this._findSourceBlockRecursive(world, blockID, x, y, z, x, y + 1, z, curBlockMetadata, maxDistance);
		if (nBlockCoords3 != null) {
			return nBlockCoords3;
		}
		int[] nBlockCoords4 = this._findSourceBlockRecursive(world, blockID, x, y, z, x, y - 1, z, curBlockMetadata, maxDistance);
		if (nBlockCoords4 != null) {
			return nBlockCoords4;
		}
		int[] nBlockCoords5 = this._findSourceBlockRecursive(world, blockID, x, y, z, x, y, z + 1, curBlockMetadata, maxDistance);
		if (nBlockCoords5 != null) {
			return nBlockCoords5;
		}
		int[] nBlockCoords6 = this._findSourceBlockRecursive(world, blockID, x, y, z, x, y, z - 1, curBlockMetadata, maxDistance);
		if (nBlockCoords6 != null) {
			return nBlockCoords6;
		}

		return null;
	}

	private int[] _findSourceBlockRecursive(World world, int blockID, int startX, int startY, int startZ, int curX, int curY, int curZ, int lastMetadata, int maxDistance) {
		if (startX == curX && startY == curY && startZ == curZ) {
			return null;
		}

		if (Math.abs(startX - curX) + Math.abs(startY - curY) + Math.abs(startZ - curZ) > maxDistance) {
			return null;
		}

		int curBlockMetadata = world.getBlockMetadata(curX, curY, curZ);
		int curBlockID = world.getBlockId(curX, curY, curZ);
		if (curBlockID == blockID && curBlockMetadata < lastMetadata) {
			if (curBlockMetadata == 0) {
				return new int[] { curX, curY, curZ };
			}
		}
		else {
			return null;
		}

		int[] nBlockCoords1 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX + 1, curY, curZ, curBlockMetadata, maxDistance);
		if (nBlockCoords1 != null) {
			return nBlockCoords1;
		}
		int[] nBlockCoords2 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX - 1, curY, curZ, curBlockMetadata, maxDistance);
		if (nBlockCoords2 != null) {
			return nBlockCoords2;
		}
		int[] nBlockCoords3 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX, curY + 1, curZ, curBlockMetadata, maxDistance);
		if (nBlockCoords3 != null) {
			return nBlockCoords3;
		}
		int[] nBlockCoords4 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX, curY - 1, curZ, curBlockMetadata, maxDistance);
		if (nBlockCoords4 != null) {
			return nBlockCoords4;
		}
		int[] nBlockCoords5 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX, curY, curZ + 1, curBlockMetadata, maxDistance);
		if (nBlockCoords5 != null) {
			return nBlockCoords5;
		}
		int[] nBlockCoords6 = this._findSourceBlockRecursive(world, blockID, startX, startY, startZ, curX, curY, curZ - 1, curBlockMetadata, maxDistance);
		if (nBlockCoords6 != null) {
			return nBlockCoords6;
		}

		return null;
	}
}
