package store.model;

import java.time.LocalDate;

public class Product {
    private final String name;
    private final int price;
    private int quantity;
    private final Promotion promotion;

    public Product(String name, int price, int quantity, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public void reduceQuantity(int count) {
        if (this.quantity < count) {
            throw new IllegalArgumentException("[ERROR] 재고가 부족합니다.");
        }
        this.quantity -= count;
    }

    public boolean hasPromotion() {
        return this.promotion != null;
    }

    public boolean isPromotionApplicable(LocalDate date) {
        return hasPromotion() && promotion.isPromotionValid(date);
    }

    // 빠른 구현을 위한 getters
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
