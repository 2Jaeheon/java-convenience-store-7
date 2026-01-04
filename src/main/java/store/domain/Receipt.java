package store.domain;

import java.util.List;

public class Receipt {
    private final List<OrderResponse> orderResponses;
    private final int membershipDiscount;

    public Receipt(List<OrderResponse> orderResponses, int membershipDiscount) {
        this.orderResponses = orderResponses;
        this.membershipDiscount = membershipDiscount;
    }

    // 총 구매액
    public int getTotalAmount() {
        int totalAmount = 0;
        for (OrderResponse order : orderResponses) {
            totalAmount += order.getPrice() * order.getQuantity();
        }
        return totalAmount;
    }

    // 총 구매 수량
    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (OrderResponse order : orderResponses) {
            totalQuantity += order.getQuantity();
        }
        return totalQuantity;
    }

    // 행사 할인 금액 (증정 개수 * 가격의 합)
    public int getPromotionDiscount() {
        int promotionDiscount = 0;
        for (OrderResponse order : orderResponses) {
            promotionDiscount += order.getPrice() * order.getPromotionCount();
        }
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return getTotalAmount() - getPromotionDiscount() - getMembershipDiscount();
    }

    public List<OrderResponse> getOrderResponses() {
        return orderResponses;
    }
}