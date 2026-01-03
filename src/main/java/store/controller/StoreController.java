package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import store.model.Order;
import store.model.Product;
import store.model.ProductRepository;
import store.model.Promotion;
import store.model.Receipt;
import store.util.Parser;
import store.view.InputView;
import store.view.OutputView;

import java.util.List;

public class StoreController {
    private final InputView inputView;
    private final OutputView outputView;
    private final ProductRepository repository;
    private final Parser parser;

    public StoreController(InputView inputView, OutputView outputView, ProductRepository repository, Parser parser) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.repository = repository;
        this.parser = parser;
    }

    public void run() {
        do {
            outputView.printProducts(repository.findAll());

            List<Order> processedOrders = getValidOrdersAndProcess();

            long membershipDiscount = calculateMembershipDiscount(processedOrders);
            Receipt receipt = new Receipt(processedOrders, membershipDiscount);
            outputView.printReceipt(receipt);

        } while (inputView.askAdditionalPurchase());
    }

    private List<Order> getValidOrdersAndProcess() {
        while (true) {
            try {
                String input = inputView.readItem();
                List<Order> orders = parser.parseOrders(input);

                validateAllOrdersStock(orders);

                for (Order order : orders) {
                    processOrder(order);
                }

                return orders;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void validateAllOrdersStock(List<Order> orders) {
        for (Order order : orders) {
            validateOrder(order.getName(), order.getQuantity());
        }
    }

    private void processOrder(Order order) {
        validateOrder(order.getName(), order.getQuantity());

        // 프로덕트 리스트 가져옴
        List<Product> products = repository.findByName(order.getName());

        // 프로모션 상품과 일반 상품 가져오기
        Product promoProduct = null;
        Product generalProduct = null;
        for (Product product : products) {
            if (product.hasPromotion()) {
                promoProduct = product;
            } else {
                generalProduct = product;
            }
        }

        // 가격 정보를 order에 세팅
        if (promoProduct != null) {
            order.setPrice(promoProduct.getPrice());
        } else if (generalProduct != null) {
            order.setPrice(generalProduct.getPrice());
        }

        // 프로모션 적용
        // 프로모션 상품이 있고, 기간이 유효할 때만 실행
        if (promoProduct != null && promoProduct.isPromotionApplicable(DateTimes.now().toLocalDate())) {

            // 2+1인데 2개만 샀니? -> 1개 더 줄까?
            checkFreeBonus(promoProduct, order);

            // 프로모션 재고 부족하니? -> 정가로 살래?
            checkPromotionStock(promoProduct, order);

            // 증정 개수 확정
            applyPromotionCount(promoProduct, order);
        }

        // 재고 차감
        decreaseStock(promoProduct, generalProduct, order.getQuantity());


    }

    private void validateOrder(String name, int quantity) {
        if (!repository.existByName(name)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }

        List<Product> products = repository.findByName(name);

        int totalStock = 0;
        for (Product product : products) {
            totalStock += product.getQuantity();
        }

        if (totalStock < quantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private void checkFreeBonus(Product promoProduct, Order order) {
        Promotion promo = promoProduct.getPromotion();
        int currentQty = order.getQuantity();

        // (구매수량) % (Buy + Get) == Buy 인 경우 (예: 2+1 행사에서 2개 샀을 때)
        if (currentQty % (promo.getBuy() + promo.getGet()) == promo.getBuy()) {
            // 한개를 더 준다고 할 때 재고가 남는지를 확인
            if (promoProduct.getQuantity() >= currentQty + 1) {
                // 사용자에게 물어보기
                if (inputView.askFreeBonus(promoProduct.getName())) {
                    order.addQuantity(1);
                }
            }
        }
    }

    // 프로모션 재고가 부족할 때 일반 재고로 구매하는지를 체크
    private void checkPromotionStock(Product promoProduct, Order order) {
        int orderQuantity = order.getQuantity();
        int stock = promoProduct.getQuantity();

        // 주문 수량이 재고보다 많을 때
        if (orderQuantity > stock) {
            Promotion promo = promoProduct.getPromotion();

            // 프로모션 적용 가능한 세트 수
            int set = stock / (promo.getBuy() + promo.getGet());
            // 프로모션 적용 가능한 총 개수
            int promoCovered = set * (promo.getBuy() + promo.getGet());

            // 정가로 사야 하는 개수
            int nonPromoCount = orderQuantity - promoCovered;

            if (nonPromoCount > 0) {
                // 사용자에게 물어보기
                if (!inputView.askStockShortage(promoProduct.getName(), nonPromoCount)) {
                    // 싫다고 하면 정가 결제분 뺌
                    order.decreaseQuantity(nonPromoCount);
                }
            }
        }
    }

    private void applyPromotionCount(Product promoProduct, Order order) {
        Promotion promo = promoProduct.getPromotion();
        int quantity = order.getQuantity();
        int promoStock = promoProduct.getQuantity();

        // 프로모션 적용 가능 개수
        int applicableQty = Math.min(quantity, promoStock);

        int sets = applicableQty / (promo.getBuy() + promo.getGet());
        int freeCount = sets * promo.getGet();

        order.setPromotionCount(freeCount);
    }

    private void decreaseStock(Product promoProduct, Product generalProduct, int quantity) {
        // 차감할 수량이 없으면 종료
        if (quantity <= 0) {
            return;
        }

        // 프로모션 상품이 없으면 일반 상품에서 다 뺌
        if (promoProduct == null) {
            generalProduct.reduceQuantity(quantity);
            return;
        }

        // 프로모션 재고가 충분하면 거기서 다 뺌
        if (promoProduct.getQuantity() >= quantity) {
            promoProduct.reduceQuantity(quantity);
        } else {
            // 부족하면 프로모션 다 털고, 나머지는 일반에서 뺌
            int remain = quantity - promoProduct.getQuantity();
            promoProduct.reduceQuantity(promoProduct.getQuantity()); // 0으로 만듦

            // 일반 재고가 있으면 거기서 뺌
            if (generalProduct != null) {
                generalProduct.reduceQuantity(remain);
            }
        }
    }

    private long calculateMembershipDiscount(List<Order> orders) {
        if (!inputView.askMembership()) {
            return 0;
        }

        // 멤버십 할인이 적용된 금액
        long nonPromoAmount = 0;

        for (Order order : orders) {
            // 수량이 없으면 스킵
            if (order.getQuantity() == 0) {
                continue;
            }

            List<Product> products = repository.findByName(order.getName());
            Product product = null;

            // 리스트에서 프로모션이 있는 상품을 우선적으로 찾음
            for (Product p : products) {
                if (p.hasPromotion()) {
                    product = p;
                    break;
                }
                product = p;
            }

            if (product == null) {
                continue;
            }

            long itemNonPromoAmount = 0;

            if (product.hasPromotion()) {
                Promotion promo = product.getPromotion();

                int sets = order.getPromotionCount() / promo.getGet();
                int coveredQty = sets * (promo.getBuy() + promo.getGet());
                int remainingQty = order.getQuantity() - coveredQty;
                itemNonPromoAmount = (long) remainingQty * order.getPrice();
            } else {
                itemNonPromoAmount = (long) order.getQuantity() * order.getPrice();
            }

            nonPromoAmount += itemNonPromoAmount;
        }

        long discount = (long) (nonPromoAmount * 0.3);
        return Math.min(discount, 8000);
    }
}