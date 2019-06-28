package al132.elementalresearch.network;

import al132.elementalresearch.shop.PurchaseValidator;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPurchase implements IMessage {
    private int index;

    @Override
    public void fromBytes(ByteBuf buf) {
        index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
    }

    public PacketPurchase() {
    }

    public PacketPurchase(int index) {
        this.index = index;
    }

    public static class Handler implements IMessageHandler<PacketPurchase, IMessage> {
        @Override
        public IMessage onMessage(PacketPurchase message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketPurchase message, MessageContext ctx) {
            PurchaseValidator.executePurchase(ctx.getServerHandler().player, message.index);
        }
    }
}