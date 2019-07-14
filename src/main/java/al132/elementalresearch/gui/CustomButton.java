package al132.elementalresearch.gui;

import al132.elementalresearch.Reference;
import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.shop.PurchaseValidator;
import al132.elementalresearch.shop.ShopEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class CustomButton extends GuiButton {

    protected static final ResourceLocation DISABLED = new ResourceLocation(Reference.MODID, "textures/gui/button_disabled.png");
    protected static final ResourceLocation AVAILABLE = new ResourceLocation(Reference.MODID, "textures/gui/button_available.png");
    protected static final ResourceLocation HOVERED = new ResourceLocation(Reference.MODID, "textures/gui/button_hovered.png");

    final static ResourceLocation ELEM_ICONS = new ResourceLocation(Reference.MODID, "textures/gui/icons.png");

    static ResourceLocation currentLocation = null;
    public ShopEntry entry;
    public UUID shopID;
    public final static int BASE_WIDTH = 150;

    public CustomButton(int buttonId, int x, int y, String buttonText, ShopEntry entry, UUID shopID) {//}, ItemStack stack, int price) {
        super(buttonId, x, y, 150, 80, buttonText);
        this.entry = entry;
        this.shopID = shopID;
    }

    public void updateStatus() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            ResearchCapability research = ResearchCapability.get(player);
            this.enabled = PurchaseValidator.hasItems(player,this.entry.getInputs()) && research.canAfford(player, this.entry, this.shopID);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            updateStatus();
            FontRenderer fontrenderer = mc.fontRenderer;
            //mc.getTextureManager().bindTexture(CUSTOM_TEXTURES);
            // GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawIcons(x + 2, y + 14);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            if (i == 0) mc.getTextureManager().bindTexture(DISABLED);
            else if (i == 1) mc.getTextureManager().bindTexture(AVAILABLE);
            else if (i == 2) mc.getTextureManager().bindTexture(HOVERED);

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x, this.y, 0 /*i * 80*/, 0, this.width, this.height);
            drawIcons(x + 2, y + 14);
            GlStateManager.popMatrix();

            //this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            //this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);

        }
    }

    public void drawIcons(int startX, int startY) {
        int size = 12;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ELEM_ICONS);
        this.drawTexturedModalRect(startX, startY, 0, 16, size, size);
        this.drawTexturedModalRect(startX, startY + size, size, 16, size, size);
        this.drawTexturedModalRect(startX, startY + (size * 2), size * 2, 16, size, size);
        this.drawTexturedModalRect(startX, startY + (size * 3), size * 3, 16, size, size);
        this.drawTexturedModalRect(startX, startY + (size * 4), size * 4, 16, size, size);
    }
}
