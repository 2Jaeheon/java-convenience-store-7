package store.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Order {
    Map<String, Integer> items = new LinkedHashMap<>();

    public Order(Map<String, Integer> items) {
        this.items = items;
    }

    public List<String> getNames() {
        return new ArrayList<>(items.keySet());
    }

    public List<Integer> getValues() {
        return new ArrayList<>(items.values());
    }

    public Map<String, Integer> getOrder() {
        return Map.copyOf(items);
    }

    public int getQuantity(String name) {
        return items.get(name);
    }

    public void addQuantity(String name, int amount) {
        items.put(name, items.getOrDefault(name, 0) + amount);
    }

    public void decreaseQuantity(String name, int amount) {
        int currentQuantity = items.getOrDefault(name, 0);
        int newQuantity = currentQuantity - amount;

        if (newQuantity <= 0) {
            items.remove(name);
            return;
        }
        items.put(name, newQuantity);
    }
}
