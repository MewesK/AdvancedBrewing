/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ItemGlintOverlayRenderer implements IItemRenderer {

	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private static RenderItem RENDERITEM = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		 return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.ENTITY_BOBBING || (helper == ItemRendererHelper.ENTITY_ROTATION && RenderManager.instance.options.fancyGraphics);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		if (type == ItemRenderType.ENTITY) {
	        if (RenderManager.instance.options.fancyGraphics) {
                if (RenderItem.renderInFrame) {
                    GL11.glTranslatef(0.0F, -0.05F, -0.084375F);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }
                
                GL11.glTranslatef(-0.5F, -0.25F, -(0.084375F * RENDERITEM.getMiniItemCount(itemStack) / 2.0F));
                
    			this.renderItem3D(type, itemStack, data);
			} else {               
                if (!RenderItem.renderInFrame) {
                    GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
                }
                
				TextureManager renderEngine = FMLClientHandler.instance().getClient().renderEngine;
				Tessellator tessellator = Tessellator.instance;

				// render colored overlay
        		renderEngine.bindTexture(renderEngine.getResourceLocation(itemStack.getItemSpriteNumber()));
        		setColorByItemStack(itemStack);
        		Icon icon = getIcon(itemStack, 0);
        		
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((-0.5F), (-0.25F), 0.0D, icon.getMinU(), icon.getMaxV());
                tessellator.addVertexWithUV((0.5F), (-0.25F), 0.0D, icon.getMaxU(), icon.getMaxV());
                tessellator.addVertexWithUV((0.5F), (0.75F), 0.0D, icon.getMaxU(), icon.getMinV());
                tessellator.addVertexWithUV((-0.5F), (0.75F), 0.0D, icon.getMinU(), icon.getMinV());
                tessellator.draw();

        		// render uncolored icon
        		setColorByItemStack(itemStack);
        		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        		icon = getIcon(itemStack, 1);
        		
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((-0.5F), (-0.25F), 0.0D, icon.getMinU(), icon.getMaxV());
                tessellator.addVertexWithUV((0.5F), (-0.25F), 0.0D, icon.getMaxU(), icon.getMaxV());
                tessellator.addVertexWithUV((0.5F), (0.75F), 0.0D, icon.getMaxU(), icon.getMinV());
                tessellator.addVertexWithUV((-0.5F), (0.75F), 0.0D, icon.getMinU(), icon.getMinV());
                tessellator.draw();
			}
		}
		else if (type == ItemRenderType.EQUIPPED) {
			this.renderItem3D(type, itemStack, data);
		}
		else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			this.renderItem3D(type, itemStack, data);
		}
		else if (type == ItemRenderType.INVENTORY) {
			this.renderItem2D(type, itemStack, data);
		}
	}

	private void renderItem3D(ItemRenderType type, ItemStack itemStack, Object... data) {
		TextureManager renderEngine = FMLClientHandler.instance().getClient().renderEngine;
		Tessellator tessellator = Tessellator.instance;
		
		if (shouldRenderOverlay(itemStack)) {
    		// render colored overlay
    		renderEngine.bindTexture(renderEngine.getResourceLocation(itemStack.getItemSpriteNumber()));
    		setColorByItemStack(itemStack);
    		Icon icon = getIcon(itemStack, 0);
    		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
    
    		// render glint
    		if (itemStack.hasEffect(0)) {
    			renderEngine.bindTexture(RES_ITEM_GLINT);
    			
    			GL11.glDepthFunc(GL11.GL_EQUAL);
    			GL11.glDisable(GL11.GL_LIGHTING);
    			GL11.glEnable(GL11.GL_BLEND);
    			GL11.glMatrixMode(GL11.GL_TEXTURE);
    			
    			GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
    
        		// first pass
    			GL11.glPushMatrix();
    			GL11.glScalef(0.125F, 0.125F, 0.125F);
    			float f9 = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
    			GL11.glTranslatef(f9, 0.0F, 0.0F);
    			GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
    			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
    			GL11.glPopMatrix();
    			
        		// second pass
    			GL11.glPushMatrix();
    			GL11.glScalef(0.125F, 0.125F, 0.125F);
    			f9 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
    			GL11.glTranslatef(-f9, 0.0F, 0.0F);
    			GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
    			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
    			GL11.glPopMatrix();
    			
    			GL11.glMatrixMode(GL11.GL_MODELVIEW);
    			GL11.glDisable(GL11.GL_BLEND);
        		GL11.glEnable(GL11.GL_LIGHTING);
    			GL11.glDepthFunc(GL11.GL_LEQUAL);
    		}
    	}

		// render uncolored icon
		renderEngine.bindTexture(renderEngine.getResourceLocation(itemStack.getItemSpriteNumber()));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Icon icon = getIcon(itemStack, 1);
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
	}

	private void renderItem2D(ItemRenderType type, ItemStack itemStack, Object... data) {
		TextureManager renderEngine = FMLClientHandler.instance().getClient().renderEngine;
		Tessellator tessellator = Tessellator.instance;

		if (shouldRenderOverlay(itemStack)) {
    		// render colored overlay
    		renderEngine.bindTexture(renderEngine.getResourceLocation(itemStack.getItemSpriteNumber()));
    		setColorByItemStack(itemStack);
    		Icon icon = getIcon(itemStack, 0);
    		RENDERITEM.renderIcon(0, 0, icon, 16, 16);
    
    		// render glint
    		if (itemStack.hasEffect(0)) {
        		renderEngine.bindTexture(RES_ITEM_GLINT);
        		
        		GL11.glDepthFunc(GL11.GL_GREATER);
        		GL11.glDepthMask(false);
        		GL11.glEnable(GL11.GL_BLEND);
        
    			GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
    			
        		// first pass
    			float f2 = Minecraft.getSystemTime() % 3000L / 3000.0F * 256.0F;
    			tessellator.startDrawingQuads();
    			tessellator.addVertexWithUV(-2, -2 + 20, -50.0F, (f2 + 20 * 4.0F) * 0.00390625F, 20 * 0.00390625F);
    			tessellator.addVertexWithUV(-2 + 20, -2 + 20, -50.0F, (f2 + 20 + 20 * 4.0F) * 0.00390625F, 20 * 0.00390625F);
    			tessellator.addVertexWithUV(-2 + 20, -2, -50.0F, (f2 + 20) * 0.00390625F, 0);
    			tessellator.addVertexWithUV(-2, -2, -50.0F, (f2 ) * 0.00390625F, 0);
    			tessellator.draw();
    
        		// second pass
    			f2 = Minecraft.getSystemTime() % 4873L / 4873.0F * 256.0F;
    			tessellator.startDrawingQuads();
    			tessellator.addVertexWithUV(-2, -2 + 20, -50.0F, (f2 + 20 * -1.0F) * 0.00390625F, 20 * 0.00390625F);
    			tessellator.addVertexWithUV(-2 + 20, -2 + 20, -50.0F, (f2 + 20 + 20 * -1.0F) * 0.00390625F, 20 * 0.00390625F);
    			tessellator.addVertexWithUV(-2 + 20, -2, -50.0F, (f2 + 20) * 0.00390625F, 0);
    			tessellator.addVertexWithUV(-2, -2, -50.0F, (f2 ) * 0.00390625F, 0);
    			tessellator.draw();
        
        		GL11.glDisable(GL11.GL_BLEND);
        		GL11.glDepthMask(true);
        		GL11.glDepthFunc(GL11.GL_LEQUAL);
    		}
		}

		// render uncolored icon
		renderEngine.bindTexture(renderEngine.getResourceLocation(itemStack.getItemSpriteNumber()));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Icon icon = getIcon(itemStack, 1);
		RENDERITEM.renderIcon(0, 0, icon, 16, 16);
	}
	
	private void setColorByItemStack(ItemStack itemStack) {
        int color = Item.itemsList[itemStack.getItem().itemID].getColorFromItemStack(itemStack, 0);
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
	}

	protected Icon getIcon(ItemStack itemStack, int pass) {
		Item item = itemStack.getItem();
		return item.getIcon(itemStack, pass);
	}

	protected boolean shouldRenderOverlay(ItemStack itemStack) {
		return itemStack.getItemDamage() > 0;
	}
}