package al132.elementalresearch.network;

import al132.elementalresearch.capabilities.ResearchCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;


public class PacketPlayerResearch implements IMessage {

    private NBTTagCompound capabilityTag;

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            NBTTagCompound tag = new PacketBuffer(buf).readCompoundTag();
            assert tag != null;
            capabilityTag = (NBTTagCompound) tag.getTag("research");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("research", capabilityTag);
        new PacketBuffer(buf).writeCompoundTag(tag);
    }

    public PacketPlayerResearch() {
    }

    public PacketPlayerResearch(ResearchCapability capability) {
        this.capabilityTag = (NBTTagCompound) capability.serializeNBT();
    }

    public static class Handler implements IMessageHandler<PacketPlayerResearch, IMessage> {

        @Override
        public IMessage onMessage(PacketPlayerResearch message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketPlayerResearch message, MessageContext ctx) {
            EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
            if (player != null) {
                ResearchCapability research = ResearchCapability.get(player);
                if (research != null) {
                    research.deserializeNBT(message.capabilityTag);
                }
            }
        }
    }
}