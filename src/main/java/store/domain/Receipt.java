package store.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class Receipt {
    private final Order order;
    private final Map<String, Integer> freeGifts;
    private final int totalQuantity;
    private final int totalAmount;
    private final int promotionDiscount;
    private final int membershipDiscount;
    private final int finalAmount;

    public Receipt(Order order, Map<String, Integer> freeGifts,
                   int totalQuantity, int totalAmount,
                   int promotionDiscount, int membershipDiscount) {
        this.order = order;
        this.freeGifts = freeGifts;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = membershipDiscount;
        this.finalAmount = totalAmount - promotionDiscount - membershipDiscount;
    }

    public Order getOrder() {
        return order;
    }

    public Map<String, Integer> getFreeGifts() {
        return freeGifts;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
}