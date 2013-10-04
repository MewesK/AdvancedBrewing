/**
 * Copyright (c) SpaceToad, 2011 http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License
 * 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.api.power;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.SafeTimeTracker;

public final class PowerHandler {

	public static enum Type {

		ENGINE, GATE, MACHINE, PIPE, STORAGE;

		public boolean canReceiveFromPipes() {
			switch (this) {
				case MACHINE:
				case STORAGE:
					return true;
				default:
					return false;
			}
		}

		public boolean eatsEngineExcess() {
			switch (this) {
				case MACHINE:
				case STORAGE:
					return true;
				default:
					return false;
			}
		}
	}

	public static class PerditionCalculator {

		public static final float DEFAULT_POWERLOSS = 1F;
		public static final float MIN_POWERLOSS = 0.01F;
		private final float powerLoss;

		public PerditionCalculator() {
			this.powerLoss = PerditionCalculator.DEFAULT_POWERLOSS;
		}

		public PerditionCalculator(float powerLoss) {
			if (powerLoss < PerditionCalculator.MIN_POWERLOSS) {
				powerLoss = PerditionCalculator.MIN_POWERLOSS;
			}
			this.powerLoss = powerLoss;
		}

		/**
		 * Apply the perdition algorithm to the current stored energy. This
		 * function can only be called once per tick, but it might not be called
		 * every tick. It is triggered by any manipulation of the stored energy.
		 * 
		 * @param powerHandler
		 *            the PowerHandler requesting the perdition update
		 * @param current
		 *            the current stored energy
		 * @param ticksPassed
		 *            ticks since the last time this function was called
		 * @return
		 */
		public float applyPerdition(PowerHandler powerHandler, float current, long ticksPassed) {
			current -= this.powerLoss * ticksPassed;
			if (current < 0) {
				current = 0;
			}
			return current;
		}
	}

	public static final PerditionCalculator DEFAULT_PERDITION = new PerditionCalculator();
	private float minEnergyReceived;
	private float maxEnergyReceived;
	private float maxEnergyStored;
	private float activationEnergy;
	private float energyStored = 0;
	private final SafeTimeTracker doWorkTracker = new SafeTimeTracker();
	private final SafeTimeTracker sourcesTracker = new SafeTimeTracker();
	private final SafeTimeTracker perditionTracker = new SafeTimeTracker();
	public final int[] powerSources = new int[6];
	public final IPowerReceptor receptor;
	private PerditionCalculator perdition;
	private final PowerReceiver receiver;
	private final Type type;

	public PowerHandler(IPowerReceptor receptor, Type type) {
		this.receptor = receptor;
		this.type = type;
		this.receiver = new PowerReceiver();
		this.perdition = PowerHandler.DEFAULT_PERDITION;
	}

	public PowerReceiver getPowerReceiver() {
		return this.receiver;
	}

	public float getMinEnergyReceived() {
		return this.minEnergyReceived;
	}

	public float getMaxEnergyReceived() {
		return this.maxEnergyReceived;
	}

	public float getMaxEnergyStored() {
		return this.maxEnergyStored;
	}

	public float getActivationEnergy() {
		return this.activationEnergy;
	}

	public float getEnergyStored() {
		return this.energyStored;
	}

	/**
	 * Setup your PowerHandler's settings.
	 * 
	 * @param minEnergyReceived
	 *            This is the minimum about of power that will be accepted by
	 *            the PowerHandler. This should generally be greater than the
	 *            activationEnergy if you plan to use the doWork() callback.
	 *            Anything greater than 1 will prevent Redstone Engines from
	 *            powering this Provider.
	 * @param maxEnergyReceived
	 *            The maximum amount of power accepted by the PowerHandler. This
	 *            should generally be less than 500. Too low and larger engines
	 *            will overheat while trying to power the machine. Too high, and
	 *            the engines will never warm up. Greater values also place
	 *            greater strain on the power net.
	 * @param activationEnergy
	 *            If the stored energy is greater than this value, the doWork()
	 *            callback is called (once per tick).
	 * @param maxStoredEnergy
	 *            The maximum amount of power this PowerHandler can store.
	 *            Values tend to range between 100 and 5000. With 1000 and 1500
	 *            being common.
	 */
	public void configure(float minEnergyReceived, float maxEnergyReceived, float activationEnergy, float maxStoredEnergy) {
		if (minEnergyReceived > maxEnergyReceived) {
			maxEnergyReceived = minEnergyReceived;
		}
		this.minEnergyReceived = minEnergyReceived;
		this.maxEnergyReceived = maxEnergyReceived;
		this.maxEnergyStored = maxStoredEnergy;
		this.activationEnergy = activationEnergy;
	}

	public void configurePowerPerdition(int powerLoss, int powerLossRegularity) {
		if (powerLoss == 0 || powerLossRegularity == 0) {
			this.perdition = new PerditionCalculator(0);
			return;
		}
		this.perdition = new PerditionCalculator((float) powerLoss / (float) powerLossRegularity);
	}

	/**
	 * Allows you to define a new PerditionCalculator class to handler perdition
	 * calculations.
	 * 
	 * For example if you want exponentially increasing loss based on amount
	 * stored.
	 * 
	 * @param perdition
	 */
	public void setPerdition(PerditionCalculator perdition) {
		if (perdition == null) {
			perdition = PowerHandler.DEFAULT_PERDITION;
		}
		this.perdition = perdition;
	}

	public PerditionCalculator getPerdition() {
		if (this.perdition == null) {
			return PowerHandler.DEFAULT_PERDITION;
		}
		return this.perdition;
	}

	/**
	 * Ticks the power handler. You should call this if you can, but its not
	 * required.
	 * 
	 * If you don't call it, the possibility exists for some weirdness with the
	 * perdition algorithm and work callback as its possible they will not be
	 * called on every tick they otherwise would be. You should be able to
	 * design around this though if you are aware of the limitations.
	 */
	public void update() {
		this.applyPerdition();
		this.applyWork();
		this.validateEnergy();
	}

	private void applyPerdition() {
		if (this.perditionTracker.markTimeIfDelay(this.receptor.getWorld(), 1) && this.energyStored > 0) {
			float newEnergy = this.getPerdition().applyPerdition(this, this.energyStored, this.perditionTracker.durationOfLastDelay());
			if (newEnergy == 0 || newEnergy < this.energyStored) {
				this.energyStored = newEnergy;
			}
			else {
				this.energyStored = PowerHandler.DEFAULT_PERDITION.applyPerdition(this, this.energyStored, this.perditionTracker.durationOfLastDelay());
			}
			this.validateEnergy();
		}
	}

	private void applyWork() {
		if (this.energyStored >= this.activationEnergy) {
			if (this.doWorkTracker.markTimeIfDelay(this.receptor.getWorld(), 1)) {
				this.receptor.doWork(this);
			}
		}
	}

	private void updateSources(ForgeDirection source) {
		if (this.sourcesTracker.markTimeIfDelay(this.receptor.getWorld(), 1)) {
			for (int i = 0; i < 6; ++i) {
				this.powerSources[i] -= this.sourcesTracker.durationOfLastDelay();
				if (this.powerSources[i] < 0) {
					this.powerSources[i] = 0;
				}
			}
		}

		if (source != null) {
			this.powerSources[source.ordinal()] = 10;
		}
	}

	/**
	 * Extract energy from the PowerHandler. You must call this even if doWork()
	 * triggers.
	 * 
	 * @param min
	 * @param max
	 * @param doUse
	 * @return amount used
	 */
	public float useEnergy(float min, float max, boolean doUse) {
		this.applyPerdition();

		float result = 0;

		if (this.energyStored >= min) {
			if (this.energyStored <= max) {
				result = this.energyStored;
				if (doUse) {
					this.energyStored = 0;
				}
			}
			else {
				result = max;
				if (doUse) {
					this.energyStored -= max;
				}
			}
		}

		this.validateEnergy();

		return result;
	}

	public void readFromNBT(NBTTagCompound data) {
		this.readFromNBT(data, "powerProvider");
	}

	public void readFromNBT(NBTTagCompound data, String tag) {
		NBTTagCompound nbt = data.getCompoundTag(tag);
		this.energyStored = nbt.getFloat("storedEnergy");
	}

	public void writeToNBT(NBTTagCompound data) {
		this.writeToNBT(data, "powerProvider");
	}

	public void writeToNBT(NBTTagCompound data, String tag) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("storedEnergy", this.energyStored);
		data.setCompoundTag(tag, nbt);
	}

	public final class PowerReceiver {

		private PowerReceiver() {
		}

		public float getMinEnergyReceived() {
			return PowerHandler.this.minEnergyReceived;
		}

		public float getMaxEnergyReceived() {
			return PowerHandler.this.maxEnergyReceived;
		}

		public float getMaxEnergyStored() {
			return PowerHandler.this.maxEnergyStored;
		}

		public float getActivationEnergy() {
			return PowerHandler.this.activationEnergy;
		}

		public float getEnergyStored() {
			return PowerHandler.this.energyStored;
		}

		public Type getType() {
			return PowerHandler.this.type;
		}

		public void update() {
			PowerHandler.this.update();
		}

		/**
		 * The amount of power that this PowerHandler currently needs.
		 * 
		 * @return
		 */
		public float powerRequest() {
			this.update();
			return Math.min(PowerHandler.this.maxEnergyReceived, PowerHandler.this.maxEnergyStored - PowerHandler.this.energyStored);
		}

		/**
		 * Add power to the PowerReceiver from an external source.
		 * 
		 * @param quantity
		 * @param from
		 * @return the amount of power used
		 */
		public float receiveEnergy(Type source, final float quantity, ForgeDirection from) {
			float used = quantity;
			if (source == Type.ENGINE) {
				if (used < PowerHandler.this.minEnergyReceived) {
					return 0;
				}
				else if (used > PowerHandler.this.maxEnergyReceived) {
					used = PowerHandler.this.maxEnergyReceived;
				}
			}

			PowerHandler.this.updateSources(from);

			used = PowerHandler.this.addEnergy(used);

			PowerHandler.this.applyWork();

			if (source == Type.ENGINE && PowerHandler.this.type.eatsEngineExcess()) {
				return Math.min(quantity, PowerHandler.this.maxEnergyReceived);
			}

			return used;
		}
	}

	/**
	 * 
	 * @return the amount the power changed by
	 */
	public float addEnergy(float quantity) {
		this.energyStored += quantity;

		if (this.energyStored > this.maxEnergyStored) {
			quantity -= this.energyStored - this.maxEnergyStored;
			this.energyStored = this.maxEnergyStored;
		}
		else if (this.energyStored < 0) {
			quantity -= this.energyStored;
			this.energyStored = 0;
		}

		this.applyPerdition();

		return quantity;
	}

	public void setEnergy(float quantity) {
		this.energyStored = quantity;
		this.validateEnergy();
	}

	public boolean isPowerSource(ForgeDirection from) {
		return this.powerSources[from.ordinal()] != 0;
	}

	private void validateEnergy() {
		if (this.energyStored < 0) {
			this.energyStored = 0;
		}
		if (this.energyStored > this.maxEnergyStored) {
			this.energyStored = this.maxEnergyStored;
		}
	}
}
