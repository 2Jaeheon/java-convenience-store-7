package store.domain;

public class MembershipPolicy {
    private static final int MAX_DISCOUNT = 8000;
    private static final double DISCOUNT_RATE = 0.3;

    public int calculateDiscount(int nonPromoAmount) {
        int discount = (int) (nonPromoAmount * DISCOUNT_RATE);
        return Math.min(discount, MAX_DISCOUNT);
    }
}