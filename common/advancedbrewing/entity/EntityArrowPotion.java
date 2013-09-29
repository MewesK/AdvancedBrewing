package advancedbrewing.entity;

import java.lang.reflect.Field;
import advancedbrewing.AdvancedBrewing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityArrowPotion extends EntityArrow {

	private int potionID = -1;

	public EntityArrowPotion(World par1World) {
		super(par1World);
		this.setDamage(0);
	}

	public EntityArrowPotion(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		this.setDamage(0);
	}
	
	public EntityArrowPotion(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
		super(par1World, par2EntityLivingBase, par3);
		this.setDamage(0);
	}

	public EntityArrowPotion(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
		super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
		this.setDamage(0);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		try {
			Field f = this.getClass().getSuperclass().getDeclaredField("inGround");
			f.setAccessible(true);
			boolean inGround = f.getBoolean(this);
			
    		if (!this.worldObj.isRemote && inGround && this.arrowShake <= 0) {
    			boolean flag = this.canBePickedUp == 1;
        		if (this.canBePickedUp == 1) {
        			if (!par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(AdvancedBrewing.arrowPotionItem, 1, this.potionID))) {
        				flag = false;
        			}
    			}
       			if (flag) {
    				this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
    				par1EntityPlayer.onItemPickup(this, 1);
    				this.setDead();
    			}
    		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey("PotionID")) {
			this.potionID = par1NBTTagCompound.getInteger("PotionID");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("PotionID", this.potionID);
	}

	// getter / setter

	public int getPotionID() {
		return potionID;
	}

	public void setPotionID(int potionID) {
		this.potionID = potionID;
	}
}