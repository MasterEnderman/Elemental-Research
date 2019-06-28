package al132.elementalresearch.shop;

import java.util.HashMap;
import java.util.Map;

public class ShopRegistry {
    private static int ID = 0;
    public static Map<Integer, ShopEntry> registry = new HashMap<>();

    private static int nextID() {
        int result = ID;
        ID++;
        return result;
    }

    public static void register(ShopEntry entry) {
        registry.put(nextID(), entry);
    }
}
