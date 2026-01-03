package store.view;

import java.util.List;
import store.domain.Product;

public class OutputView {
    public void printInventory(List<Product> all) {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");

        for (Product product : all) {
            String promo = checkValidPromotion(product);
            String stock = checkValidStock(product.getQuantity());
            System.out.printf("- %s %,d원 %s %s\n", product.getName(), product.getPrice(), stock, promo);
        }
    }

    private String checkValidStock(int quantity) {
        if (quantity == 0) {
            return "재고 없음";
        }
        return quantity + "개";
    }

    private String checkValidPromotion(Product product) {
        if (product.hasPromotion()) {
            return product.getPromotion().getName();
        }

        return "";
    }

    public void printOrderMessage() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
