package store.util;

import java.util.ArrayList;
import java.util.List;
import store.model.Order;

public class Parser {
    private static final String COMMA = ",";
    private static final String HYPHEN = "-";

    public List<Order> parseOrders(String input) {
        validateInput(input);

        List<Order> orders = new ArrayList<>();
        String[] parts = input.split(COMMA);

        for (String part : parts) {
            // [콜라-10] -> 콜라-10 (대괄호 제거)
            String content = removeBrackets(part.trim());
            // 콜라-10 -> {콜라, 10} (하이픈 분리)
            orders.add(parseSingleOrder(content));
        }

        return orders;
    }

    private void validateInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 입력값이 비어있습니다.");
        }
    }

    private String removeBrackets(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식입니다. (예: [콜라-10])");
        }
        return input.substring(1, input.length() - 1);
    }

    private Order parseSingleOrder(String content) {
        String[] info = content.split(HYPHEN);
        if (info.length != 2) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식입니다. (예: [콜라-10])");
        }

        try {
            String name = info[0];
            int quantity = Integer.parseInt(info[1]);
            return new Order(name, quantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 수량은 숫자여야 합니다.");
        }
    }
}
