package al132.elementalresearch.event;

import al132.elementalresearch.Reference;
import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.compat.ct.StageGroup;
import al132.elementalresearch.gui.GuiShop;
import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import al132.elementalresearch.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static al132.elementalresearch.Reference.*;
import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;

public class ClientEventHandler {

    final static ResourceLocation FIRE_ICON = new ResourceLocation(Reference.MODID, "textures/gui/fire.png");

    int x = 10;
    int y = 100;
    int width = 50;
    int height = 50;
    int margin = 2;


    //(priority = EventPriority.HIGHEST, receiveCanceled = true)
    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent e) {
        ResearchCapability research = Minecraft.getMinecraft().player.getCapability(ResearchCapability.RESEARCH_CAPABILITY, null);
        if (research != null) {
            if (e.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
                int firePoints = research.getValue(FIRE);
                int waterPoints = research.getValue(WATER);
                int airPoints = research.getValue(AIR);
                int earthPoints = research.getValue(EARTH);
                boolean fireVisible = research.isTypeVisible(FIRE);
                boolean waterVisible = research.isTypeVisible(WATER);
                boolean airVisible = research.isTypeVisible(AIR);
                boolean earthVisible = research.isTypeVisible(EARTH);

                if (fireVisible || waterVisible || airVisible || earthVisible) {
                    RenderUtils.drawBeveledBox(x, y, x + width, y + height, Color.darkGray.getRGB(), Color.black.getRGB(), 0x55555555);
                    if (fireVisible) {
                        RenderUtils.renderText(Minecraft.getMinecraft(),
                                x + margin, y + margin, "F: " + firePoints, FIRE_COLOR);
                    }
                    if (waterVisible) {
                        RenderUtils.renderText(Minecraft.getMinecraft(),
                                x + margin, y + margin + 12, "W: " + waterPoints, WATER_COLOR);
                    }
                    if (airVisible) {
                        RenderUtils.renderText(Minecraft.getMinecraft(),
                                x + margin, y + margin + 24, "A: " + airPoints, AIR_COLOR);
                    }
                    if (earthVisible) {
                        RenderUtils.renderText(Minecraft.getMinecraft(),
                                x + margin, y + margin + 36, "E: " + earthPoints, EARTH_COLOR);
                    }

                    // drawTexture();

                }
            }
        }
    }

    public void drawTexture() {
        ResourceLocation FIRE_ICON = new ResourceLocation(Reference.MODID, "textures/gui/fire.png");
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(FIRE_ICON);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(50, 50, 0, 0, 16, 16);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void openShop() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ResearchCapability research = ResearchCapability.get(player);
        for (Map.Entry<Integer, ShopEntry> mapEntry : ShopRegistry.registry.entrySet()) {
            List<StageGroup> requiredStages = mapEntry.getValue().requiredGamestages;
            if (research.shopQuantity.get(mapEntry.getKey()).quantityRemaining != 0
                    && requiredStages.stream().allMatch(group -> group.evaluateFor(player))) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiShop());
                return;
            }
        }
        player.sendMessage(new TextComponentString("No research options are currently available"));

    }
/*
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (KeyBindings.shopKey.isPressed()) {
            openShop();
        }
    }*/
}
