package store.domain;

import java.time.LocalDate;

public class Product {
    // name,price,quantity,promotion
    private String name;
    private int price;
    private int quantity;
    private Promotion promotion;

    public Product(String name, int price, int quantity, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

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

    public boolean hasPromotion() {
        if (promotion == null) {
            return false;
        }

        return true;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }
}
