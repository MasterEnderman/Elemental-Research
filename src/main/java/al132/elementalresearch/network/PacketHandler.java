package al132.elementalresearch.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int packetID = 0;
    public static SimpleNetworkWrapper INSTANCE = null;


    public PacketHandler() {
    }

    public static int nextID() {
        return packetID++;
    }


    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(PacketPlayerResearch.Handler.class, PacketPlayerResearch.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketPurchase.Handler.class, PacketPurchase.class, nextID(), Side.SERVER);
    }
}
