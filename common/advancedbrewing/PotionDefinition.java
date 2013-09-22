package advancedbrewing;

import net.minecraft.block.Block;
import net.minecraft.potion.PotionHelper;

public class PotionDefinition {

	public enum Type {
		red, green, blue, yellow, pink, turquoise, grey, darkred, darkgreen, darkblue, darkyellow, darkpink, darkturquoise, darkgrey
	}

	private String name;
	private int[] potionIDs;
	private Block block;

	public PotionDefinition(String name) {
		this(name, null);
	}

	public PotionDefinition(String name, int[] potionIDs) {
		this.name = name;
		this.potionIDs = potionIDs;
	}
	
	public int getColor() {
		return PotionHelper.func_77915_a(getPotionID(), false);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPotionID() {
		return potionIDs != null ? potionIDs[0] : -1;
	}

	public int[] getPotionIDs() {
		return potionIDs;
	}

	public void setPotionIds(int[] potionIDs) {
		this.potionIDs = potionIDs;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
}