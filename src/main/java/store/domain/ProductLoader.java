package store.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductLoader {
    private static final String PROMOTIONS_PATH = "src/main/resources/promotions.md";
    private static final String PRODUCTS_PATH = "src/main/resources/products.md";

    public Inventory load() {
        // 프로모션 가져오기
        Map<String, Promotion> promotions = loadPromotions();

        // 상품 읽어서 프로모션 연결
        List<Product> products = loadProductions(promotions);

        return new Inventory(products);
    }

    private List<Product> loadProductions(Map<String, Promotion> promotions) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_PATH))) {
            String line;
            br.readLine();

            splitAndGet(promotions, br, products);
        } catch (IOException e) {
            throw new IllegalArgumentException("[ERROR] 상품 파일을 읽을 수 없습니다.");
        }

        return products;
    }

    private void splitAndGet(Map<String, Promotion> promotions, BufferedReader br, List<Product> products)
            throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int quantity = Integer.parseInt(parts[2]);
            String promotionName = parts[3];

            // "null" 문자열이면 진짜 null로, 아니면 Map에서 찾아오기
            Promotion promotion = null;
            if (!"null".equals(promotionName)) {
                promotion = promotions.get(promotionName);
            }
            products.add(new Product(name, price, quantity, promotion));
        }
    }

    private Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PROMOTIONS_PATH))) {
            String line;
            br.readLine(); // 헤더 건너뛰기

            splitAndGet(br, promotions);
        } catch (IOException e) {
            throw new IllegalArgumentException("[ERROR] 프로모션 파일을 읽을 수 없습니다.");
        }

        return promotions;
    }

    private void splitAndGet(BufferedReader br, Map<String, Promotion> promotions) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            String name = parts[0];
            int buy = Integer.parseInt(parts[1]);
            int get = Integer.parseInt(parts[2]);
            String start = parts[3];
            String end = parts[4];

            promotions.put(name, new Promotion(name, buy, get, start, end));
        }
    }
}
