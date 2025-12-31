package store.view;

import java.util.List;
import java.util.Map;
import store.domain.Inventory;
import store.domain.Product;

public class OutputView {
    public void printMessage(String raw) {
        System.out.println(raw);
    }

    public void printInventory(Inventory inventory) {
        Map<String, List<Product>> productsMap = inventory.getProducts();

        for (List<Product> products : productsMap.values()) {
            for (Product product : products) {
                String quantityStr = product.getQuantity() + "개";
                if (product.getQuantity() == 0) {
                    quantityStr = "재고 없음";
                }

                String promotionStr = "";
                if (product.hasPromotion()) {
                    promotionStr = " " + product.getPromotion().getName();
                }

                System.out.printf("- %s %,d원 %s%s\n",
                        product.getName(),
                        product.getPrice(),
                        quantityStr,
                        promotionStr);
            }
        }
    }

    public void printFreeGift(Map<String, Integer> freeGift) {
        for (Map.Entry<String, Integer> entry : freeGift.entrySet()) {

            System.out.println(
                    "현재 " + entry.getKey() + "은(는) " + entry.getValue() + "개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
        }
    }
}
