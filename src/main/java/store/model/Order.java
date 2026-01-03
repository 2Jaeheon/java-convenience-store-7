package store.model;

public class Order {
    private final String name;
    private int quantity;
    private int price;
    private int promotionCount;

    public Order(String name, int quantity) {
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
        this.promotionCount = 0;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("[ERROR] 구매 수량은 1개 이상이어야 합니다.");
        }
    }

    public void addQuantity(int count) {
        this.quantity += count;
    }

    public void decreaseQuantity(int count) {
        if (this.quantity < count) {
            this.quantity = 0;
            return;
        }
        this.quantity -= count;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPromotionCount(int count) {
        this.promotionCount = count;
    }

    public int getPrice() {
        return price;
    }

    public int getPromotionCount() {
        return promotionCount;
    }
}
