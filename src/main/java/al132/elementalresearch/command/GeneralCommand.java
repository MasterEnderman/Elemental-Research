package al132.elementalresearch.command;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.data.ShopQuantityData;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPlayerResearch;
import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class GeneralCommand extends CommandBase {
    @Override
    public String getName() {
        return "elem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "elem |resetshop|";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equals("set") || args[0].equals("add") || args[0].equals("subtract")) {
            Entity _entity = CommandBase.getEntity(server, sender, args[1]);
            if (_entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) _entity;
                ResearchCapability research = ResearchCapability.get(player);
                String elementArg = args[2].toLowerCase();
                int amount = Integer.parseInt(args[3]);
                ResearchCapability.ResearchType type = ResearchCapability.parseType(elementArg);
                if (type != null) {
                    if (args[0].equals("set")) research.setValue(type, amount);
                    else if (args[0].equals("add")) research.increase(type, amount);
                    else if (args[0].equals("subtract")) research.decrease(type, amount);
                    else return;
                    PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
                    notifyCommandListener(sender, this, "Modified points for player");
                    return;
                }
            }
        }
        if (args[0].equals("resetshop")) {
            Entity _entity;
            if (args.length == 1) _entity = sender.getCommandSenderEntity();
            else _entity = CommandBase.getEntity(server, sender, args[1]);
            if (_entity instanceof EntityPlayer) {
                //EntityPlayer player = (EntityPlayer) _entity;
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ResearchCapability research = ResearchCapability.get(player);
                research.shopQuantity = new HashMap<>();
                for (Map.Entry<UUID, ShopEntry> mapEntry : ShopRegistry.registry.entrySet()) {
                    research.shopQuantity.put(mapEntry.getKey(), new ShopQuantityData(mapEntry.getValue().saleQuantity, 0));
                }
                PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
                notifyCommandListener(sender, this, "Reset shop for sender");
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> output = new ArrayList<>();
        output.add("add");
        output.add("subtract");
        output.add("set");
        output.add("resetshop");
        return output;
    }
}
