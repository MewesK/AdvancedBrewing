package advancedbrewing.tileentity;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.block.Block;
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
import advancedbrewing.block.BlockMachine;
import advancedbrewing.gui.SlotBreweryBasePotionContainer;
import advancedbrewing.gui.SlotBreweryEmptyContainer;
import advancedbrewing.gui.SlotBreweryPotionIngredient;
import advancedbrewing.utils.Utils;

public class TileEntityBrewery extends TileEntityMachine {
	public static int RESULT_FLUIDAMOUNT = FluidContainerRegistry.BUCKET_VOLUME * 4;
	public static int RESULT_FLUIDAMOUNT_MULTI = FluidContainerRegistry.BUCKET_VOLUME * 5;
	public static int MAX_WORKTIME = 400;
	public static int MAX_WORKTIME_MULTI = 300;

	// properties
	private int[] ingredientIDs = new int[3];
	private int type = 0;

	public TileEntityBrewery() {
		this.itemStacks = new ItemStack[14];
		this.fluidTanks = new FluidTank[2];

		this.fluidTanks[0] = new FluidTank(MAX_FLUIDAMOUNT);
		this.fluidTanks[1] = new FluidTank(MAX_FLUIDAMOUNT);

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

		if (processContainerInput(0, 0, true)) {
			usedEnergy += this.getPowerHandler().useEnergy(1, 1, true);
		}

		// process bucket slot (output)

		if (processContainerOutput(1, 1, 1)) {
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
				else if ((this.itemStacks[2] == null && this.itemStacks[3] == null && this.itemStacks[4] == null) || this.ingredientIDs[0] != Utils.getItemIDByItemStack(this.itemStacks[2]) || this.ingredientIDs[1] != Utils.getItemIDByItemStack(this.itemStacks[3]) || this.ingredientIDs[2] != Utils.getItemIDByItemStack(this.itemStacks[4]) || !this.hasItemInBuffer(this.ingredientIDs[0]) || !this.hasItemInBuffer(this.ingredientIDs[1]) || !this.hasItemInBuffer(this.ingredientIDs[2])) {
					this.workTime = 0;
				}
			}
		}
		else if (this.canWork()) {
			this.workTime = MAX_WORKTIME;

			this.ingredientIDs[0] = Utils.getItemIDByItemStack(this.itemStacks[2]);
			this.ingredientIDs[1] = Utils.getItemIDByItemStack(this.itemStacks[3]);
			this.ingredientIDs[2] = Utils.getItemIDByItemStack(this.itemStacks[4]);
		}

		this.setCurrentEnergy((int) usedEnergy);

		// change block
		if (working != this.workTime > 0) {
			((BlockBrewery) AdvancedBrewing.breweryIdleBlock).updateBlockState(this.workTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}

		// synchronize with client
		this.onInventoryChanged();
	}

	private boolean hasItemInBuffer(int itemID) {
		if (itemID < 0) {
			return true;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = this.itemStacks[i + 5];
			if (itemStack != null && itemStack.itemID == itemID && itemStack.stackSize > 0) {
				return true;
			}
		}
		return false;
	}

	private void removeItemFromBuffer(int itemID, int amount) {
		if (itemID < 0 || amount <= 0) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = this.itemStacks[i + 5];
			if (itemStack != null && itemStack.itemID == itemID && itemStack.stackSize >= amount) {
				decrStackSize(i + 5, amount);
				return;
			}
		}
	}

	// TileEntityMachine

	@Override
	protected boolean canWork() {
		// update type
		this.checkIfProperlyFormed();

		// check tanks & fluids
		int fluidAmount = this.type > 0 ? RESULT_FLUIDAMOUNT_MULTI : RESULT_FLUIDAMOUNT;
		if (this.fluidTanks[0].getFluid() != null && this.fluidTanks[0].getFluidAmount() >= fluidAmount && this.fluidTanks[1].getFluidAmount() <= MAX_FLUIDAMOUNT - fluidAmount) {
			// check ingredients
			List<ItemStack> validIngredients = new ArrayList<ItemStack>();
			for (int i = 2; i < (this.type > 0 ? 5 : 3); i++) {
				ItemStack itemStack = this.itemStacks[i];
				if (itemStack != null && this.hasItemInBuffer(itemStack.itemID) && Item.itemsList[itemStack.itemID].isPotionIngredient()) {
					validIngredients.add(itemStack);
				}
			}

			// check brewing
			if (validIngredients.size() > 0) {
				Fluid fluidResult = this.fluidTanks[0].getFluid().getFluid();
				for (ItemStack validIngredient : validIngredients) {
					int potionResult = Utils.getPotionIDResult(fluidResult, validIngredient, false);
					PotionDefinition potionDefinitionResult = Utils.getPotionDefinitionByPotionID(potionResult);
					fluidResult = Utils.getFluidByPotionDefintion(potionDefinitionResult);

					if (fluidResult == null) {
						return false;
					}
				}

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
			// get ingredients
			List<ItemStack> validIngredients = new ArrayList<ItemStack>();
			for (int i = 2; i < (this.type > 0 ? 5 : 3); i++) {
				ItemStack itemStack = this.itemStacks[i];
				if (itemStack != null && this.hasItemInBuffer(itemStack.itemID) && Item.itemsList[itemStack.itemID].isPotionIngredient()) {
					validIngredients.add(itemStack);
					// remove item from buffer
					this.removeItemFromBuffer(itemStack.itemID, 1);
				}
			}

			// do brewing
			Fluid fluidResult = this.fluidTanks[0].getFluid().getFluid();
			for (ItemStack validIngredient : validIngredients) {
				int potionResult = Utils.getPotionIDResult(fluidResult, validIngredient, false);
				PotionDefinition potionDefinitionResult = Utils.getPotionDefinitionByPotionID(potionResult);
				fluidResult = Utils.getFluidByPotionDefintion(potionDefinitionResult);
			}

			int fluidAmount = this.type > 0 ? RESULT_FLUIDAMOUNT_MULTI : RESULT_FLUIDAMOUNT;
			this.fluidTanks[0].drain(fluidAmount, true);
			this.fluidTanks[1].fill(new FluidStack(fluidResult, fluidAmount), true);

			return true;
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
		if (slotIndex == 0 && SlotBreweryBasePotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex == 1 && SlotBreweryEmptyContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex >= 5 && slotIndex <= 13 && SlotBreweryPotionIngredient.isItemValidForSlot(itemStack)) {
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		switch (side) {
			case 0:
				// bottom
			case 1:
				// top
				return new int[] { 5, 6, 7, 8, 9, 10, 11, 12, 13 };
			default:
				// sides
				return new int[] { 0, 1 };
		}
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if (slotIndex == 0 && SlotBreweryEmptyContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex == 1 && SlotBreweryBasePotionContainer.isItemValidForSlot(itemStack)) {
			return true;
		}
		if (slotIndex >= 5 && slotIndex <= 13 && SlotBreweryPotionIngredient.isItemValidForSlot(itemStack)) {
			return false; // TODO: allow it or not?
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

	// Multi

	public int checkIfProperlyFormed() {
		int type = 1;
		int dir = getBlockMetadata();

		// Horizontal (X or Z)
		for (int horiz = -1; horiz <= 1; horiz++) {
			// Vertical (Y)
			for (int vert = 0; vert <= 2; vert++) {
				// Depth (Z or X)
				for (int depth = 1; depth <= 3; depth++) {
					int x = this.xCoord + (dir == BlockMachine.DIR_SOUTH ? horiz : (dir == BlockMachine.DIR_NORTH ? -horiz : (dir == BlockMachine.DIR_EAST ? depth : -depth)));
					int y = this.yCoord + vert;
					int z = this.zCoord + (dir == BlockMachine.DIR_SOUTH ? depth : (dir == BlockMachine.DIR_NORTH ? -depth : horiz));

					int blockId = this.worldObj.getBlockId(x, y, z);

					if (horiz == 0 && vert == 1 && depth == 2) {
						if (blockId == Block.blockEmerald.blockID) {
							type = 2;
						}
						else if (blockId != 0) {
							this.type = 0;
							return 0;
						}
					}
					else if (blockId != Block.blockIron.blockID) {
						this.type = 0;
						return 0;
					}
				}
			}
		}

		this.type = type;
		return type;
	}

	// getter / setter

	public int[] getIngredientIDs() {
		return this.ingredientIDs;
	}

	public void setIngredientIDs(int[] ingredientIDs) {
		this.ingredientIDs = ingredientIDs;
	}

	public int getType() {
		return type;
	}
}