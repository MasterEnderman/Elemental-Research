package al132.elementalresearch.shop;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPlayerResearch;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

public class PurchaseValidator {

    public static void executePurchase(EntityPlayer player, UUID shopIndex) {
        ResearchCapability research = player.getCapability(ResearchCapability.RESEARCH_CAPABILITY, null);
        if (research != null) {
            ShopEntry entry = ShopRegistry.registry.get(shopIndex);
            if (research.canAfford(player, entry, shopIndex)
                    && player.experienceLevel >= entry.experienceRequired
                    && PurchaseValidator.hasItems(player, entry.getInputs())) {
                if (entry.consumeItemInputs) {
                    consumeItems(player,entry.getInputs());
                }
                research.executePurchase(player, shopIndex, entry);
                player.addExperienceLevel(entry.experienceGiven - entry.experienceRequired);
                entry.getOutputs().forEach(player::addItemStackToInventory);
                entry.givenGamestages.forEach(stage -> GameStageHelper.addStage(player, stage));
                entry.givenGamestages.stream()
                        .filter(x -> !GameStageHelper.hasStage(player, x))
                        .forEach(stage -> player.sendMessage(new TextComponentString("Gamestage [" + stage + "] unlocked")));
                GameStageHelper.syncPlayer(player);
                entry.commands.forEach(sender -> executeCommand(player, sender));
                PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
            }
        }
    }

    public static boolean hasItems(EntityPlayer player, List<ItemStack> targetItems) {
        if (targetItems.isEmpty()) return true;
        for (ItemStack target : targetItems) {
            int playerCount = player.inventory.mainInventory.stream()
                    .filter(x -> x.getItem() == target.getItem() && x.getMetadata() == target.getMetadata())
                    .mapToInt(ItemStack::getCount)
                    .sum();
            if (playerCount < target.getCount()) return false;
        }
        return true;
    }

    public static void consumeItems(EntityPlayer player, List<ItemStack> targetItems) {
        if (targetItems.isEmpty()) return;
        for (ItemStack targetStack : targetItems) {
            int targetCount = targetStack.getCount();
            for (ItemStack playerStack : player.inventory.mainInventory) {
                if (targetCount == 0) break;
                if (ItemStack.areItemsEqual(targetStack, playerStack) && targetStack.getMetadata() == playerStack.getMetadata()) {
                    while (targetCount > 0 && playerStack.getCount() > 0) {
                        targetCount--;
                        playerStack.shrink(1);
                    }
                }
            }
        }
    }

    public static void executeCommand(EntityPlayer sender, String _command) {
        String parsedCommand;
        if (_command.charAt(0) == '/') parsedCommand = _command.replaceFirst("/", "");
        else parsedCommand = _command;
        sender.getServer().getCommandManager().executeCommand(sender.getServer(), parsedCommand);
    }
}
