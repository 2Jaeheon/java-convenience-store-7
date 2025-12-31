package store.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private final Map<String, List<Product>> products = new LinkedHashMap<>();

    public Inventory(List<Product> productList) {
        for (Product product : productList) {
            String name = product.getName();
            if (!products.containsKey(name)) {
                products.put(name, new ArrayList<>());
            }

            products.get(name).add(product);
        }

        fillMissingGeneralStock();
    }

    private void fillMissingGeneralStock() {
        for (String name : products.keySet()) {
            List<Product> productList = products.get(name);

            boolean hasGeneralStock = false;
            for (Product product : productList) {
                if (!product.hasPromotion()) {
                    hasGeneralStock = true;
                    break;
                }
            }

            if (!hasGeneralStock) {
                Product promoProduct = productList.get(0);
                Product zeroStockProduct = new Product(name, promoProduct.getPrice(), 0, null);
                productList.add(zeroStockProduct);
            }
        }
    }

    public List<Product> getProducts(String name) {
        validateExistence(name);
        return products.get(name);
    }

    private void validateExistence(String name) {
        if (!products.containsKey(name)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }

    public Map<String, List<Product>> getProducts() {
        return products;
    }

    public void validateOrder(Order order) {
        List<String> names = order.getNames();
        for (String name : names) {
            if (!products.containsKey(name)) {
                throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
            }

            int requestedQuantity = order.getQuantity(name);

            int totalStock = 0;
            List<Product> productList = products.get(name);
            for (Product product : productList) {
                totalStock += product.getQuantity();
            }

            if (requestedQuantity > totalStock) {
                throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
        }
    }
}
