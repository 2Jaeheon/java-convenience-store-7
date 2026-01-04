package store.domain;

public class OrderRequest {
    private final String name;
    private final int quantity;

    public OrderRequest(String name, int quantity) {
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("[ERROR] 구매 수량은 1개 이상이어야 합니다.");
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
