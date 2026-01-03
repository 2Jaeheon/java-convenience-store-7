package store.model;

import java.util.List;

public class Receipt {
    private final List<Order> orders;
    private final long membershipDiscount;

    public Receipt(List<Order> orders, long membershipDiscount) {
        this.orders = orders;
        this.membershipDiscount = membershipDiscount;
    }

    // 1. 총 구매액 (할인 전)
    public long getTotalAmount() {
        long total = 0;
        for (Order order : orders) {
            total += (long) order.getPrice() * order.getQuantity();
        }
        return total;
    }

    // 2. 행사 할인 금액 (증정 개수 * 가격)
    public long getPromotionDiscount() {
        long discount = 0;
        for (Order order : orders) {
            discount += (long) order.getPrice() * order.getPromotionCount();
        }
        return discount;
    }

    // 3. 멤버십 할인 금액
    public long getMembershipDiscount() {
        return membershipDiscount;
    }

    // 4. 내실 돈 (총구매액 - 행사할인 - 멤버십할인)
    public long getFinalPrice() {
        return getTotalAmount() - getPromotionDiscount() - getMembershipDiscount();
    }

    // 5. 총 구매 수량
    public int getTotalQuantity() {
        return orders.stream().mapToInt(Order::getQuantity).sum();
    }

    public List<Order> getOrders() {
        return orders;
    }
}