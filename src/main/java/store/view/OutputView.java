package store.view;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import store.model.Order;
import store.model.Product;
import store.model.Receipt;

public class OutputView {

    public void printProducts(List<Product> products) {
        System.out.println("\n안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");

        for (Product product : products) {
            String price = NumberFormat.getNumberInstance(Locale.KOREA).format(product.getPrice());
            String stock = product.getQuantity() == 0 ? "재고 없음" : product.getQuantity() + "개";
            String promo = product.hasPromotion() ? " " + product.getPromotion().getName() : "";

            System.out.println("- " + product.getName() + " " + price + "원 " + stock + promo);
        }
        System.out.println();
    }

    public void printError(String message) {
        System.out.println(message);
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("\n==============W 편의점================");
        System.out.println("상품명\t\t수량\t금액");

        for (Order order : receipt.getOrders()) {
            if (order.getQuantity() == 0) {
                continue;
            }

            System.out.printf("%s\t\t%d\t%,d%n",
                    order.getName(),
                    order.getQuantity(),
                    order.getQuantity() * order.getPrice());
        }

        System.out.println("=============증\t정===============");
        for (Order order : receipt.getOrders()) {
            if (order.getPromotionCount() > 0) {
                System.out.printf("%s\t\t%d%n", order.getName(), order.getPromotionCount());
            }
        }

        System.out.println("====================================");
        System.out.printf("총구매액\t\t%d\t%,d%n", receipt.getTotalQuantity(), receipt.getTotalAmount());
        System.out.printf("행사할인\t\t\t-%,d%n", receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t-%,d%n", receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t %,d%n", receipt.getFinalPrice());
    }
}
