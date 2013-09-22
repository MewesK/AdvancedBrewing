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
import advancedbrewing.PotionDefinition.Type;
import advancedbrewing.block.BlockBrewery;
import advancedbrewing.block.BlockInfuser;
import advancedbrewing.block.BlockPotion;
import advancedbrewing.block.BlockVaporizer;
import advancedbrewing.item.ItemArrowPotion;
import advancedbrewing.item.ItemBlockLocalized;
import advancedbrewing.item.ItemAutoPotion;
import advancedbrewing.item.ItemBucketPotion;
import advancedbrewing.proxy.Proxy;
import advancedbrewing.tileentity.TileEntityBrewery;
import advancedbrewing.tileentity.TileEntityInfuser;
import advancedbrewing.tileentity.TileEntityVaporizer;
import advancedbrewing.utils.Localization;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name = "AdvancedBrewing", version = "0.0.2", useMetadata = false, modid = "AdvancedBrewing", acceptedMinecraftVersions = "[1.6,1.7)", dependencies = "required-after:Forge@[9.10.0.800,)")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class AdvancedBrewing {

	public static Configuration configuration;

	// potions
	public static List<PotionDefinition> potionDefinitions = new ArrayList<PotionDefinition>();
	public static Map<Integer, PotionDefinition> potionDefinitionMappings = new HashMap<Integer, PotionDefinition>();

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

	@SidedProxy(clientSide = "advancedbrewing.proxy.ProxyClient", serverSide = "advancedbrewing.proxy.Proxy")
	public static Proxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		configuration = new Configuration(new File(evt.getModConfigurationDirectory(), "advancedbrewing/main.conf"));

		try {
			configuration.load();

			// machinery

			// blocks
			int breweryIdleBlockID = configuration.getBlock("breweryIdleBlockID", 1000).getInt(1000);
			breweryIdleBlock = new BlockBrewery(breweryIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.brewery.idle.name");
			GameRegistry.registerBlock(breweryIdleBlock, ItemBlockLocalized.class, breweryIdleBlock.getUnlocalizedName());

			int breweryBurningBlockID = configuration.getBlock("breweryBurningBlockID", 1001).getInt(1001);
			breweryBurningBlock = new BlockBrewery(breweryBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.brewery.burning.name");
			GameRegistry.registerBlock(breweryBurningBlock, ItemBlockLocalized.class, breweryBurningBlock.getUnlocalizedName());

			int infuserIdleBlockID = configuration.getBlock("infuserIdleBlockID", 1002).getInt(1002);
			infuserIdleBlock = new BlockInfuser(infuserIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.infuser.idle.name");
			GameRegistry.registerBlock(infuserIdleBlock, ItemBlockLocalized.class, infuserIdleBlock.getUnlocalizedName());

			int infuserBurningBlockID = configuration.getBlock("infuserBurningBlockID", 1003).getInt(1003);
			infuserBurningBlock = new BlockInfuser(infuserBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.infuser.burning.name");
			GameRegistry.registerBlock(infuserBurningBlock, ItemBlockLocalized.class, infuserBurningBlock.getUnlocalizedName());
			
			int vaporizerIdleBlockID = configuration.getBlock("vaporizerIdleBlockID", 1004).getInt(1004);
			vaporizerIdleBlock = new BlockVaporizer(vaporizerIdleBlockID, false).setCreativeTab(CreativeTabs.tabBrewing).setUnlocalizedName("block.vaporizer.idle.name");
			GameRegistry.registerBlock(vaporizerIdleBlock, ItemBlockLocalized.class, vaporizerIdleBlock.getUnlocalizedName());

			int vaporizerBurningBlockID = configuration.getBlock("vaporizerBurningBlockID", 1005).getInt(1005);
			vaporizerBurningBlock = new BlockVaporizer(vaporizerBurningBlockID, true).setLightValue(0.875F).setUnlocalizedName("block.vaporizer.burning.name");
			GameRegistry.registerBlock(vaporizerBurningBlock, ItemBlockLocalized.class, vaporizerBurningBlock.getUnlocalizedName());

			// potions

			// bucketPotion
			int bucketPotionItemID = configuration.getItem("bucketPotionItemID", 10000).getInt(10000);
			bucketPotionItem = new ItemBucketPotion(bucketPotionItemID);
			bucketPotionItem.setContainerItem(Item.bucketEmpty);

			// autoPotion
			int autoPotionItemID = configuration.getItem("autoPotionItemID", 10001).getInt(10001);
			autoPotionItem = new ItemAutoPotion(autoPotionItemID);

			// arrowPotion
			int arrowPotionItemID = configuration.getItem("arrowPotionItemID", 10002).getInt(10002);
			arrowPotionItem = new ItemArrowPotion(arrowPotionItemID);
	        BlockDispenser.dispenseBehaviorRegistry.putObject(arrowPotionItem, new DispenserBehaviorArrowPotion());
			
			int i = 0;
			for (PotionDefinition potionDefinition : potionDefinitions) {
				// ignore water definition
				if (i == 0) {
					i++;
					continue;
				}
				
				int potionID = potionDefinition.getPotionID();

				// fluid
				Fluid fluid = new FluidPotion(potionDefinition.getName(), potionDefinition.getColor());
				FluidRegistry.registerFluid(fluid);
				FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucketPotionItem, 1, potionID), FluidContainerRegistry.EMPTY_BUCKET);
				FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(Item.potion, 1, potionID), FluidContainerRegistry.EMPTY_BOTTLE);

				// block
				int potionBlockID = configuration.getBlock("potionBlockID." + potionDefinition.getName(), 1010 + i).getInt(1010 + i);
				Block potionBlock = new BlockPotion(potionBlockID, fluid, Material.water).setUnlocalizedName("block." + potionDefinition.getName() + ".name");
				GameRegistry.registerBlock(potionBlock, ItemBlockLocalized.class, potionBlock.getUnlocalizedName());
				fluid.setBlockID(potionBlock);
				potionDefinition.setBlock(potionBlock);
				
				i++;
			}

			// register handlers
			MinecraftForge.EVENT_BUS.register(new HandlerTexture());
			MinecraftForge.EVENT_BUS.register(new HandlerBucket());
			MinecraftForge.EVENT_BUS.register(new HandlerBow());

			Localization.addLocalization("/lang/advancedbrewing/", "en_US");
		}
		finally {
			if (configuration.hasChanged()) {
				configuration.save();
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		// register handlers
		NetworkRegistry.instance().registerGuiHandler(this, new HandlerGui());

		// entities
		GameRegistry.registerTileEntity(TileEntityBrewery.class, "entity.brewery");
		GameRegistry.registerTileEntity(TileEntityInfuser.class, "entity.infuser");
		GameRegistry.registerTileEntity(TileEntityVaporizer.class, "entity.vaporizer");
		
		// renderers
        proxy.registerRenderers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	private static void registerPotionDefinition(String name, Type type, int[] potionIDs) {
		PotionDefinition  potionDefinition = new PotionDefinition(name, type, potionIDs);
		potionDefinitions.add(potionDefinition);
		for (int potionID : potionIDs) {
			potionDefinitionMappings.put(potionID, potionDefinition);
		}
	}
	
	static {
		registerPotionDefinition("water", Type.blue, new int[] { 0 });
		
		// base potions
		registerPotionDefinition("potion.awkward", Type.blue, new int[] { 16 });
		registerPotionDefinition("potion.thick", Type.blue, new int[] { 32 });
		registerPotionDefinition("potion.mundane_extended", Type.blue, new int[] { 64 });
		registerPotionDefinition("potion.mundane", Type.blue, new int[] { 8192 });

		// regeneration potions
		registerPotionDefinition("potion.regeneration", Type.pink, new int[] { 8193 });
		registerPotionDefinition("potion.regeneration_extended", Type.pink, new int[] { 8257 });
		registerPotionDefinition("potion.regeneration_2", Type.pink, new int[] { 8225 });

		// swiftness potions
		registerPotionDefinition("potion.swiftness", Type.turquoise, new int[] { 8194 });
		registerPotionDefinition("potion.swiftness_extended", Type.turquoise, new int[] { 8258 });
		registerPotionDefinition("potion.swiftness_2", Type.turquoise, new int[] { 8226 });

		// fire resistance potions
		registerPotionDefinition("potion.fire_resistance", Type.yellow, new int[] { 8195, 8227 });
		registerPotionDefinition("potion.fire_resistance_extended", Type.yellow, new int[] { 8259 });

		// healing potions
		registerPotionDefinition("potion.healing", Type.red, new int[] { 8197, 8261 });
		registerPotionDefinition("potion.healing_2", Type.red, new int[] { 8229 });

		// night vision potions
		registerPotionDefinition("potion.night_vision", Type.darkblue, new int[] { 8198, 8230 });
		registerPotionDefinition("potion.night_vision_extended", Type.darkblue, new int[] { 8262 });

		// strength potions
		registerPotionDefinition("potion.strength", Type.darkred, new int[] { 8201 });
		registerPotionDefinition("potion.strength_extended", Type.darkred, new int[] { 8265 });
		registerPotionDefinition("potion.strength_2", Type.darkred, new int[] { 8233 });

		// invisibility potions
		registerPotionDefinition("potion.invisibility", Type.grey, new int[] { 8206, 8238 });
		registerPotionDefinition("potion.invisibility_extended", Type.grey, new int[] { 8270 });
	}
}
