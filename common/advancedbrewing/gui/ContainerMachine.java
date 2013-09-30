/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import advancedbrewing.tileentity.TileEntityMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerMachine<T extends TileEntityMachine> extends Container {

	protected T tileEntity;
	protected float lastStoredEnergy;
	protected float lastRecentEnergyAverage;
	protected float lastCurrentInput;
	protected int lastWorkTime;
	protected int[] lastFluidIDs;
	protected int[] lastFluidAmounts;

	public ContainerMachine(InventoryPlayer inventoryPlayer, T tileEntity, int inventoryOffsetY) {
		int tankCount = tileEntity.getFluidTanks().length;
		
		this.tileEntity = tileEntity;
		this.lastFluidIDs = new int[tankCount];
		this.lastFluidAmounts = new int[tankCount];

		// add player inventory slots
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + inventoryOffsetY + i * 18));
			}
		}

		// add player hotbar slots
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142 + inventoryOffsetY));
		}
	}

	@Override
	public void detectAndSendChanges() {
		
		// send updated inventory
		
		super.detectAndSendChanges();

		// send updated custom values

		float storedEnergy = (int) this.tileEntity.getPowerHandler().getEnergyStored();
		float recentEnergyAverage = this.tileEntity.calculateRecentEnergyAverage();
		float currentInput = this.tileEntity.getCurrentInput();
		int workTime = this.tileEntity.getWorkTime();
		FluidTank[] fluidTanks = this.tileEntity.getFluidTanks();
		
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastStoredEnergy != storedEnergy) {
				icrafting.sendProgressBarUpdate(this, 0, (int)(storedEnergy * 100));
			}
			
			if (this.lastRecentEnergyAverage != recentEnergyAverage) {
				icrafting.sendProgressBarUpdate(this, 1, (int)(recentEnergyAverage * 100));
			}
			
			if (this.lastCurrentInput != currentInput) {
				icrafting.sendProgressBarUpdate(this, 2, (int)(currentInput * 100));
			}

			if (this.lastWorkTime != workTime) {
				icrafting.sendProgressBarUpdate(this, 3, workTime);
			}
			
			for (int j = 0; j < this.lastFluidIDs.length; j++) {
				FluidStack newFluid = fluidTanks[j].getFluid();
				int newFluidID = newFluid != null ? newFluid.fluidID : -1;
				if (this.lastFluidIDs[j] != newFluidID) {
					icrafting.sendProgressBarUpdate(this, 100 + j, newFluidID);
				}

				int newFluidAmount = fluidTanks[j].getFluidAmount();
				if (this.lastFluidAmounts[j] != newFluidAmount) {
					icrafting.sendProgressBarUpdate(this, 200 + j, newFluidAmount);
				}
			}
		}

		// save updated values
		
		this.lastStoredEnergy = storedEnergy;
		this.lastRecentEnergyAverage = recentEnergyAverage;
		this.lastCurrentInput = currentInput;
		this.lastWorkTime = workTime;

		for (int j = 0; j < this.lastFluidAmounts.length; j++) {
			FluidStack newFluid = fluidTanks[j].getFluid();
			this.lastFluidIDs[j] = newFluid != null ? newFluid.fluidID : -1;
			this.lastFluidAmounts[j] = fluidTanks[j].getFluidAmount();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		// set properties
		switch (id) {
			case 0:
				this.tileEntity.getPowerHandler().setEnergy(value / 100);
				return;
			case 1:
				this.tileEntity.setRecentEnergyAverage(value / 100);
				return;
			case 2:
				this.tileEntity.setCurrentInput(value / 100);
				return;
			case 3:
					this.tileEntity.setWorkTime(value);
					return;
		}
		
		FluidTank[] fluidTanks = this.tileEntity.getFluidTanks();
		
		// set fluid ids
		if (id >= 100 && id < 200) {
			if (fluidTanks[id - 100].getFluid() == null) {
				if (value >= 0) {
					fluidTanks[id - 100].setFluid(new FluidStack(value, 0));
				}
			}
			else {
				fluidTanks[id - 100].getFluid().fluidID = value;
			}
			return;
		}
		
		// set fluid amounts
		if (id >= 200 && id < 300) {
			if (fluidTanks[id - 200].getFluid() != null) {
				fluidTanks[id - 200].getFluid().amount = value;
			}
			return;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileEntity.isUseableByPlayer(player);
	}

	@Override
	// TODO
	public ItemStack transferStackInSlot(EntityPlayer player, int par2) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 == 2) {
				if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 1 && par2 != 0) {
				if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null) {
					if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				}
				else if (TileEntityFurnace.isItemFuel(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				}
				else if (par2 >= 3 && par2 < 30) {
					if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				}
				else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			}
			else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}
}