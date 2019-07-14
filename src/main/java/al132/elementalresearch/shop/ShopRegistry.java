package al132.elementalresearch.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopRegistry {
    //private static int ID = 0;
    //public static Map<Integer, ShopEntry> registry = new HashMap<>();
    public static Map<UUID, ShopEntry> registry = new HashMap<>();

    /*
    private static int nextID() {
        int result = ID;
        ID++;
        return result;
    }*/

    public static void register(ShopEntry entry) {
        registry.put(UUID.randomUUID(), entry);
    }
}
