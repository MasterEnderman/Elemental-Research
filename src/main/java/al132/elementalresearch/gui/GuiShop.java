package al132.elementalresearch.gui;

import al132.elementalresearch.Reference;
import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.capabilities.ResearchCapability.ResearchType;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPurchase;
import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import al132.elementalresearch.utils.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;
import static al132.elementalresearch.gui.ArrowButton.Direction.LEFT;
import static al132.elementalresearch.gui.ArrowButton.Direction.RIGHT;

public class GuiShop extends GuiScreen {

    private static int buttonX = 0;
    private static int buttonY = 0;
    private int pageIndex = 0;

    int buttonWidthAndMargin = 125;
    int buttonHeightAndMargin = 85;


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
        buttonList.add(new ArrowButton(buttonIndex, this.width - 32, this.height / 2, RIGHT));
        buttonIndex++;
        buttonList.add(new ArrowButton(buttonIndex, 64, this.height / 2, LEFT));
        buttonIndex++;
        ResearchCapability research = ResearchCapability.get(this.mc.player);
        List<Integer> shopIndices = new ArrayList<>();
        if (research != null) {
            for (int i = 0; i < ShopRegistry.registry.size(); i++) {
                if (research.shopQuantity.get(i).quantityRemaining != 0
                        && ShopRegistry.registry.get(i).requiredGamestages.stream().allMatch(x -> x.evaluateFor(this.mc.player))) {
                    shopIndices.add(i);
                }
            }

            for (int i = (6 * pageIndex); i < (6 * (pageIndex + 1)); i++) {
                if (shopIndices.size() > i) {
                    ShopEntry entry = ShopRegistry.registry.get(shopIndices.get(i));
                    if (entry != null) {
                        //if (research.shopQuantity.get(i).quantityRemaining != 0
                        //        && entry.requiredGamestages.stream().allMatch(x -> x.evaluateFor(this.mc.player))) {
                        this.buttonList.add(new CustomButton(buttonIndex, (this.width / 8) + buttonX, (this.height / 4) + buttonY,
                                "", ShopRegistry.registry.get(shopIndices.get(i)), shopIndices.get(i)));
                        buttonX += buttonWidthAndMargin;
                        if ((buttonIndex - 1) % 3 == 0) {
                            buttonY += buttonHeightAndMargin;
                            resetButtonX();
                        }
                        buttonIndex++;

                        // }
                    }
                }
            }
        }
    }

    public void pageLeft() {
        if (pageIndex > 0) pageIndex--;
    }

    public void pageRight() {
        if ((this.buttonList.size() - 2) / 6 > this.pageIndex) pageIndex++;
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
            int boxX = this.width / 4 - 24;
            int boxY = 5;
            RenderUtils.drawBeveledBox(boxX, boxY,
                    boxX + 200, boxY + 60, Color.darkGray.getRGB(), Color.black.getRGB(), 0xAA555555);
            if (!hovered.entry.commands.isEmpty()) {
                RenderUtils.renderText(this.mc, boxX + 4, boxY + 4, "Runs Commands: " + hovered.entry.commands.size());
            }
            if (!hovered.entry.givenGamestages.isEmpty()) {
                RenderUtils.renderText(this.mc, boxX + 4, boxY + 16, "Unlocks Gamestages: " + hovered.entry.givenGamestages);
            }
            if (hovered.entry.experienceGiven > 0) {
                RenderUtils.renderText(this.mc, boxX + 4, boxY + 28, "XP Levels Received: " + hovered.entry.experienceGiven);
            }
            if (!hovered.entry.getOutputs().isEmpty()) {
                RenderUtils.renderText(this.mc, boxX + 4, boxY + 40, "Items Given:");
                for (int i = 0; i < hovered.entry.getOutputs().size(); i++) {
                    RenderUtils.renderItemStackWithCount(this.mc, mc.getRenderItem(),
                            hovered.entry.getOutputs().get(i), boxX + 64 + (i * 18), boxY + 40, true);
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
                if (entry.requiredGamestages.stream().allMatch(x -> x.evaluateFor(this.mc.player))) {
                    Map<ResearchType, Integer> costs = entry.calculateCostForPlayer(this.mc.player, ((CustomButton) _button).shopID);
                    int x = this.width / 8 + buttonX;
                    int y = this.height / 4 + buttonY;
                    drawItemStack(entry.getIcon(), x + 60, y + 30, "");//$" + entry.fireCost);
                    RenderUtils.renderText(this.mc, x + 4, y + 4, entry.displayName, 16777215);
                    RenderUtils.renderText(this.mc, x + 4, y + 16, "F: " + costs.get(FIRE), Reference.FIRE_COLOR);
                    RenderUtils.renderText(this.mc, x + 4, y + 28, "W: " + costs.get(WATER), Reference.WATER_COLOR);
                    RenderUtils.renderText(this.mc, x + 4, y + 40, "A: " + costs.get(AIR), Reference.AIR_COLOR);
                    RenderUtils.renderText(this.mc, x + 4, y + 52, "E: " + costs.get(EARTH), Reference.EARTH_COLOR);
                    RenderUtils.renderText(this.mc, x + 4, y + 66, "XP: " + entry.experienceRequired, Reference.XP_COLOR);
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