package store.view;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import store.domain.Inventory;
import store.domain.Product;
import store.domain.Receipt;

public class OutputView {

    private static final DecimalFormat formatter = new DecimalFormat("###,###");

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

    public void printReceipt(Receipt receipt, Inventory inventory) {
        System.out.println("\n==============W 편의점================");
        System.out.printf("%-10s\t%-8s\t%-6s\n", "상품명", "수량", "금액");

        Map<String, Integer> items = receipt.getOrder().getOrder();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();
            int price = inventory.getProducts(name).get(0).getPrice();
            System.out.printf("%-10s\t%-8d\t%-6s\n", name, count, formatter.format((long) price * count));
        }

        if (!receipt.getFreeGifts().isEmpty()) {
            System.out.println("=============증\t정===============");
            for (Map.Entry<String, Integer> entry : receipt.getFreeGifts().entrySet()) {
                System.out.printf("%-10s\t%-8d\n", entry.getKey(), entry.getValue());
            }
        }

        System.out.println("====================================");
        System.out.printf("%-10s\t%-8d\t%-6s\n", "총구매액", receipt.getTotalQuantity(),
                formatter.format(receipt.getTotalAmount()));
        System.out.printf("%-10s\t%-8s\t-%-6s\n", "행사할인", "", formatter.format(receipt.getPromotionDiscount()));
        System.out.printf("%-10s\t%-8s\t-%-6s\n", "멤버십할인", "", formatter.format(receipt.getMembershipDiscount()));
        System.out.printf("%-10s\t%-8s\t %-6s\n", "내실돈", "", formatter.format(receipt.getFinalAmount()));
    }
}
