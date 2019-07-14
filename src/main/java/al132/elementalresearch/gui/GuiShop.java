package al132.elementalresearch.gui;

import al132.elementalresearch.ElementalResearch;
import al132.elementalresearch.Reference;
import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.capabilities.ResearchCapability.ResearchType;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPurchase;
import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import al132.elementalresearch.utils.RenderUtils;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;
import static al132.elementalresearch.gui.ArrowButton.Direction.LEFT;
import static al132.elementalresearch.gui.ArrowButton.Direction.RIGHT;

public class GuiShop extends GuiScreen {

    final static ResourceLocation ELEM_ICONS = new ResourceLocation(Reference.MODID, "textures/gui/icons.png");

    private static int buttonX = 0;
    private static int buttonY = 0;
    private int pageIndex = 0;

    int buttonWidthAndMargin = CustomButton.BASE_WIDTH + 5;
    int buttonHeightAndMargin = 85;
    int startX = 30;


    public static void resetButtonXY() {
        resetButtonX();
        buttonY = 0;
    }

    public static void resetButtonX() {
        buttonX = 30;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (typedChar == 'd' || typedChar == 'D') pageRight();
        else if (typedChar == 'a' || typedChar == 'A') pageLeft();
    }

    @Override
    public void initGui() {
        refreshButtons();
    }

    private void refreshButtons() {
        this.buttonList.clear();
        int buttonIndex = 0;
        resetButtonXY();
        buttonList.add(new ArrowButton(buttonIndex, 5, 240, LEFT));
        buttonIndex++;
        buttonList.add(new ArrowButton(buttonIndex, 5 + 32, 240, RIGHT));
        buttonIndex++;
        if (this.mc == null || this.mc.player == null) return;
        ResearchCapability research = ResearchCapability.get(this.mc.player);
        if (research == null) return;
        List<UUID> shopIndices = new ArrayList<>();
        if (research != null) {
            ShopRegistry.registry.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).forEach(registryEntry -> {
                //for (Map.Entry<UUID, ShopEntry> registryEntry : ShopRegistry.registry.entrySet()) {

                //for (int i = 0; i < ShopRegistry.registry.size(); i++) {
                if (research.shopQuantity.get(registryEntry.getKey()).quantityRemaining != 0
                        && ShopRegistry.registry.get(registryEntry.getKey()).requiredGamestages.stream()
                        .allMatch(x -> x.evaluateFor(this.mc.player).result)) {
                    shopIndices.add(registryEntry.getKey());
                }
            });

            for (int i = (6 * pageIndex); i < (6 * (pageIndex + 1)); i++) {
                if (shopIndices.size() > i) {
                    ShopEntry entry = ShopRegistry.registry.get(shopIndices.get(i));
                    if (entry != null) {
                        this.buttonList.add(new CustomButton(buttonIndex, (startX) + buttonX, (this.height / 4) + buttonY,
                                "", ShopRegistry.registry.get(shopIndices.get(i)), shopIndices.get(i)));
                        buttonX += buttonWidthAndMargin;
                        if ((buttonIndex - 1) % 3 == 0) {
                            buttonY += buttonHeightAndMargin;
                            resetButtonX();
                        }
                        buttonIndex++;
                    }
                }
            }
        }
        if (!canPageLeft()) buttonList.get(0).visible = false;
        if (!canPageRight()) buttonList.get(1).visible = false;
    }


    public boolean canPageLeft() {
        return pageIndex > 0;
    }

    public boolean canPageRight() {
        return (this.buttonList.size() - 2) / 6 > this.pageIndex;
    }

    public void pageLeft() {
        if (canPageLeft()) pageIndex--;
    }

    public void pageRight() {
        if (canPageRight()) pageIndex++;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button instanceof CustomButton) {
            CustomButton clicked = (CustomButton) button;
            PacketHandler.INSTANCE.sendToServer(new PacketPurchase(clicked.shopID));
        } else if (button instanceof ArrowButton) {
            if (((ArrowButton) button).direction == LEFT) pageLeft();
            else if (((ArrowButton) button).direction == RIGHT) pageRight();
        }
        //refreshButtons();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        refreshButtons();
        ResearchCapability research = ResearchCapability.get(this.mc.player);
        //this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        // GlStateManager.pushMatrix();
        // GlStateManager.scale(2.0F, 2.0F, 2.0F);
        //this.drawCenteredString(this.fontRenderer, "Shop", this.width / 4, 30, 16777215);
        //RenderUtils.drawBeveledBox(boxX, boxY, boxX + boxWidth, boxY + boxHeight, Color.darkGray.getRGB(), Color.black.getRGB(), 0x55555555);
        // RenderUtils.renderText(this.mc, boxX + boxMargin, boxY + boxMargin, "$" + research.getMoney());
        // GlStateManager.popMatrix();
        GuiButton _hovered = this.buttonList.stream().filter(GuiButton::isMouseOver).findFirst().orElse(null);
        if (_hovered != null) {
            CustomButton hovered = (CustomButton) _hovered;
            //Requirement Box
            int requirementBoxX = 80;
            int requirementBoxY = 5;
            if (!hovered.entry.getInputs().isEmpty()) {
                RenderUtils.drawBeveledBox(requirementBoxX, requirementBoxY,
                        requirementBoxX + 200, requirementBoxY + 60, Color.darkGray.getRGB(), Color.black.getRGB(), 0xAA555555);

                RenderUtils.renderText(this.mc, requirementBoxX + 4, requirementBoxY + 4,
                        "Additional Requirements: ");
                RenderUtils.renderText(this.mc, requirementBoxX + 4, requirementBoxY + 20, "Consume: " + hovered.entry.consumeItemInputs);
                RenderUtils.renderText(this.mc, requirementBoxX + 4, requirementBoxY + 40, "Items Required:");
                for (int i = 0; i < hovered.entry.getOutputs().size(); i++) {
                    RenderUtils.renderItemStackWithCount(this.mc, mc.getRenderItem(),
                            hovered.entry.getInputs().get(i), requirementBoxX + 64 + (i * 18), requirementBoxY + 40, true);
                }
            }

            //Reward Box
            int rewardBoxX = requirementBoxX + 200 + 5;
            int rewardBoxY = 5;
            RenderUtils.drawBeveledBox(rewardBoxX, rewardBoxY,
                    rewardBoxX + 200, rewardBoxY + 60, Color.darkGray.getRGB(), Color.black.getRGB(), 0xAA555555);
            if (!hovered.entry.commands.isEmpty()) {
                RenderUtils.renderText(this.mc, rewardBoxX + 4, rewardBoxY + 4, "Runs Commands: " + hovered.entry.commands.size());
            }
            if (!hovered.entry.givenGamestages.isEmpty()) {
                RenderUtils.renderText(this.mc, rewardBoxX + 4, rewardBoxY + 16, "Unlocks Gamestages: " + hovered.entry.givenGamestages);
            }
            if (hovered.entry.experienceGiven > 0) {
                RenderUtils.renderText(this.mc, rewardBoxX + 4, rewardBoxY + 28, "XP Levels Received: " + hovered.entry.experienceGiven);
            }
            if (!hovered.entry.getOutputs().isEmpty()) {
                RenderUtils.renderText(this.mc, rewardBoxX + 4, rewardBoxY + 40, "Items Given:");
                for (int i = 0; i < hovered.entry.getOutputs().size(); i++) {
                    RenderUtils.renderItemStackWithCount(this.mc, mc.getRenderItem(),
                            hovered.entry.getOutputs().get(i), rewardBoxX + 64 + (i * 18), rewardBoxY + 40, true);
                }
            }

        }
        resetButtonXY();
        int buttonIndex = 0;
        for (int i = 2; i < 8; i++) {
            //}
            //for (GuiButton _button : this.buttonList) {
            if (buttonList.size() > i) {
                GuiButton _button = buttonList.get(i);
                ShopEntry entry = ((CustomButton) _button).entry;
                if (entry.requiredGamestages.stream().allMatch(x -> x.evaluateFor(this.mc.player).result)) {
                    Map<ResearchType, Integer> costs = entry.calculateCostForPlayer(this.mc.player, ((CustomButton) _button).shopID);
                    int x = startX + buttonX;
                    int y = this.height / 4 + buttonY;
                    drawItemStack(entry.getIcon(), x + 60, y + 14, "");//$" + entry.fireCost);
                    try {
                        List<String> desc = Lists.newArrayList(WordUtils.wrap(entry.description, 17).split("\n"));
                        for (int line = 0; line < desc.size(); line++) {
                            RenderUtils.renderText(this.mc, x + 36, y + 30 + (line * 12), desc.get(line).replaceAll("\r", ""));
                        }
                    } catch (Exception e) {
                        ElementalResearch.logger.warn("Failed to render shop entry description");
                    }
                    int textX = 16;
                    RenderUtils.renderText(this.mc, x + textX, y + 4, entry.displayName);
                    RenderUtils.renderText(this.mc, x + textX, y + 16, "" + costs.get(FIRE), Reference.FIRE_COLOR);
                    RenderUtils.renderText(this.mc, x + textX, y + 28, "" + costs.get(WATER), Reference.WATER_COLOR);
                    RenderUtils.renderText(this.mc, x + textX, y + 40, "" + costs.get(AIR), Reference.AIR_COLOR);
                    RenderUtils.renderText(this.mc, x + textX, y + 52, "" + costs.get(EARTH), Reference.EARTH_COLOR);
                    RenderUtils.renderText(this.mc, x + textX, y + 66, "" + entry.experienceRequired, Reference.XP_COLOR);

                    //drawIcons(x + 2, y + 14);

                    buttonX += buttonWidthAndMargin;
                    if ((buttonIndex + 1) % 3 == 0) {
                        buttonY += buttonHeightAndMargin;
                        resetButtonX();
                    }
                    buttonIndex++;
                }
            }
        }
    }

/*
    public void drawIcons(int startX, int startY) {
        int size = 12;
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICONS);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 0, 16, size, size);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY + size, size, 16, size, size);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY + (size * 2), size * 2, 16, size, size);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY + (size * 3), size * 3, 16, size, size);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

 */


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawItemStack(ItemStack stack, int x, int y, String text) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.translate(0.0f, 0.0f, 32.0f);
        this.zLevel = 200.0f;
        this.itemRender.zLevel = 200.0f;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y + 5, text);
        this.zLevel = 0.0f;
        this.itemRender.zLevel = 0.0f;
    }
}