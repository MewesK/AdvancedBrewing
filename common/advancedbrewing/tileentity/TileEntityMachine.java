/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.tileentity;

import buildcraft.api.power.PowerHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.item.ItemAutoPotion;
import advancedbrewing.utils.Utils;

public abstract class TileEntityMachine extends TileEntityPowered implements ISidedInventory, IFluidHandler {
	public static int MAX_FLUIDAMOUNT = FluidContainerRegistry.BUCKET_VOLUME * 10;
	public static int MAX_WORKTIME = 0;

	// properties
	protected int workTime;
	protected ItemStack[] itemStacks;
	protected FluidTank[] fluidTanks;

	// TileEntitySynchronized

	@Override
	protected void readCustomFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readCustomFromNBT(par1NBTTagCompound);

		this.workTime = par1NBTTagCompound.getShort("WorkTime");
		
		NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("Slots");
		this.itemStacks = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < nbttaglist1.tagCount(); ++i) {
			NBTTagCompound tag = (NBTTagCompound) nbttaglist1.tagAt(i);
			byte index = tag.getByte("Slot");
			if (index >= 0 && index < this.itemStacks.length) {
				this.itemStacks[index] = ItemStack.loadItemStackFromNBT(tag);
			}
		}

		NBTTagList nbttaglist2 = par1NBTTagCompound.getTagList("Tanks");
		for (int i = 0; i < nbttaglist2.tagCount(); ++i) {
			NBTTagCompound tag = (NBTTagCompound) nbttaglist2.tagAt(i);
			byte index = tag.getByte("Tank");
			if (index >= 0 && index < this.fluidTanks.length) {
				this.fluidTanks[index].readFromNBT(tag);
			}
		}
	}

	@Override
	protected void writeCustomToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeCustomToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setShort("WorkTime", (short) this.workTime);
		
		NBTTagList nbttaglist1 = new NBTTagList();
		for (int i = 0; i < this.itemStacks.length; ++i) {
			if (this.itemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.itemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist1.appendTag(nbttagcompound1);
			}
		}
		par1NBTTagCompound.setTag("Slots", nbttaglist1);

		NBTTagList nbttaglist2 = new NBTTagList();
		for (int i = 0; i < this.fluidTanks.length; ++i) {
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setByte("Tank", (byte) i);
			this.fluidTanks[i].writeToNBT(nbttagcompound1);
			nbttaglist2.appendTag(nbttagcompound1);
		}
		par1NBTTagCompound.setTag("Tanks", nbttaglist2);
	}

	
	// TileEntityPowered

	@Override
	public abstract void doWork(PowerHandler workProvider);
	
	// ISidedInventory

	@Override
	public int getSizeInventory() {
		return this.itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return this.itemStacks[slotIndex];
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int par2) {
		if (this.itemStacks[slotIndex] != null) {
			ItemStack itemstack;

			if (this.itemStacks[slotIndex].stackSize <= par2) {
				itemstack = this.itemStacks[slotIndex];
				this.itemStacks[slotIndex] = null;
			}
			else {
				itemstack = this.itemStacks[slotIndex].splitStack(par2);
				if (this.itemStacks[slotIndex].stackSize == 0) {
					this.itemStacks[slotIndex] = null;
				}
			}

			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if (this.itemStacks[slotIndex] != null) {
			ItemStack itemstack = this.itemStacks[slotIndex];
			this.itemStacks[slotIndex] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack par2ItemStack) {
		this.itemStacks[slotIndex] = par2ItemStack;
		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public abstract String getInvName();

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public abstract boolean isItemValidForSlot(int slotIndex, ItemStack par2ItemStack);

	@Override
	public abstract int[] getAccessibleSlotsFromSide(int side);

	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
		return this.isItemValidForSlot(par1, par2ItemStack);
	}

	@Override
	public abstract boolean canExtractItem(int slotIndex, ItemStack par2ItemStack, int side);

	// IFluidHandler

	@Override
	public abstract int fill(ForgeDirection from, FluidStack resource, boolean doFill);

	@Override
	public abstract FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain);

	@Override
	public abstract FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain);

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (fluid == FluidRegistry.WATER) {
			return true;
		}
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			if (fluid == FluidRegistry.getFluid(potionDefinition.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			if (fluid == FluidRegistry.getFluid(potionDefinition.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public abstract FluidTankInfo[] getTankInfo(ForgeDirection from);

	// TileEntityMachine

	protected abstract boolean canWork();

	protected abstract boolean work();
	
	// helper
	
	protected boolean processContainerInput(int itemStackIndex, int fluidTankIndex, boolean allowBaseFluids) {
		if (itemStackIndex < 0 || itemStackIndex >= this.itemStacks.length || fluidTankIndex < 0 || fluidTankIndex >= this.fluidTanks.length) {
			return false;
		}
		ItemStack itemStack = this.itemStacks[itemStackIndex];
		FluidTank fluidTank = this.fluidTanks[fluidTankIndex];
		if (itemStack != null && itemStack.stackSize == 1 && fluidTank.getFluidAmount() + FluidContainerRegistry.BUCKET_VOLUME <= MAX_FLUIDAMOUNT) {
			itemStack = Utils.unreversePotionItemStack(itemStack);
			FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(itemStack);
			if (fluidStack != null && (fluidTank.getFluid() == null || fluidTank.getFluid().fluidID == fluidStack.fluidID)) {
				this.fluidTanks[fluidTankIndex].fill(fluidStack, true);
				this.itemStacks[itemStackIndex] = FluidContainerRegistry.isBucket(itemStack) ?  new ItemStack(Item.bucketEmpty) : new ItemStack(Item.glassBottle);
				return true;
			}
		}		
		return false;
	}
	
	protected boolean processContainerOutput(int itemStackInputIndex, int itemStackOutputIndex, int fluidTankIndex) {
		if (itemStackInputIndex < 0 || itemStackInputIndex >= this.itemStacks.length || itemStackOutputIndex < 0 || itemStackOutputIndex >= this.itemStacks.length || fluidTankIndex < 0 || fluidTankIndex >= this.fluidTanks.length) {
			return false;
		}
		ItemStack itemStack = this.itemStacks[itemStackInputIndex];
		FluidTank fluidTank = this.fluidTanks[fluidTankIndex];
		if (itemStack != null && itemStack.stackSize == 1 && fluidTank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
			if (itemStack.getItem().itemID == AdvancedBrewing.autoPotionItem.itemID) {
				NBTTagCompound tag = itemStack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					itemStack.writeToNBT(tag);
				}
				int fluidAmount = tag.getInteger("FluidAmount");
				Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
				if ((fluid == null || fluidTank.getFluid().getFluid() == fluid) && fluidAmount + FluidContainerRegistry.BUCKET_VOLUME <= ItemAutoPotion.MAX_FLUIDAMOUNT) {
					PotionDefinition potionDefinition = Utils.getPotionDefinitionByFluid(fluidTank.getFluid().getFluid());
					if (potionDefinition != null) {
						boolean success = false;
						if (itemStackInputIndex == itemStackOutputIndex) {
    						itemStack.setItemDamage(potionDefinition.getPotionID());
    						success = true;
    					} else {
    						if (this.itemStacks[itemStackOutputIndex] == null || (this.itemStacks[itemStackOutputIndex].itemID == itemStack.itemID && this.itemStacks[2].getItemDamage() == potionDefinition.getPotionID() && this.itemStacks[itemStackOutputIndex].stackSize + 1 <= this.itemStacks[itemStackOutputIndex].getMaxStackSize())) {
    							this.itemStacks[itemStackInputIndex] = null;
    			    			if (this.itemStacks[itemStackOutputIndex] == null) {
    			        			this.itemStacks[itemStackOutputIndex] = new ItemStack(Item.potion.itemID, 1, potionDefinition.getPotionID());
    			    			} else {
    			    				this.itemStacks[itemStackOutputIndex].stackSize++;
    			    			}
    			    			success = true;
    						} 
    					}
					
    					if (success) {
        					tag.setString("FluidName", fluidTank.getFluid().getFluid().getName());
        					tag.setInteger("FluidAmount", fluidAmount + FluidContainerRegistry.BUCKET_VOLUME);
        					fluidTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
        					itemStack.setTagCompound(tag);		
        					return true;
    					}
					}
				}
			}
			else if (itemStack.getItem().itemID == Item.bucketEmpty.itemID || itemStack.getItem().itemID == Item.glassBottle.itemID) {
				ItemStack itemStackFilled = FluidContainerRegistry.fillFluidContainer(fluidTank.getFluid(), itemStack);
				if (itemStack != null) {
					boolean success = false;
					if (itemStackInputIndex == itemStackOutputIndex) {
						this.itemStacks[itemStackInputIndex] = itemStackFilled;
						success = true;
					} else {
						if (this.itemStacks[itemStackOutputIndex] == null || (this.itemStacks[itemStackOutputIndex].itemID == itemStackFilled.itemID && this.itemStacks[2].getItemDamage() == itemStackFilled.getItemDamage() && this.itemStacks[itemStackOutputIndex].stackSize + itemStackFilled.stackSize <= this.itemStacks[itemStackOutputIndex].getMaxStackSize())) {
    						this.itemStacks[itemStackInputIndex] = null;
    		    			if (this.itemStacks[itemStackOutputIndex] == null) {
    		        			this.itemStacks[itemStackOutputIndex] = new ItemStack(Item.potion.itemID, 1, itemStackFilled.getItemDamage());
    		    			} else {
    		    				this.itemStacks[itemStackOutputIndex].stackSize += itemStackFilled.stackSize;
    		    			}
    		    			success = true;
    					}
					}
					
					if (success) {
						this.fluidTanks[fluidTankIndex].drain(FluidContainerRegistry.BUCKET_VOLUME, true);
    					return true;
					}
				}
			}
		}
		return false;
	}

	// getter / setter

	public int getWorkTime() {
		return workTime;
	}

	public void setWorkTime(int workTime) {
		this.workTime = workTime;
	}

	public ItemStack[] getItemStacks() {
		return itemStacks;
	}

	public void setItemStacks(ItemStack[] itemStacks) {
		this.itemStacks = itemStacks;
	}

	public FluidTank[] getFluidTanks() {
		return fluidTanks;
	}

	public void setFluidTanks(FluidTank[] fluidTanks) {
		this.fluidTanks = fluidTanks;
	}
}