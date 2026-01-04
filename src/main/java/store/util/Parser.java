package store.util;

import java.util.ArrayList;
import java.util.List;
import store.domain.OrderRequest;

public class Parser {
    public List<OrderRequest> parseOrders(String raw) {
        validateInput(raw);
        List<OrderRequest> orderRequests = new ArrayList<>();

        String[] parts = raw.split(",");

        for (String part : parts) {
            String content = removeBrackets(part);
            OrderRequest orderRequest = parseSingleOrder(content);
            orderRequests.add(orderRequest);
        }

        return orderRequests;
    }

    private OrderRequest parseSingleOrder(String input) {
        String[] parts = input.split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        String name;
        int quantity;

        try {
            name = parts[0];
            quantity = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        return new OrderRequest(name, quantity);
    }

    private String removeBrackets(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        return input.substring(1, input.length() - 1);
    }

    private void validateInput(String raw) {
        if (raw.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }

}
