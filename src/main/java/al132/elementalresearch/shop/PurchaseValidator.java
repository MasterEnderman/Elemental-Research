package al132.elementalresearch.shop;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPlayerResearch;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class PurchaseValidator {

    public static void executePurchase(EntityPlayer player, int shopIndex) {
        ResearchCapability research = player.getCapability(ResearchCapability.RESEARCH_CAPABILITY, null);
        if (research != null) {
            ShopEntry entry = ShopRegistry.registry.get(shopIndex);
            if (research.canAfford(player,entry,shopIndex) && player.experienceLevel >= entry.experienceRequired) {
                research.executePurchase(player,shopIndex, entry);
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

    public static void executeCommand(EntityPlayer sender, String _command) {
        String parsedCommand;
        if (_command.charAt(0) == '/') parsedCommand = _command.replaceFirst("/", "");
        else parsedCommand = _command;
        sender.getServer().getCommandManager().executeCommand(sender.getServer(), parsedCommand);
    }
}
