/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import advancedbrewing.entity.EntityArrowPotion;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorArrowPotion extends BehaviorProjectileDispense {
	@Override
	public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
		World world = par1IBlockSource.getWorld();
		IPosition iposition = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
		EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
		IProjectile iprojectile = this.getProjectileEntity(world, iposition);
		((EntityArrowPotion) iprojectile).setPotionID(par2ItemStack.getItemDamage());
		iprojectile.setThrowableHeading((double) enumfacing.getFrontOffsetX(), (double) ((float) enumfacing.getFrontOffsetY() + 0.1F), (double) enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
		world.spawnEntityInWorld((Entity) iprojectile);
		par2ItemStack.splitStack(1);
		return par2ItemStack;
	}

	@Override
	protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
		EntityArrowPotion entityArrowPotion = new EntityArrowPotion(par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ());
		entityArrowPotion.canBePickedUp = 1;
		return entityArrowPotion;
	}
}
