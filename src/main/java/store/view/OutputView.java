package store.view;

import java.text.DecimalFormat;
import java.util.List;
import store.domain.OrderResponse;
import store.domain.Product;
import store.domain.Receipt;

public class OutputView {
    private static final String NEW_LINE = System.lineSeparator();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");

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

    public void printReceipt(Receipt receipt) {
        StringBuilder sb = new StringBuilder();

        sb.append(NEW_LINE).append("==============W 편의점================").append(NEW_LINE);
        sb.append(String.format("%-19s\t%-10s\t%-10s", "상품명", "수량", "금액")).append(NEW_LINE);

        // 1. 구매 내역 출력
        for (OrderResponse order : receipt.getOrderResponses()) {
            sb.append(String.format("%-19s\t%-10d\t%-10s", order.getName(), order.getQuantity(),
                    MONEY_FORMAT.format(order.getPrice() * order.getQuantity()))).append(NEW_LINE);
        }

        // 2. 증정 내역 출력 (증정 개수가 있는 것만)
        sb.append("=============증    정===============").append(NEW_LINE);
        for (OrderResponse order : receipt.getOrderResponses()) {
            if (order.getPromotionCount() > 0) {
                sb.append(String.format("%-19s\t%-10d", order.getName(), order.getPromotionCount())).append(NEW_LINE);
            }
        }

        // 3. 금액 합계 출력
        sb.append("====================================").append(NEW_LINE);
        sb.append(String.format("%-19s\t%-10d\t%-10s", "총구매액", receipt.getTotalQuantity(),
                MONEY_FORMAT.format(receipt.getTotalAmount()))).append(NEW_LINE);

        sb.append(
                        String.format("%-19s\t%-10s\t-%-10s", "행사할인", "", MONEY_FORMAT.format(receipt.getPromotionDiscount())))
                .append(NEW_LINE);

        sb.append(String.format("%-19s\t%-10s\t-%-10s", "멤버십할인", "",
                MONEY_FORMAT.format(receipt.getMembershipDiscount()))).append(NEW_LINE);

        sb.append(String.format("%-19s\t%-10s\t %-10s", "내실돈", "", MONEY_FORMAT.format(receipt.getFinalAmount())))
                .append(NEW_LINE);

        System.out.println(sb);
    }
}
