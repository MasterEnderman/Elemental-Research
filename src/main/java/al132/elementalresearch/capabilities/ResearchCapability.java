package al132.elementalresearch.capabilities;

import al132.elementalresearch.ConfigHandler;
import al132.elementalresearch.data.ShopQuantityData;
import al132.elementalresearch.shop.ShopEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static al132.elementalresearch.capabilities.ResearchCapability.ResearchType.*;

public class ResearchCapability implements ICapabilitySerializable<NBTBase> {

    public static ResearchCapability get(EntityPlayer player) {
        return player.getCapability(ResearchCapability.RESEARCH_CAPABILITY, null);
    }

    public enum ResearchType {
        FIRE,
        WATER,
        AIR,
        EARTH;
    }

    public static ResearchType parseType(String _type) {
        String type = _type.toLowerCase();
        if (type.equals("fire")) return FIRE;
        else if (type.equals("water")) return WATER;
        else if (type.equals("air")) return AIR;
        else if (type.equals("earth")) return EARTH;
        else return null;
    }

    @CapabilityInject(ResearchCapability.class)
    public static Capability<ResearchCapability> RESEARCH_CAPABILITY;

    //public EntityPlayer player;
    private int fire = 0;
    private int water = 0;
    private int air = 0;
    private int earth = 0;
    private int jumpCounter = 0;
    private boolean fireVisible = false;
    private boolean waterVisible = false;
    private boolean airVisible = false;
    private boolean earthVisible = false;
    //LEFT: QuantityRemaining, RIGHT: AmountPurchased
    public HashMap<UUID, ShopQuantityData> shopQuantity = new HashMap<>();

    public ResearchCapability() {
    }


    public boolean canAfford(int fire, int water, int air, int earth) {
        return this.fire >= fire
                && this.water >= water
                && this.air >= air
                & this.earth >= earth;
    }

    public boolean canAfford(EntityPlayer player, ShopEntry entry, UUID index) {
        Map<ResearchType, Integer> prices = entry.calculateCostForPlayer(player, index);
        return canAfford(prices.get(FIRE), prices.get(WATER), prices.get(AIR), prices.get(EARTH));
    }

    public void executePurchase(EntityPlayer player, UUID key, ShopEntry entry) {
        int amountRemaining = shopQuantity.get(key).quantityRemaining;
        int amountPurchased = shopQuantity.get(key).quantityPurchased;

        Map<ResearchType, Integer> prices = entry.calculateCostForPlayer(player, key);
        this.decrease(FIRE, prices.get(FIRE));
        this.decrease(WATER, prices.get(WATER));
        this.decrease(AIR, prices.get(AIR));
        this.decrease(EARTH, prices.get(EARTH));

        if (shopQuantity.get(key).quantityRemaining > 0) amountRemaining--;
        shopQuantity.put(key, new ShopQuantityData(amountRemaining, amountPurchased + 1));
    }

    public int getValue(ResearchType type) {
        if (type == FIRE) return fire;
        else if (type == WATER) return water;
        else if (type == AIR) return air;
        else return earth;
    }

    public void setValue(ResearchType type, int newValue) {
        if (type == FIRE) fire = newValue;
        else if (type == WATER) water = newValue;
        else if (type == AIR) air = newValue;
        else earth = newValue;

        if (getValue(type) > 0 && !isTypeVisible(type)) makeTypeVisible(type);
    }

    public boolean isTypeVisible(ResearchType type) {
        if (type == FIRE) return fireVisible;
        else if (type == WATER) return waterVisible;
        else if (type == AIR) return airVisible;
        else return earthVisible;
    }

    public void makeTypeVisible(ResearchType type) {
        if (type == FIRE) fireVisible = true;
        else if (type == WATER) waterVisible = true;
        else if (type == AIR) airVisible = true;
        else earthVisible = true;
    }

    public void jump() {
        jumpCounter++;
        if (jumpCounter >= ConfigHandler.general.jumpsPerAirPoint) {
            jumpCounter = 0;
            increase(AIR, 1);
        }
    }

    public void increase(ResearchType type, int amount) {
        setValue(type, getValue(type) + amount);
    }

    public void decrease(ResearchType type, int amount) {
        setValue(type, getValue(type) - amount);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == RESEARCH_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == RESEARCH_CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTBase serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("fire", fire);
        tag.setInteger("water", water);
        tag.setInteger("air", air);
        tag.setInteger("earth", earth);
        tag.setInteger("jumpCounter", jumpCounter);
        tag.setBoolean("fireVisible", fireVisible);
        tag.setBoolean("waterVisible", waterVisible);
        tag.setBoolean("airVisible", airVisible);
        tag.setBoolean("earthVisible", earthVisible);
        HashMap<UUID, Pair<Integer, Integer>> shopQuantityNBT = new HashMap<>();
        shopQuantity.forEach((key, value) ->
                shopQuantityNBT.put(key, new MutablePair(value.quantityRemaining, value.quantityPurchased)));
        tag.setByteArray("shopQuantity", SerializationUtils.serialize(shopQuantityNBT));
        // tag.setByteArray("player", SerializationUtils.serialize(player.getUniqueID()));
        return tag;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        fire = tag.getInteger("fire");
        water = tag.getInteger("water");
        air = tag.getInteger("air");
        earth = tag.getInteger("earth");
        jumpCounter = tag.getInteger("jumpCounter");
        fireVisible = tag.getBoolean("fireVisible");
        waterVisible = tag.getBoolean("waterVisible");
        airVisible = tag.getBoolean("airVisible");
        earthVisible = tag.getBoolean("earthVisible");
        if (tag.hasKey("shopQuantity")) {
            HashMap<Object, Pair<Integer, Integer>> rawData = new HashMap<>();
            rawData = SerializationUtils.deserialize(tag.getByteArray("shopQuantity"));
            Optional<Object> firstKey = rawData.keySet().stream().findFirst();
            if (firstKey.isPresent()) {
                if (firstKey.get() instanceof Integer) { //legacy compat
                    rawData.forEach((key, value) -> shopQuantity.put(UUID.randomUUID(),
                            new ShopQuantityData(value.getLeft(), value.getRight())));
                } else if (firstKey.get() instanceof UUID) {
                    rawData.forEach((key, value) -> shopQuantity.put((UUID) key,
                            new ShopQuantityData(value.getLeft(), value.getRight())));
                }
            }

        }
        //UUID uuid = SerializationUtils.deserialize(tag.getByteArray("player"));
        //player = (EntityPlayer) Minecraft.getMinecraft().player.getEntityWorld().getMinecraftServer().getEntityFromUuid(uuid);
    }


    public static class Storage implements Capability.IStorage<ResearchCapability> {
        @Override
        public NBTBase writeNBT(Capability<ResearchCapability> capability, ResearchCapability instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ResearchCapability> capability, ResearchCapability instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT(nbt);
        }
    }
}