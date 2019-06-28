package al132.elementalresearch.event;

import al132.elementalresearch.Reference;
import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.network.PacketHandler;
import al132.elementalresearch.network.PacketPlayerResearch;
import al132.elementalresearch.shop.ShopEntry;
import al132.elementalresearch.shop.ShopRegistry;
import al132.elementalresearch.util.ShopQuantityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import java.util.Map;

import static al132.elementalresearch.capabilities.ResearchCapability.RESEARCH_CAPABILITY;
import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.FIRE;
import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.WATER;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventHandler {


    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayer) {
            if (!e.getObject().hasCapability(RESEARCH_CAPABILITY, null)) {
                e.addCapability(new ResourceLocation(Reference.MODID, "research"), new ResearchCapability());
            }
        }
    }


    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone e) {
        if (e.isWasDeath()) {
            EntityPlayer oldPlayer = e.getOriginal();
            EntityPlayer newPlayer = e.getEntityPlayer();
            if (oldPlayer.hasCapability(RESEARCH_CAPABILITY, null)) {
                ResearchCapability oldResearch = ResearchCapability.get(oldPlayer);
                ResearchCapability newResearch = ResearchCapability.get(newPlayer);
                newResearch.deserializeNBT(oldResearch.serializeNBT());
                //newResearch.player = newPlayer;
                PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(newResearch), (EntityPlayerMP) e.getEntityPlayer());
            }
        }
    }

    @SubscribeEvent
    public void playerRespawnEvent(PlayerRespawnEvent e) {
        ResearchCapability research = ResearchCapability.get(e.player);
        //research.player = e.player;
        PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) e.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent e) {
        ResearchCapability research = ResearchCapability.get(e.player);
        //research.player = e.player;
        for (Map.Entry<Integer, ShopEntry> mapEntry : ShopRegistry.registry.entrySet()) {
            if (!research.shopQuantity.containsKey(mapEntry.getKey())) {
                research.shopQuantity.put(mapEntry.getKey(), new ShopQuantityData(mapEntry.getValue().saleQuantity, 0));
            }
        }
        PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) e.player);
    }

    @SubscribeEvent
    public void jumpEvent(LivingEvent.LivingJumpEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e.getEntity();
            if (player != null && player.hasCapability(RESEARCH_CAPABILITY, null)) {
                ResearchCapability research = ResearchCapability.get(player);
                research.jump();//(ResearchCapability.ResearchType.AIR, 5);
                //PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
            }
        }
    }

    @SubscribeEvent
    public void ev(BlockEvent.HarvestDropsEvent e) {
        EntityPlayer harvester = e.getHarvester();
        if (harvester != null && harvester.hasCapability(RESEARCH_CAPABILITY, null)) {
            ResearchCapability research = ResearchCapability.get(e.getHarvester());
            research.increase(ResearchCapability.ResearchType.EARTH, 1);
            PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) e.getHarvester());
        }
    }

    @SubscribeEvent
    public void x(LivingDamageEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e.getEntityLiving();
            if (player != null && player.hasCapability(RESEARCH_CAPABILITY, null)) {
                ResearchCapability research = ResearchCapability.get(player);
                if (e.getSource() == DamageSource.ON_FIRE || e.getSource() == DamageSource.IN_FIRE) {
                    research.increase(FIRE, 1);
                    PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
                } else if (e.getSource() == DamageSource.DROWN) {
                    research.increase(WATER, 1);
                    PacketHandler.INSTANCE.sendTo(new PacketPlayerResearch(research), (EntityPlayerMP) player);
                }
            }
        }
    }
}