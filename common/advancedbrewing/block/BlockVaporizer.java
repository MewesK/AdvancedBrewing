package advancedbrewing.block;

import advancedbrewing.AdvancedBrewing;
import advancedbrewing.tileentity.TileEntityVaporizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockVaporizer extends BlockMachine<TileEntityVaporizer> {

	public BlockVaporizer(int blockID, boolean isActive) {
		super(blockID, isActive);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return AdvancedBrewing.vaporizerIdleBlock.blockID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return AdvancedBrewing.vaporizerIdleBlock.blockID;
	}

	@Override
	public void updateBlockState(boolean isBrewing, World par1World, int par2, int par3, int par4) {
		int metadata = par1World.getBlockMetadata(par2, par3, par4);
		TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
		keepInventory = true;
		par1World.setBlock(par2, par3, par4, isBrewing ? AdvancedBrewing.vaporizerBurningBlock.blockID : AdvancedBrewing.vaporizerIdleBlock.blockID);
		keepInventory = false;
		par1World.setBlockMetadataWithNotify(par2, par3, par4, metadata, 2);
		if (tileentity != null) {
			tileentity.validate();
			par1World.setBlockTileEntity(par2, par3, par4, tileentity);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityVaporizer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("advancedbrewing:vaporizer.side");
		this.iconFront = par1IconRegister.registerIcon(this.isActive ? "advancedbrewing:vaporizer.front.burning" : "advancedbrewing:vaporizer.front.idle");
		this.iconTop = par1IconRegister.registerIcon("advancedbrewing:vaporizer.top");
	}
}