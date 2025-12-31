package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PromotionVerifier {

    public Map<String, Integer> verifyFreeGift(Order order, Inventory inventory) {
        Map<String, Integer> gifts = new LinkedHashMap<>();
        LocalDateTime now = DateTimes.now();

        for (String name : order.getNames()) {
            int orderQuantity = order.getOrder().get(name);
            Product promoProduct = getValidPromoProduct(name, inventory, now);

            if (promoProduct == null) {
                continue;
            }

            Promotion promotion = promoProduct.getPromotion();
            int setUnit = promotion.getBuy() + promotion.getGet();

            if (orderQuantity % setUnit == promotion.getBuy()) {
                if (promoProduct.getQuantity() >= orderQuantity + 1) {
                    gifts.put(name, 1);
                }
            }
        }
        return gifts;
    }

    public Map<String, Integer> checkStockShortage(Order order, Inventory inventory) {
        Map<String, Integer> shortages = new LinkedHashMap<>();
        LocalDateTime now = DateTimes.now();

        for (String name : order.getNames()) {
            int orderQuantity = order.getOrder().get(name);
            Product promoProduct = getValidPromoProduct(name, inventory, now);

            if (promoProduct == null) {
                continue;
            }

            Promotion promotion = promoProduct.getPromotion();
            int setUnit = promotion.getBuy() + promotion.getGet();
            int promoStock = promoProduct.getQuantity();

            int maxSets = promoStock / setUnit;

            int maxPromoableQuantity = maxSets * setUnit;

            if (orderQuantity > maxPromoableQuantity) {
                int shortageCount = orderQuantity - maxPromoableQuantity;
                shortages.put(name, shortageCount);
            }
        }
        return shortages;
    }

    private Product getValidPromoProduct(String name, Inventory inventory, LocalDateTime now) {
        List<Product> products = inventory.getProducts(name);

        // 리스트에서 프로모션이 있는 상품을 찾음
        Product promoProduct = null;

        for (Product product : products) {
            if (product.hasPromotion()) {
                promoProduct = product;
            }
        }

        if (promoProduct != null && promoProduct.getPromotion().isActive(now.toLocalDate())) {
            return promoProduct;
        }
        return null;
    }
}
