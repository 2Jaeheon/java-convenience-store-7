package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReceiptCalculator {
    private final Inventory inventory;
    private final MembershipPolicy membershipPolicy;

    public ReceiptCalculator(Inventory inventory) {
        this.inventory = inventory;
        this.membershipPolicy = new MembershipPolicy();
    }

    public Receipt calculate(Order order, boolean applyMembership) {
        Map<String, Integer> freeGifts = new LinkedHashMap<>();
        int totalQuantity = 0;
        int totalAmount = 0;
        int promotionDiscount = 0;
        int nonPromoAmount = 0;

        LocalDateTime now = DateTimes.now();

        for (String name : order.getNames()) {
            int quantity = order.getQuantity(name);
            List<Product> products = inventory.getProducts(name);
            int price = products.get(0).getPrice();

            totalQuantity += quantity;
            totalAmount += (quantity * price);

            PromotionResult result = calculateItemPromotion(name, quantity, products, now);

            if (result.freeCount > 0) {
                freeGifts.put(name, result.freeCount);
                promotionDiscount += (result.freeCount * price);
            }

            nonPromoAmount += result.nonPromoQuantity * price;
        }

        int membershipDiscount = 0;
        if (applyMembership) {
            membershipDiscount = membershipPolicy.calculateDiscount(nonPromoAmount);
        }

        return new Receipt(order, freeGifts, totalQuantity, totalAmount, promotionDiscount, membershipDiscount);
    }

    private PromotionResult calculateItemPromotion(String name, int quantity, List<Product> products,
                                                   LocalDateTime now) {
        Product promoProduct = null;
        for (Product p : products) {
            if (p.hasPromotion() && p.getPromotion().isActive(now.toLocalDate())) {
                promoProduct = p;
                break;
            }
        }

        if (promoProduct == null) {
            return new PromotionResult(0, quantity);
        }

        int promoStock = promoProduct.getQuantity();
        Promotion promotion = promoProduct.getPromotion();
        int setUnit = promotion.getBuy() + promotion.getGet();

        int maxSets = promoStock / setUnit;
        int requestedSets = quantity / setUnit;
        int appliedSets = Math.min(maxSets, requestedSets);

        int promoAppliedQuantity = appliedSets * setUnit;

        int freeCount = appliedSets * promotion.getGet();
        int nonPromoQuantity = quantity - promoAppliedQuantity;

        return new PromotionResult(freeCount, nonPromoQuantity);
    }

    private static class PromotionResult {
        int freeCount;
        int nonPromoQuantity;

        public PromotionResult(int freeCount, int nonPromoQuantity) {
            this.freeCount = freeCount;
            this.nonPromoQuantity = nonPromoQuantity;
        }
    }
}