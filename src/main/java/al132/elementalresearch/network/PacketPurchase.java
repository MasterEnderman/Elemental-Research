package al132.elementalresearch.network;

import al132.elementalresearch.shop.PurchaseValidator;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketPurchase implements IMessage {
    private long mostSigBits;
    private long leastSigBits;

    @Override
    public void fromBytes(ByteBuf buf) {
        mostSigBits = buf.readLong();
        leastSigBits = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(mostSigBits);
        buf.writeLong(leastSigBits);
    }

    public PacketPurchase() {
    }

    public PacketPurchase(UUID index) {
        this.mostSigBits = index.getMostSignificantBits();
        this.leastSigBits = index.getLeastSignificantBits();
    }

    public static class Handler implements IMessageHandler<PacketPurchase, IMessage> {
        @Override
        public IMessage onMessage(PacketPurchase message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketPurchase message, MessageContext ctx) {
            PurchaseValidator.executePurchase(ctx.getServerHandler().player, new UUID(message.mostSigBits, message.leastSigBits));
        }
    }
}