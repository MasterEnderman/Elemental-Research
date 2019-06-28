package al132.elementalresearch.gui;

import al132.elementalresearch.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static al132.elementalresearch.gui.ArrowButton.Direction.LEFT;

public class ArrowButton extends GuiButton {

    enum Direction {
        LEFT, RIGHT;
    }

    static ResourceLocation RIGHT_ARROW = new ResourceLocation(Reference.MODID, "textures/gui/arrows.png");
    public Direction direction;

    public ArrowButton(int buttonId, int x, int y, Direction direction) {//}, String buttonText, ShopEntry entry, int shopID) {//}, ItemStack stack, int price) {
        super(buttonId, x, y, 16, 16, "");
        this.direction = direction;
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(RIGHT_ARROW);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            int textureY = 0;
            if (this.direction == LEFT) textureY = 16;
            this.drawTexturedModalRect(this.x, this.y, 0 /*i * 80*/, textureY, this.width, this.height);
            //this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            //this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

        }
    }
}