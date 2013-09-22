package advancedbrewing.tileentity;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;
import advancedbrewing.block.BlockBrewery;
import advancedbrewing.gui.SlotBreweryEmptyContainer;
import advancedbrewing.gui.SlotBreweryPotionContainer;
import advancedbrewing.gui.SlotBreweryPotionIngredient;
import advancedbrewing.utils.Utils;

public class TileEntityBrewery extends TileEntityMachine {
	public static int RESULT_FLUIDAMOUNT = FluidContainerRegistry.BUCKET_VOLUME * 3;
	public static int MAX_WORKTIME = 400;

	// properties
	private int ingredientID;

	public TileEntityBrewery() {
		this.itemStacks = new ItemStack[3];
		this.fluidTanks = new FluidTank[2];

		this.fluidTanks[0] = new FluidTank(MAX_FLUIDAMOUNT);
		this.fluidTanks[1] = new FluidTank(MAX_FLUIDAMOUNT);

		this.setPowerHandler(new PowerHandler(this, Type.MACHINE));
		this.getPowerHandler().configure(2, 100, 1, 10000);
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

		if (processContainerInput(1, 0, true)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process bucket slot (output)

		if (processContainerOutput(2, 2, 1)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process brewing

		boolean working = this.workTime > 0;

		if (this.workTime > 0) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 4, true);
			if (usedEnergy > 1) {
				this.workTime -= (int) usedEnergy;

				if (this.workTime == 0) {
					this.work();
				}
				else if (!this.canWork()) {
					this.workTime = 0;
				}
				else if (this.itemStacks[0] == null || this.ingredientID != this.itemStacks[0].itemID) {
					this.workTime = 0;
				}
			}
		}
		else if (this.canWork()) {
			this.workTime = MAX_WORKTIME;
			this.ingredientID = this.itemStacks[0].itemID;
		}

		this.setCurrentEnergy((int) usedEnergy);

		// change block
		if (working != this.workTime > 0) {
			((BlockBrewery) AdvancedBrewing.breweryIdleBlock).updateBlockState(this.workTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}

		// synchronize with client
		this.onInventoryChanged();
	}

	// TileEntityMachine

	@Override
	protected boolean canWork() {
		if (this.fluidTanks[0].getFluid() != null && this.fluidTanks[0].getFluidAmount() >= RESULT_FLUIDAMOUNT && this.fluidTanks[1].getFluidAmount() <= MAX_FLUIDAMOUNT - RESULT_FLUIDAMOUNT && this.itemStacks[0] != null && this.itemStacks[0].stackSize > 0) {
			ItemStack itemStack = this.itemStacks[0];
			if (Item.itemsList[itemStack.itemID].isPotionIngredient()) {
				int potionResult = Utils.getPotionIDResult(this.fluidTanks[0].getFluid().getFluid(), itemStack, false);
				PotionDefinition potionDefinitionResult = Utils.getPotionDefinitionByPotionID(potionResult);
				Fluid fluidResult = Utils.getFluidByPotionDefintion(potionDefinitionResult);
				if (fluidResult != null && this.fluidTanks[1].getFluid() == null || this.fluidTanks[1].getFluid().getFluid() == fluidResult) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected boolean work() {
		if (this.canWork()) {
			int potionResult = Utils.getPotionIDResult(this.fluidTanks[0].getFluid().getFluid(), this.itemStacks[0], false);
			PotionDefinition potionDefinitionResult = Utils.getPotionDefinitionByPotionID(potionResult);
			Fluid fluidResult = Utils.getFluidByPotionDefintion(potionDefinitionResult);
    		if (fluidResult != null) {
    			this.fluidTanks[0].drain(RESULT_FLUIDAMOUNT, true);
    			this.fluidTanks[1].fill(new FluidStack(fluidResult, RESULT_FLUIDAMOUNT), true);	
    			decrStackSize(0, 1);
    			return true;
			}
		}
		return false;
	}
	
	// ISidedInventory

	@Override
	public String getInvName() {
		return "container.brewery";
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if (slotIndex == 0 && SlotBreweryPotionIngredient.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex == 1 && SlotBreweryPotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex == 2 && SlotBreweryEmptyContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		switch (side) {
			case 0:
				// bottom
				return new int[] { 0 };
			case 1:
				// top
				return new int[] { 0 };
			default:
				// sides
				return new int[] { 0, 1, 2 };
		}
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if (slotIndex == 0 && SlotBreweryPotionIngredient.isItemValidForSlot(itemStack)) {
			return false;
		}
		if (slotIndex == 1 && SlotBreweryPotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex == 2 && SlotBreweryEmptyContainer.isItemValidForSlot(itemStack)) {
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
		return this.fluidTanks[1].drain(maxDrain, doDrain);
	}

	// getter / setter

	public int getIngredientID() {
		return ingredientID;
	}

	public void setIngredientID(int ingredientID) {
		this.ingredientID = ingredientID;
	}
}