package advancedbrewing;

import net.minecraft.block.Block;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HandlerTexture {

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		for (PotionDefinition potionDefinition : AdvancedBrewing.potionDefinitions) {
			Fluid fluid = FluidRegistry.getFluid(potionDefinition.getName());
			Block block = potionDefinition.getBlock();
			if (fluid != null && block != null) {
				fluid.setIcons(block.getIcon(0, 0), block.getIcon(2, 0));
			}
		}
	}
}
