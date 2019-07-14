package al132.elementalresearch.compat.ct;


import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import com.google.common.collect.Lists;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.elementalresearch.Entry")
@ModOnly("elementalresearch")
@ZenRegister
public class CTEntryBuilder {

    @ZenProperty
    public String displayName;
    @ZenProperty
    public String description = "";
    @ZenProperty
    public int fireCost = 0;
    @ZenProperty
    public int waterCost = 0;
    @ZenProperty
    public int airCost = 0;
    @ZenProperty
    public int earthCost = 0;
    @ZenProperty
    public IItemStack icon;
    @ZenProperty
    public int saleQuantity = 1;
    @ZenProperty
    public int experienceRequired = 0;
    @ZenProperty
    public int experienceGiven = 0;
    @ZenProperty
    public int priority = 0;
    @ZenProperty
    public List<StageGroup> requiredGamestages = new ArrayList<>();
    @ZenProperty
    public List<String> givenGamestages = new ArrayList<>();
    @ZenProperty
    public List<IItemStack> itemInputs = new ArrayList<>();
    @ZenProperty
    public boolean consumeInputs = true;
    @ZenProperty
    public List<IItemStack> itemOutputs = new ArrayList<>();
    @ZenProperty
    public List<String> commands = new ArrayList<>();
    @ZenProperty
    public double multiplierPerPurchase = 1.0;

    @ZenMethod
    public void addInputs(IItemStack[] stacks, boolean consume) {
        this.itemInputs = Lists.newArrayList(stacks);
        this.consumeInputs = consume;
    }

    @ZenMethod
    public void addCommand(String command) {
        this.commands.add(command);
    }

    @ZenMethod
    public void setIcon(IItemStack icon) {
        this.icon = icon;
    }

    @ZenMethod
    public void addRequiredStage(String stage) {
        this.requiredGamestages.add(new StageGroup("AND", stage));
    }

    @ZenMethod
    public void addRequiredStageWithModifier(String stage, double modifier) {
        this.requiredGamestages.add(new StageGroup("AND", modifier, stage));
    }

    @ZenMethod
    public void addRequiredStages(String type, String[] stages) {
        this.requiredGamestages.add(new StageGroup(type, stages));
    }

    @ZenMethod
    public void addRequiredStagesWithModifier(String type, double modifier, String[] stages) {
        this.requiredGamestages.add(new StageGroup(type, modifier, stages));
    }

    @ZenMethod
    public void addOutputs(IItemStack[] stacks) {
        this.itemOutputs.addAll(Lists.newArrayList(stacks));
    }

    @ZenMethod
    public void addGivenStage(String stage) {
        this.givenGamestages.add(stage);
    }

    @ZenMethod
    public void build() {
        ShopRegistry.register(new ShopEntry(this));
    }
}