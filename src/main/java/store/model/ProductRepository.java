package store.model;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public void save(Product product) {
        products.add(product);
    }

    public List<Product> findAll() {
        return List.copyOf(products);
    }

    // 콜라를 검색하면 콜라(일반), 콜라(프로모션) 반환함
    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().equals(name)) {
                result.add(product);
            }
        }

        return result;
    }

    // 존재하는지 확인
    public boolean existByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
