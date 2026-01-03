package store;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import store.model.Product;
import store.model.Promotion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileLoader {
    private static final String PROMOTIONS_PATH = "src/main/resources/promotions.md";
    private static final String PRODUCTS_PATH = "src/main/resources/products.md";
    private static final String COMMA = ",";

    // 프로모션 먼저 로딩(Map으로 저장해서 로딩 편하게)
    public Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();

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

    // 상품 로딩
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

                // null 문자열이거나 맵에 없으면 null 처리
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
        List<Product> result = new ArrayList<>();

        Map<String, List<Product>> grouped = products.stream()
                .collect(Collectors.groupingBy(Product::getName, LinkedHashMap::new, Collectors.toList()));

        for (String name : grouped.keySet()) {
            List<Product> pList = grouped.get(name);
            result.addAll(pList);

            // 검사: 프로모션 상품은 존재하는데, 일반 상품(promotion == null)이 없는가?
            boolean hasPromo = pList.stream().anyMatch(Product::hasPromotion);
            boolean hasGeneral = pList.stream().anyMatch(p -> !p.hasPromotion());

            if (hasPromo && !hasGeneral) {
                // 프로모션 상품의 가격 정보를 가져와서 재고 0짜리 일반 상품 생성
                Product promoProduct = pList.get(0);
                Product zeroStockGeneral = new Product(name, promoProduct.getPrice(), 0, null);
                result.add(zeroStockGeneral);
            }
        }

        return result;
    }
}