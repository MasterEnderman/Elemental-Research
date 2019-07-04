package al132.elementalresearch.shop;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.capabilities.ResearchCapability.ResearchType;
import al132.elementalresearch.compat.ct.CTEntryBuilder;
import al132.elementalresearch.compat.ct.StageGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;

public class ShopEntry {

    public final String displayName;
    private final int fireCost;
    private final int waterCost;
    private final int airCost;
    private final int earthCost;
    public final int experienceRequired;
    public final int experienceGiven;
    public final List<StageGroup> requiredGamestages;
    public final List<String> givenGamestages;
    private final List<ItemStack> outputs = new ArrayList<>();
    public final List<String> commands;
    private double multiplierPerPurchase;
    public String description;

    public final int saleQuantity;
    //public final ItemStack itemCost;
    private final ItemStack icon;

    public ShopEntry(CTEntryBuilder builder) {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.fireCost = builder.fireCost;
        this.waterCost = builder.waterCost;
        this.airCost = builder.airCost;
        this.earthCost = builder.earthCost;
        this.multiplierPerPurchase = builder.multiplierPerPurchase;
        this.experienceRequired = builder.experienceRequired;
        this.experienceGiven = builder.experienceGiven;
        this.requiredGamestages = builder.requiredGamestages;
        this.givenGamestages = builder.givenGamestages;
        this.saleQuantity = builder.saleQuantity;
        this.commands = builder.commands;
        if (builder.itemOutputs != null) {
            builder.itemOutputs.stream()
                    .map(x -> (ItemStack) x.getInternal())
                    .forEach(outputs::add);
        }
        if (builder.icon != null && !builder.icon.isEmpty()) {
            this.icon = (ItemStack) builder.icon.getInternal();
        } else this.icon = ItemStack.EMPTY;
    }

    private int getElementCost(ResearchType type) {
        if (type == FIRE) return this.fireCost;
        else if (type == WATER) return this.waterCost;
        else if (type == AIR) return this.airCost;
        else return this.earthCost;
    }

    public Map<ResearchType, Integer> calculateCostForPlayer(EntityPlayer player, int index) {
        ResearchCapability research = ResearchCapability.get(player);
        int purchasesMade = research.shopQuantity.get(index).quantityPurchased;
        HashMap<ResearchType, Integer> result = new HashMap<>();
        for (ResearchType type : ResearchType.values()) {
            double baseCost = (double) this.getElementCost(type);
            for (StageGroup group : this.requiredGamestages) {
                if (group.evaluateFor(player)) baseCost *= group.costModifier;
            }
            double perPurchaseMultiplier = Math.pow(this.multiplierPerPurchase, purchasesMade);
            result.put(type, (int) Math.round(baseCost * perPurchaseMultiplier));
        }
        return result;
    }

    public ItemStack getIcon() {
        return icon.copy();
    }

    public List<ItemStack> getOutputs() {
        List<ItemStack> result = new ArrayList<>();
        this.outputs.forEach(x -> result.add(x.copy()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Entry: ");
        builder.append("\tdisplayName: ");
        builder.append(displayName);
        builder.append("\tfireCost: ");
        builder.append(fireCost);
        builder.append("\twaterCost: ");
        builder.append(waterCost);
        builder.append("\tairCost: ");
        builder.append(airCost);
        builder.append("\tearthCost: ");
        builder.append(earthCost);
        builder.append("\trequiredStage: ");
        builder.append(requiredGamestages);
        builder.append("\tawardedStages: ");
        builder.append(givenGamestages);
        builder.append("\toutputs: ");
        builder.append(getOutputs());
        return builder.toString();
    }
}