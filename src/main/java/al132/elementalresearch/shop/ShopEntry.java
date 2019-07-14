package al132.elementalresearch.shop;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.capabilities.ResearchCapability.ResearchType;
import al132.elementalresearch.compat.ct.CTEntryBuilder;
import al132.elementalresearch.compat.ct.StageGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.*;

import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;

public class ShopEntry implements Comparable<ShopEntry> {

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
    public final int priority;

    public final int saleQuantity;
    private final List<ItemStack> itemInputs = new ArrayList<>();
    public final boolean consumeItemInputs;
    private final ItemStack icon;

    public ShopEntry(CTEntryBuilder builder) {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.fireCost = builder.fireCost;
        this.waterCost = builder.waterCost;
        this.airCost = builder.airCost;
        this.earthCost = builder.earthCost;
        this.priority = builder.priority;
        this.multiplierPerPurchase = builder.multiplierPerPurchase;
        this.experienceRequired = builder.experienceRequired;
        this.experienceGiven = builder.experienceGiven;
        this.requiredGamestages = builder.requiredGamestages;
        this.givenGamestages = builder.givenGamestages;
        this.saleQuantity = builder.saleQuantity;
        this.commands = builder.commands;
        this.consumeItemInputs = builder.consumeInputs;

        if(builder.itemInputs != null) {
            builder.itemInputs.stream()
                    .map(x -> (ItemStack) x.getInternal())
                    .forEach(itemInputs::add);
        }
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

    public Map<ResearchType, Integer> calculateCostForPlayer(EntityPlayer player, UUID index) {
        ResearchCapability research = ResearchCapability.get(player);
        int purchasesMade = research.shopQuantity.get(index).quantityPurchased;
        HashMap<ResearchType, Integer> output = new HashMap<>();
        for (ResearchType type : ResearchType.values()) {
            double baseCost = (double) this.getElementCost(type);
            for (StageGroup group : this.requiredGamestages) {
                StageGroup.EvaluationResult evalResult = group.evaluateFor(player);
                if (group.evaluateFor(player).result) baseCost *= evalResult.modifier;
            }
            double perPurchaseMultiplier = Math.pow(this.multiplierPerPurchase, purchasesMade);
            output.put(type, (int) Math.round(baseCost * perPurchaseMultiplier));
        }
        return output;
    }

    public ItemStack getIcon() {
        return icon.copy();
    }

    public List<ItemStack> getOutputs() {
        List<ItemStack> result = new ArrayList<>();
        this.outputs.forEach(x -> result.add(x.copy()));
        return result;
    }

    public List<ItemStack> getInputs(){
        List<ItemStack> result = new ArrayList<>();
        this.itemInputs.forEach(x -> result.add(x.copy()));
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

    @Override
    public int compareTo(ShopEntry o) {
        //we want high priority to come first, so we are putting this as the second parameter
        return Integer.compare(o.priority, this.priority);
    }
}