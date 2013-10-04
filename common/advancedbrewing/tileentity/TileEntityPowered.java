/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public abstract class TileEntityPowered extends TileEntitySynchronized implements IPowerReceptor {

	private PowerHandler powerHandler;
	private float[] recentEnergy = new float[20];
	private float recentEnergyAverage;
	private float currentInput; // TODO
	private int tick = 0;

	public TileEntityPowered() {
		this.powerHandler = new PowerHandler(this, Type.MACHINE);
		this.powerHandler.configure(2, 100, 1, 10000);
		this.powerHandler.configurePowerPerdition(0, 0);
	}

	// TileEntitySynchronized

	@Override
	protected void readCustomFromNBT(NBTTagCompound par1NBTTagCompound) {
		this.powerHandler.readFromNBT(par1NBTTagCompound);
	}

	@Override
	protected void writeCustomToNBT(NBTTagCompound par1NBTTagCompound) {
		this.powerHandler.writeToNBT(par1NBTTagCompound);
	}

	// IPowerReceptor

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return this.powerHandler.getPowerReceiver();
	}

	@Override
	public abstract void doWork(PowerHandler workProvider);

	@Override
	public World getWorld() {
		return this.worldObj;
	}

	public float calculateRecentEnergyAverage() {
		float recentEnergyAverage = 0;
		for (int i = 0; i < this.recentEnergy.length; i++) {
			recentEnergyAverage += this.recentEnergy[i] / (this.recentEnergy.length - 1);
		}
		return recentEnergyAverage;
	}

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote) {
			return;
		}

		this.tick++;
		this.tick = this.tick % this.recentEnergy.length;
		this.recentEnergy[this.tick] = 0.0f;

		this.powerHandler.update();
	}

	// getter / setter

	public PowerHandler getPowerHandler() {
		return this.powerHandler;
	}

	public void setPowerHandler(PowerHandler powerHandler) {
		this.powerHandler = powerHandler;
	}

	public float getRecentEnergyAverage() {
		return this.recentEnergyAverage;
	}

	public void setRecentEnergyAverage(float recentEnergyAverage) {
		this.recentEnergyAverage = recentEnergyAverage;
	}

	public float getCurrentEnergy() {
		return this.recentEnergy[this.tick];
	}

	public void setCurrentEnergy(float currentEnergy) {
		this.recentEnergy[this.tick] = currentEnergy;
	}

	public float getCurrentInput() {
		return this.currentInput;
	}

	public void setCurrentInput(float currentInput) {
		this.currentInput = currentInput;
	}
}