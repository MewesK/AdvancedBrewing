/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.tileentity;

import java.util.List;

import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.block.BlockMachine;
import advancedbrewing.block.BlockVaporizer;
import advancedbrewing.gui.SlotBreweryPotionContainer;
import advancedbrewing.utils.Utils;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

public class TileEntityVaporizer extends TileEntityMachine {
	public static int MAX_WORKTIME = 10;

	// properties

	public TileEntityVaporizer() {
		this.itemStacks = new ItemStack[1];
		this.fluidTanks = new FluidTank[1];

		this.fluidTanks[0] = new FluidTank(MAX_FLUIDAMOUNT);

		this.setPowerHandler(new PowerHandler(this, Type.MACHINE));
		this.getPowerHandler().configure(1, 100, 1, 1000);
		this.getPowerHandler().configurePowerPerdition(0, 0);
	}

	// TileEntityPowered

	@Override
	public void doWork(PowerHandler workProvider) {
		if (this.worldObj.isRemote) {
			return;
		}
		
		float usedEnergy = 0;

		// process bucket slot (input)

		if (this.processContainerInput(0, 0, false)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process vaporization

		boolean working = this.workTime > 0;

		if (this.workTime > 0) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
			if (usedEnergy > 0) {
				this.workTime -= (int) usedEnergy;

				if (this.workTime == 0) {
					this.work();
				}
				else if (!this.canWork()) {
					this.workTime = 0;
				}
			}
		}
		else if (this.canWork()) {
			this.workTime = MAX_WORKTIME;
		}

		this.setCurrentEnergy((int) usedEnergy);

		// change block
		if (working != this.workTime > 0) {
			((BlockVaporizer) AdvancedBrewing.vaporizerIdleBlock).updateBlockState(this.workTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}

		// synchronize with client
		this.onInventoryChanged();
	}

	// TileEntityMachine

	@Override
	@SuppressWarnings("unchecked")
	protected boolean canWork() {
		if (this.fluidTanks[0] != null && this.fluidTanks[0].getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
			PotionDefinition potionDefinitionBase = Utils.getPotionDefinitionByFluid(this.fluidTanks[0].getFluid().getFluid());
			if (potionDefinitionBase == null) {
				return false;
			}

			int potionBase = potionDefinitionBase.getPotionID();
			if (potionBase < 0) {
				return false;
			}

			List<PotionEffect> potionEffects = Item.potion.getEffects(potionBase);
			if (potionEffects != null && potionEffects.size() > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	protected boolean work() {
		if (this.canWork()) {
			int radius = 1;
			
			int xOffset = 0;
			int yOffset = 0;
			int zOffset = 0;

			if (this.blockMetadata == BlockMachine.DIR_BOTTOM) {
				yOffset--;
			}
			else if (this.blockMetadata == BlockMachine.DIR_TOP) {
				yOffset++;
			}
			else if (this.blockMetadata == BlockMachine.DIR_SOUTH) {
				zOffset--;
			}
			else if (this.blockMetadata == BlockMachine.DIR_NORTH) {
				zOffset++;
			}
			else if (this.blockMetadata == BlockMachine.DIR_EAST) {
				xOffset--;
			}
			else if (this.blockMetadata == BlockMachine.DIR_WEST) {
				xOffset++;
			}

			AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(
					this.xCoord + xOffset + radius - 1, 
					this.yCoord + yOffset + radius - 1, 
					this.zCoord + zOffset + radius - 1, 
					this.xCoord + xOffset + 1 + radius - 1, 
					this.yCoord + yOffset + 1 + radius - 1, 
					this.zCoord + zOffset + 1 + radius - 1);
			
			List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
			if (entities != null && entities.size() > 0) {
    			PotionDefinition potionDefinitionBase = Utils.getPotionDefinitionByFluid(this.fluidTanks[0].getFluid().getFluid());
    			List<PotionEffect> potionEffects = Item.potion.getEffects(potionDefinitionBase.getPotionID());
				
				boolean willHaveEffect = false;
				for (PotionEffect potionEffect : potionEffects) {
					for (EntityLivingBase entity : entities) {
						if (!entity.isPotionActive(potionEffect.getPotionID())) {
							willHaveEffect = true;
							break;
						}
					}
				}
				if (willHaveEffect) {
					Utils.applyPotionEffects(potionDefinitionBase.getPotionID(), entities);
					this.fluidTanks[0].drain((radius * 2 - 1) * FluidContainerRegistry.BUCKET_VOLUME, true);
					
					return true;
				}
			}
		}
		
		return false;
	}

	// ISidedInventory

	@Override
	public String getInvName() {
		return "container.vaporizer";
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if (slotIndex == 0 && SlotBreweryPotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if (slotIndex == 0 && SlotBreweryPotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		return false;
	}

	// IFluidHandler

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.fluidTanks[0].fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.drain(from, FluidContainerRegistry.BUCKET_VOLUME, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.fluidTanks[0].drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { this.fluidTanks[0].getInfo() };
	}
}