package advancedbrewing;

import net.minecraftforge.fluids.Fluid;

public class FluidPotion extends Fluid {

	private int color = 0xFFFFFF;

	public FluidPotion(String fluidName) {
		super(fluidName);
	}

	public FluidPotion(String fluidName, int color) {
		super(fluidName);
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = 0xFFFFFF;
	}
}