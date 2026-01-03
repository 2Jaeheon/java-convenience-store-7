package store.domain;

public class Order {
    private final String name;
    private int quantity;

    public Order(String name, int quantity) {
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("[ERROR] 구매 수량은 1개 이상이어야 합니다.");
        }
    }

    public void decrease(int decreaseQuantity) {
        quantity -= decreaseQuantity;
    }

    public void increase(int increaseQuantity) {
        quantity += increaseQuantity;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
