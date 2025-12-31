package store.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import store.domain.Order;

public class Parser {

    private static final String ERROR_INPUT = "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.";
    private static final Pattern ORDER_PATTERN = Pattern.compile("^\\[([^-]+)-([0-9]+)\\]$");

    public static Order parseProducts(String raw) {
        Map<String, Integer> order = new LinkedHashMap<>();

        String[] splitInput = raw.split(",");

        try {
            for (String str : splitInput) {
                if (!str.startsWith("[") || !str.endsWith("]")) {
                    throw new IllegalArgumentException(ERROR_INPUT);
                }

                String substring = str.substring(1, str.length() - 1);
                String[] productInfo = substring.split("-");

                if (productInfo.length != 2) {
                    throw new IllegalArgumentException(ERROR_INPUT);
                }

                String productName = productInfo[0];
                int productCount = Integer.parseInt(productInfo[1]);

                order.put(productName, productCount);
            }

            return new Order(order);
        } catch (Exception e) {
            throw new IllegalArgumentException(ERROR_INPUT);
        }
    }
}
