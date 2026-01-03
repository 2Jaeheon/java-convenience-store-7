package store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;

public class FileLoader {
    private static final String PROMOTIONS_PATH = "src/main/resources/promotions.md";
    private static final String PRODUCTS_PATH = "src/main/resources/products.md";
    private static final String COMMA = ",";

    public Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PROMOTIONS_PATH))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(COMMA);
                String name = data[0];
                int buy = Integer.parseInt(data[1]);
                int get = Integer.parseInt(data[2]);
                LocalDate startDate = LocalDate.parse(data[3]);
                LocalDate endDate = LocalDate.parse(data[4]);

                // 프로모션에 넣어주기
                promotions.put(name, new Promotion(name, buy, get, startDate, endDate));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("[ERROR] 프로모션 파일을 읽을 수 없습니다.");
        }

        return promotions;
    }

    public List<Product> loadProducts(Map<String, Promotion> promotionMap) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_PATH))) {
            String line;
            br.readLine(); // 헤더 건너뛰기

            while ((line = br.readLine()) != null) {
                String[] data = line.split(COMMA);
                String name = data[0];
                int price = Integer.parseInt(data[1]);
                int quantity = Integer.parseInt(data[2]);
                String promotionName = data[3];

                Promotion promotion = null;
                if (!"null".equals(promotionName)) {
                    promotion = promotionMap.get(promotionName);
                }

                products.add(new Product(name, price, quantity, promotion));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("[ERROR] 상품 파일을 읽을 수 없습니다.");
        }

        return fillMissingGeneralProducts(products);
    }

    private List<Product> fillMissingGeneralProducts(List<Product> products) {
        Map<String, List<Product>> grouped = new LinkedHashMap<>();
        for (Product product : products) {
            String name = product.getName();

            // 그룹에 상품이 있는지 체크
            if (!grouped.containsKey(name)) {
                grouped.put(name, new ArrayList<>());
            }

            grouped.get(name).add(product);
        }

        List<Product> result = new ArrayList<>();

        for (List<Product> group : grouped.values()) {
            result.addAll(group);

            // 그룹의 사이즈가 1이면서 프로모션이 존재한다면
            if (group.size() == 1 && group.getFirst().hasPromotion()) {
                // promotion이 null인 객체를 추가
                Product promoProduct = group.getFirst();
                result.add(new Product(promoProduct.getName(), promoProduct.getPrice(), 0, null));
            }
        }

        return result;
    }
}
