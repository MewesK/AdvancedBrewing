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
import net.minecraft.nbt.NBTTagCompound;
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
	private int radius = 1;

	public TileEntityVaporizer() {
		this.itemStacks = new ItemStack[1];
		this.fluidTanks = new FluidTank[1];

		this.fluidTanks[0] = new FluidTank(MAX_FLUIDAMOUNT);

		this.setPowerHandler(new PowerHandler(this, Type.MACHINE));
		this.getPowerHandler().configure(1, 100, 1, 1000);
		this.getPowerHandler().configurePowerPerdition(0, 0);
	}

	// TileEntitySynchronized

	@Override
	protected void readCustomFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readCustomFromNBT(par1NBTTagCompound);

		this.radius = par1NBTTagCompound.getShort("Radius");
	}

	@Override
	protected void writeCustomToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeCustomToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setShort("Radius", (short) this.radius);
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
		if (this.fluidTanks[0] != null && this.fluidTanks[0].getFluidAmount() >= this.radius * FluidContainerRegistry.BUCKET_VOLUME) {
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
			int radius = this.radius - 1;
			int x1 = this.xCoord;
			int y1 = this.yCoord;
			int z1 = this.zCoord;		
			int x2 = this.xCoord + 1;
			int y2 = this.yCoord + 1;
			int z2 = this.zCoord + 1;

			if (this.blockMetadata == BlockMachine.DIR_BOTTOM) {		
				x1 -= radius;
				y1 -= 1;
				z1 -= radius;		
				x2 += radius;
				y2 -= 1;
				z2 += radius;
			}
			else if (this.blockMetadata == BlockMachine.DIR_TOP) {
				x1 -= radius;
				y1 += 1;
				z1 -= radius;	
				x2 += radius;
				y2 += 1;
				z2 += radius;
			}
			else if (this.blockMetadata == BlockMachine.DIR_SOUTH) {
				x1 -= radius;
				y1 -= radius;
				z1 -= 1;		
				x2 += radius;
				y2 += radius;
				z2 -= 1;
			}
			else if (this.blockMetadata == BlockMachine.DIR_NORTH) {
				x1 -= radius;
				y1 -= radius;
				z1 += 1;		
				x2 += radius;
				y2 += radius;
				z2 += 1;
			}
			else if (this.blockMetadata == BlockMachine.DIR_EAST) {
				x1 -= 1;
				y1 -= radius;
				z1 -= radius;
				x2 -= 1;
				y2 += radius;
				z2 += radius;
			}
			else if (this.blockMetadata == BlockMachine.DIR_WEST) {
				x1 += 1;
				y1 -= radius;
				z1 -= radius;
				x2 += 1;
				y2 += radius;
				z2 += radius;
			}

			AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x1, y1, z1, x2, y2, z2);
			
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
					this.fluidTanks[0].drain(this.radius * FluidContainerRegistry.BUCKET_VOLUME, true);
					
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

	// getter/setter
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}