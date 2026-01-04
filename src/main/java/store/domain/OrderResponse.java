package store.domain;

public class OrderResponse {
    private final String name;
    private final int price;
    private final int quantity;
    private final int promotionCount;

    public OrderResponse(String name, int price, int quantity, int promotionCount) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotionCount = promotionCount;
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

    public int getPromotionCount() {
        return promotionCount;
    }
}
