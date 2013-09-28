package advancedbrewing.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import advancedbrewing.AdvancedBrewing;
import advancedbrewing.PotionDefinition;

public class Utils {
	public static PotionDefinition getPotionDefinitionByPotionID(int potionID) {
		return AdvancedBrewing.potionDefinitionMappings.get(potionID);
	}
	public static PotionDefinition getPotionDefinitionByPotionID(int potionID, boolean allowSplash) {
		PotionDefinition potionDefinition = getPotionDefinitionByPotionID(potionID);
		return potionDefinition == null ? AdvancedBrewing.potionDefinitionMappingsSplash.get(potionID) : potionDefinition;
	}

	public static PotionDefinition getPotionDefinitionByBlock(Block block) {
		return getPotionDefinitionByBlock(block.blockID);
	}
	
	public static PotionDefinition getPotionDefinitionByBlock(int blockID) {
		if (blockID < 0 || blockID >= Block.blocksList.length) {
			return null;
		}
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			Block block = potionDefinition.getBlock();
			if (block != null && block.blockID == blockID) {
				return potionDefinition;
			}
			
		}
		return null;
	}

	public static PotionDefinition getPotionDefinitionByFluid(Fluid fluid) {
		if (fluid == null) {
			return null;
		}
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			if (potionDefinition.getName() == fluid.getName()) {
				return potionDefinition;
			}
		}
		return null;
	}

	public static Fluid getFluidByPotionDefintion(PotionDefinition potionDefinition) {
		if (potionDefinition == null) {
			return null;
		}
		return FluidRegistry.getFluid(potionDefinition.getName());
	}
	
	public static ItemStack unreversePotionItemStack(ItemStack itemStack) {
		if (itemStack != null && itemStack.itemID == Item.potion.itemID) {
    		PotionDefinition potionDefinition = getPotionDefinitionByPotionID(itemStack.getItemDamage());
    		if (potionDefinition != null) {
    			itemStack.setItemDamage(potionDefinition.getPotionID());
    		}
		}
		return itemStack;
	}
	
	@SuppressWarnings("unchecked")
	public static int getPotionIDResult(Fluid fluid, ItemStack itemStack, boolean allowSplash) {
		PotionDefinition potionDefinitionBase = Utils.getPotionDefinitionByFluid(fluid);
		if (potionDefinitionBase == null) {
			return -1;
		}

		int potionBase = potionDefinitionBase.getPotionID();
		if (potionBase < 0) {
			return -1;
		}

		int potionResult =  itemStack == null ? potionBase : (Item.itemsList[itemStack.itemID].isPotionIngredient() ? PotionHelper.applyIngredient(potionBase, Item.itemsList[itemStack.itemID].getPotionEffect()) : potionBase);
		if (!allowSplash && ItemPotion.isSplash(potionResult)) {
			return -1;
		}

		List<PotionEffect> potionEffectsBase = Item.potion.getEffects(potionBase);
		List<PotionEffect> potionEffectsResult = Item.potion.getEffects(potionResult);

		if ((potionBase <= 0 || potionEffectsBase != potionEffectsResult) && (potionEffectsBase == null || !potionEffectsBase.equals(potionEffectsResult) && potionEffectsResult != null)) {
			return potionResult;
		}
		
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public static void applyPotionEffects(int potionID, EntityLivingBase entityLivingBase) {
		ItemStack itemStackInner = new ItemStack(Item.potion, 1, potionID);
		List<PotionEffect> list = Item.potion.getEffects(itemStackInner);
		if (list != null) {
			for (PotionEffect potioneffect : list) {
				entityLivingBase.addPotionEffect(new PotionEffect(potioneffect));
			}
		}
	}
	
	public static void applyPotionEffects(int potionID, List<EntityLivingBase> entityLivingBases) {
		for (EntityLivingBase entityLivingBase : entityLivingBases) {
			applyPotionEffects(potionID, entityLivingBase);
		}
	}
	
	public static int getItemIDByItemStack(ItemStack itemStack) {
		return itemStack == null ? -1 : itemStack.itemID;
	}
}
