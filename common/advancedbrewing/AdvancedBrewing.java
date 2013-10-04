/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import advancedbrewing.block.BlockBrewery;
import advancedbrewing.block.BlockInfuser;
import advancedbrewing.block.BlockPotion;
import advancedbrewing.block.BlockVaporizer;
import advancedbrewing.item.ItemArrowPotion;
import advancedbrewing.item.ItemAutoPotion;
import advancedbrewing.item.ItemBlockLocalized;
import advancedbrewing.item.ItemBucketPotion;
import advancedbrewing.proxy.Proxy;
import advancedbrewing.tileentity.TileEntityBrewery;
import advancedbrewing.tileentity.TileEntityInfuser;
import advancedbrewing.tileentity.TileEntityVaporizer;
import advancedbrewing.utils.Localization;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, dependencies = Reference.DEPENDENCIES)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, packetHandler = HandlerPacket.class, channels = { Reference.MOD_ID })
public class AdvancedBrewing {

	public static Configuration configuration;

	// potions
	public static List<PotionDefinition> potionDefinitions = new ArrayList<PotionDefinition>();
	public static Map<Integer, PotionDefinition> potionDefinitionMappings = new HashMap<Integer, PotionDefinition>();
	public static List<PotionDefinition> potionDefinitionsSplash = new ArrayList<PotionDefinition>();
	public static Map<Integer, PotionDefinition> potionDefinitionMappingsSplash = new HashMap<Integer, PotionDefinition>();

	// blocks
	public static Block breweryIdleBlock;
	public static Block breweryBurningBlock;
	public static Block infuserIdleBlock;
	public static Block infuserBurningBlock;
	public static Block vaporizerIdleBlock;
	public static Block vaporizerBurningBlock;

	// items
	public static Item arrowPotionItem;
	public static Item bucketPotionItem;
	public static Item autoPotionItem;

	@Instance("AdvancedBrewing")
	public static AdvancedBrewing instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		AdvancedBrewing.configuration = new Configuration(new File(evt.getModConfigurationDirectory(), "advancedbrewing/main.conf"));

		try {
			AdvancedBrewing.configuration.load();

			// machinery

			// blocks
			int breweryIdleBlockID = AdvancedBrewing.configuration.getBlock("breweryIdleBlockID", 1000).getInt(1000);
			AdvancedBrewing.breweryIdleBlock = new BlockBrewery(breweryIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.brewery.idle.name");
			GameRegistry.registerBlock(AdvancedBrewing.breweryIdleBlock, ItemBlockLocalized.class, AdvancedBrewing.breweryIdleBlock.getUnlocalizedName());

			int breweryBurningBlockID = AdvancedBrewing.configuration.getBlock("breweryBurningBlockID", 1001).getInt(1001);
			AdvancedBrewing.breweryBurningBlock = new BlockBrewery(breweryBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.brewery.burning.name");
			GameRegistry.registerBlock(AdvancedBrewing.breweryBurningBlock, ItemBlockLocalized.class, AdvancedBrewing.breweryBurningBlock.getUnlocalizedName());

			int infuserIdleBlockID = AdvancedBrewing.configuration.getBlock("infuserIdleBlockID", 1002).getInt(1002);
			AdvancedBrewing.infuserIdleBlock = new BlockInfuser(infuserIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.infuser.idle.name");
			GameRegistry.registerBlock(AdvancedBrewing.infuserIdleBlock, ItemBlockLocalized.class, AdvancedBrewing.infuserIdleBlock.getUnlocalizedName());

			int infuserBurningBlockID = AdvancedBrewing.configuration.getBlock("infuserBurningBlockID", 1003).getInt(1003);
			AdvancedBrewing.infuserBurningBlock = new BlockInfuser(infuserBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.infuser.burning.name");
			GameRegistry.registerBlock(AdvancedBrewing.infuserBurningBlock, ItemBlockLocalized.class, AdvancedBrewing.infuserBurningBlock.getUnlocalizedName());

			int vaporizerIdleBlockID = AdvancedBrewing.configuration.getBlock("vaporizerIdleBlockID", 1004).getInt(1004);
			AdvancedBrewing.vaporizerIdleBlock = new BlockVaporizer(vaporizerIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.vaporizer.idle.name");
			GameRegistry.registerBlock(AdvancedBrewing.vaporizerIdleBlock, ItemBlockLocalized.class, AdvancedBrewing.vaporizerIdleBlock.getUnlocalizedName());

			int vaporizerBurningBlockID = AdvancedBrewing.configuration.getBlock("vaporizerBurningBlockID", 1005).getInt(1005);
			AdvancedBrewing.vaporizerBurningBlock = new BlockVaporizer(vaporizerBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.vaporizer.burning.name");
			GameRegistry.registerBlock(AdvancedBrewing.vaporizerBurningBlock, ItemBlockLocalized.class, AdvancedBrewing.vaporizerBurningBlock.getUnlocalizedName());

			// potions

			// bucketPotion
			int bucketPotionItemID = AdvancedBrewing.configuration.getItem("bucketPotionItemID", 10000).getInt(10000);
			AdvancedBrewing.bucketPotionItem = new ItemBucketPotion(bucketPotionItemID);
			AdvancedBrewing.bucketPotionItem.setContainerItem(Item.bucketEmpty);

			// autoPotion
			int autoPotionItemID = AdvancedBrewing.configuration.getItem("autoPotionItemID", 10001).getInt(10001);
			AdvancedBrewing.autoPotionItem = new ItemAutoPotion(autoPotionItemID);

			// arrowPotion
			int arrowPotionItemID = AdvancedBrewing.configuration.getItem("arrowPotionItemID", 10002).getInt(10002);
			AdvancedBrewing.arrowPotionItem = new ItemArrowPotion(arrowPotionItemID);
			BlockDispenser.dispenseBehaviorRegistry.putObject(AdvancedBrewing.arrowPotionItem, new DispenserBehaviorArrowPotion());

			int i = 0;
			for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
				// ignore water definition
				if (i == 0) {
					i++;
					continue;
				}

				int potionID = potionDefinition.getPotionID();

				// fluid
				Fluid fluid = new FluidPotion(potionDefinition.getName(), potionDefinition.getColor());
				FluidRegistry.registerFluid(fluid);
				FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(AdvancedBrewing.bucketPotionItem, 1, potionID), FluidContainerRegistry.EMPTY_BUCKET);
				FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(Item.potion, 1, potionID), FluidContainerRegistry.EMPTY_BOTTLE);

				// block
				int potionBlockID = AdvancedBrewing.configuration.getBlock("potionBlockID." + potionDefinition.getName(), 1010 + i).getInt(1010 + i);
				Block potionBlock = new BlockPotion(potionBlockID, fluid, Material.water).setUnlocalizedName("block." + potionDefinition.getName() + ".name");
				GameRegistry.registerBlock(potionBlock, ItemBlockLocalized.class, potionBlock.getUnlocalizedName());
				fluid.setBlockID(potionBlock);
				potionDefinition.setBlock(potionBlock);

				i++;
			}

			Localization.addLocalization("/lang/advancedbrewing/", "en_US");
		}
		finally {
			if (AdvancedBrewing.configuration.hasChanged()) {
				AdvancedBrewing.configuration.save();
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		// add recipes
		ItemStack ironIngotStack = new ItemStack(Item.ingotIron);
		ItemStack redstoneStack = new ItemStack(Item.redstone);
		GameRegistry.addRecipe(new ItemStack(AdvancedBrewing.breweryIdleBlock), "xxx", "xyx", "xzx", 'x', ironIngotStack, 'y', new ItemStack(Item.brewingStand), 'z', redstoneStack);
		GameRegistry.addRecipe(new ItemStack(AdvancedBrewing.infuserIdleBlock), "xxx", "xyx", "xzx", 'x', ironIngotStack, 'y', new ItemStack(Block.hopperBlock), 'z', redstoneStack);
		GameRegistry.addRecipe(new ItemStack(AdvancedBrewing.vaporizerIdleBlock), "xxx", "xyx", "xzx", 'x', ironIngotStack, 'y', new ItemStack(Block.dispenser), 'z', redstoneStack);
		GameRegistry.addRecipe(new ItemStack(AdvancedBrewing.autoPotionItem), "xxx", "xyx", "xxx", 'x', new ItemStack(Item.glassBottle), 'y', new ItemStack(Item.pocketSundial));

		// register handlers
		MinecraftForge.EVENT_BUS.register(new HandlerTexture());
		MinecraftForge.EVENT_BUS.register(new HandlerBucket());
		MinecraftForge.EVENT_BUS.register(new HandlerBow());

		// register handlers
		NetworkRegistry.instance().registerGuiHandler(this, new HandlerGui());

		// entities
		GameRegistry.registerTileEntity(TileEntityBrewery.class, "entity.brewery");
		GameRegistry.registerTileEntity(TileEntityInfuser.class, "entity.infuser");
		GameRegistry.registerTileEntity(TileEntityVaporizer.class, "entity.vaporizer");

		// renderers
		AdvancedBrewing.proxy.registerRenderers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	private static void registerPotionDefinition(String name, int[] potionIDs) {
		PotionDefinition potionDefinition = new PotionDefinition(name, potionIDs);
		AdvancedBrewing.potionDefinitions.add(potionDefinition);
		for (int potionID : potionIDs) {
			AdvancedBrewing.potionDefinitionMappings.put(potionID, potionDefinition);
		}
		if (!name.equals("water") && !name.equals("potion.awkward") && !name.equals("potion.thick") && !name.equals("potion.mundane_extended") && !name.equals("potion.mundane")) {
			int[] splashPotionIDs = potionIDs.clone();
			for (int i = 0; i < potionIDs.length; i++) {
				splashPotionIDs[i] += 8192;
			}
			potionDefinition = new PotionDefinition(name + "_splash", splashPotionIDs);
			AdvancedBrewing.potionDefinitionsSplash.add(potionDefinition);
			for (int potionID : splashPotionIDs) {
				AdvancedBrewing.potionDefinitionMappingsSplash.put(potionID, potionDefinition);
			}
		}
	}

	static {
		AdvancedBrewing.registerPotionDefinition("water", new int[] { 0 });

		// base potions
		AdvancedBrewing.registerPotionDefinition("potion.awkward", new int[] { 16 });
		AdvancedBrewing.registerPotionDefinition("potion.thick", new int[] { 32 });
		AdvancedBrewing.registerPotionDefinition("potion.mundane_extended", new int[] { 64 });
		AdvancedBrewing.registerPotionDefinition("potion.mundane", new int[] { 8192 });

		// positive potions
		AdvancedBrewing.registerPotionDefinition("potion.regeneration", new int[] { 8193 });
		AdvancedBrewing.registerPotionDefinition("potion.regeneration_extended", new int[] { 8257 });
		AdvancedBrewing.registerPotionDefinition("potion.regeneration_2", new int[] { 8225 });
		AdvancedBrewing.registerPotionDefinition("potion.swiftness", new int[] { 8194 });
		AdvancedBrewing.registerPotionDefinition("potion.swiftness_extended", new int[] { 8258 });
		AdvancedBrewing.registerPotionDefinition("potion.swiftness_2", new int[] { 8226 });
		AdvancedBrewing.registerPotionDefinition("potion.fire_resistance", new int[] { 8195, 8227 });
		AdvancedBrewing.registerPotionDefinition("potion.fire_resistance_extended", new int[] { 8259 });
		AdvancedBrewing.registerPotionDefinition("potion.healing", new int[] { 8197, 8261 });
		AdvancedBrewing.registerPotionDefinition("potion.healing_2", new int[] { 8229 });
		AdvancedBrewing.registerPotionDefinition("potion.night_vision", new int[] { 8198, 8230 });
		AdvancedBrewing.registerPotionDefinition("potion.night_vision_extended", new int[] { 8262 });
		AdvancedBrewing.registerPotionDefinition("potion.strength", new int[] { 8201 });
		AdvancedBrewing.registerPotionDefinition("potion.strength_extended", new int[] { 8265 });
		AdvancedBrewing.registerPotionDefinition("potion.strength_2", new int[] { 8233 });
		AdvancedBrewing.registerPotionDefinition("potion.invisibility", new int[] { 8206, 8238 });
		AdvancedBrewing.registerPotionDefinition("potion.invisibility_extended", new int[] { 8270 });

		// negative potions
		AdvancedBrewing.registerPotionDefinition("potion.poison", new int[] { 8196 });
		AdvancedBrewing.registerPotionDefinition("potion.poison_extended", new int[] { 8260 });
		AdvancedBrewing.registerPotionDefinition("potion.poison_2", new int[] { 8228 });
		AdvancedBrewing.registerPotionDefinition("potion.weakness", new int[] { 8200, 8232 });
		AdvancedBrewing.registerPotionDefinition("potion.weakness_extended", new int[] { 8264 });
		AdvancedBrewing.registerPotionDefinition("potion.slowness", new int[] { 8202, 8234 });
		AdvancedBrewing.registerPotionDefinition("potion.slowness_extended", new int[] { 8266 });
		AdvancedBrewing.registerPotionDefinition("potion.harming", new int[] { 8204, 8268 });
		AdvancedBrewing.registerPotionDefinition("potion.harming_2", new int[] { 8236 });
	}
}
