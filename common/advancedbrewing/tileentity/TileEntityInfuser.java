package advancedbrewing.tileentity;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.block.BlockInfuser;
import advancedbrewing.utils.Utils;

public class TileEntityInfuser extends TileEntityMachine {
	public static int MAX_WORKTIME = 100;

	// properties
	private int ingredientID;
	private int additiveID = -1;

	public TileEntityInfuser() {
		this.itemStacks = new ItemStack[4];
		this.fluidTanks = new FluidTank[1];

		this.fluidTanks[0] = new FluidTank(MAX_FLUIDAMOUNT);

		this.setPowerHandler(new PowerHandler(this, Type.MACHINE));
		this.getPowerHandler().configure(2, 100, 1, 5000);
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

		if (this.processContainerInput(3, 0, true)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process bucket slot (output)

		if (this.workTime == 0 && this.itemStacks[0] == null && this.processContainerOutput(1, 2, 0)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process infusion

		boolean working = this.workTime > 0;

		if (this.workTime > 0) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 2, true);
			if (usedEnergy > 1) {
				this.workTime -= (int) usedEnergy;

				if (this.workTime == 0) {
					this.work();
				}
				else if (!this.canWork()) {
					this.workTime = 0;
				}
				else if (this.itemStacks[1] == null || this.ingredientID != this.itemStacks[1].itemID || this.additiveID != (this.itemStacks[0] != null ? this.itemStacks[0].itemID : -1)) {
					this.workTime = 0;
				}
			}
		}
		else if (this.canWork()) {
			this.workTime = MAX_WORKTIME;
			this.ingredientID = this.itemStacks[1].itemID;
			this.additiveID = this.itemStacks[0] != null ? this.itemStacks[0].itemID : -1;
		}

		this.setCurrentEnergy((int) usedEnergy);

		// change block
		if (working != this.workTime > 0) {
			((BlockInfuser) AdvancedBrewing.infuserIdleBlock).updateBlockState(this.workTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}

		// synchronize with client
		this.onInventoryChanged();
	}

	// TileEntityMachine

	@Override
	protected boolean canWork() {
		// check fluidTank
		if (this.fluidTanks[0].getFluid() != null && this.fluidTanks[0].getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
			// check itemStacks
			if (this.itemStacks[1] != null && this.itemStacks[1].stackSize > 0 && (this.itemStacks[0] == null || (this.itemStacks[0].stackSize > 0 && this.itemStacks[0].itemID == Item.gunpowder.itemID))) {
				// splash potions
				if (this.itemStacks[1].itemID == Item.glassBottle.itemID) {
					// check result
					int potionResult = Utils.getPotionIDResult(this.fluidTanks[0].getFluid().getFluid(), this.itemStacks[0], true);
					if (potionResult > 0 && ItemPotion.isSplash(potionResult)) {
						// check output
						if (this.itemStacks[2] == null || (this.itemStacks[2].itemID == Item.potion.itemID && this.itemStacks[2].getItemDamage() == potionResult && this.itemStacks[2].stackSize + 1 <= this.itemStacks[2].getMaxStackSize())) {
							return true;
						}
					}
				}
				// TODO arrows
			}
		}
		return false;
	}

	@Override
	protected boolean work() {
		if (this.canWork()) {
			// create splash potions
			if (this.itemStacks[1].itemID == Item.glassBottle.itemID) {
				int potionResult = Utils.getPotionIDResult(this.fluidTanks[0].getFluid().getFluid(), this.itemStacks[0], true);

				this.fluidTanks[0].drain(FluidContainerRegistry.BUCKET_VOLUME, true);
    			decrStackSize(0, 1);
    			decrStackSize(1, 1);
    			
    			if (this.itemStacks[2] == null) {
        			this.itemStacks[2] = new ItemStack(Item.potion.itemID, 1, potionResult);
    			} else {
    				this.itemStacks[2].stackSize++;
    			}
			}
			// TODO create potion arrows
		}
		return false;
	}

	// ISidedInventory

	@Override
	public String getInvName() {
		return "container.infusor";
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack par2ItemStack) {
		if (slotIndex == 0) {
			return true;
		}
		if (slotIndex == 1 && (par2ItemStack.itemID == AdvancedBrewing.bucketPotionItem.itemID)) {
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		switch (side) {
			case 0:
				// bottom
				return new int[] { 2 };
			case 1:
				// top
				return new int[] { 1 };
			default:
				// sides
				return new int[] { 3 };
		}
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack par2ItemStack, int side) {
		if (slotIndex == 3) {
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

	// getter / setter

	public int getIngredientID() {
		return ingredientID;
	}

	public void setIngredientID(int ingredientID) {
		this.ingredientID = ingredientID;
	}

	public int getAdditiveID() {
		return additiveID;
	}

	public void setAdditiveID(int additiveID) {
		this.additiveID = additiveID;
	}
}