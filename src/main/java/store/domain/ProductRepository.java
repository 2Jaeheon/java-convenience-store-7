package store.domain;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    List<Product> inventory = new ArrayList<>();

    public List<Product> findByName(String name) {
        List<Product> products = new ArrayList<>();
        for (Product product : inventory) {
            String productName = product.getName();
            if (productName.equals(name)) {
                products.add(product);
            }
        }

        return products;
    }

    public List<Product> findAll() {
        return List.copyOf(inventory);
    }

    public void save(Product product) {
        inventory.add(product);
    }

    public boolean isExist(String name) {
        for (Product product : inventory) {
            String productName = product.getName();
            if (productName.equals(name)) {
                return true;
            }
        }

        return false;
    }
}
