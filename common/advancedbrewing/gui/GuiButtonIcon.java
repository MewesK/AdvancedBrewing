package advancedbrewing.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;


@SideOnly(Side.CLIENT)
public class GuiButtonIcon extends GuiButton {

    protected static final ResourceLocation buttonTextures = new ResourceLocation("advancedbrewing", "textures/gui/widgets.png");

    // backup for ledger support
    public int xPosition_;
    public int yPosition_;
    
    public int iconIndexX;
    public int iconIndexY;
    
    public GuiButtonIcon(int id, int xPosition, int yPosition, int iconIndexX, int iconIndexY) {
        super(id, xPosition, yPosition, 20, 20, "");
        
        this.xPosition_ = xPosition;
        this.yPosition_ = yPosition;
        
        this.iconIndexX = iconIndexX;
        this.iconIndexY = iconIndexY;
    }

	@Override
    public void drawButton(Minecraft minecraft, int par2, int par3) {
        if (this.drawButton) {
            minecraft.getTextureManager().bindTexture(GuiButtonIcon.buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            boolean mouseOver = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            
            if (mouseOver) {
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 20, 0, this.width, this.height);
            } else {
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, iconIndexX * 20, iconIndexY * 20, this.width, this.height);
        }
    }
	
	public int getWidth() {
		return this.width;
	}
}
