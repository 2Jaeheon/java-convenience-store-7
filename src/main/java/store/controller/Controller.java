package store.controller;

import java.util.ArrayList;
import java.util.List;
import store.domain.OrderRequest;
import store.domain.OrderResponse;
import store.domain.Product;
import store.domain.ProductRepository;
import store.domain.Promotion;
import store.domain.Receipt;
import store.util.Parser;
import store.view.InputView;
import store.view.OutputView;

public class Controller {
    private final InputView inputView;
    private final OutputView outputView;
    private final Parser parser;
    private final ProductRepository repository;

    public Controller(InputView inputView, OutputView outputView, Parser parser, ProductRepository repository) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.parser = parser;
        this.repository = repository;
    }

    public void run() {
        outputView.printInventory(repository.findAll());

        List<OrderResponse> processedOrders = process();

        int membershipDiscount = calculateMembershipDiscount(processedOrders);
        Receipt receipt = new Receipt(processedOrders, membershipDiscount);
        outputView.printReceipt(receipt);
    }

    private int calculateMembershipDiscount(List<OrderResponse> responses) {
        if (!inputView.askMembership()) {
            return 0;
        }

        int totalNonPromoAmount = 0;

        for (OrderResponse response : responses) {
            Product product = getProductByName(response.getName()); // 리포지토리 조회

            int quantity = response.getQuantity();
            int price = response.getPrice();
            int promoCount = response.getPromotionCount();

            // 프로모션 미적용 금액 계산
            if (product.hasPromotion()) {
                Promotion promo = product.getPromotion();
                // 프로모션 적용된 세트 수 (예: 2+1이면 3개씩 묶임)
                int sets = promoCount / promo.getGet();
                int coveredQuantity = sets * (promo.getBuy() + promo.getGet());

                // 전체 수량에서 프로모션 적용된 수량을 뺀 나머지
                int remainingQuantity = quantity - coveredQuantity;
                totalNonPromoAmount += remainingQuantity * price;
            } else {
                // 프로모션 없는 상품은 전액 할인 대상
                totalNonPromoAmount += quantity * price;
            }
        }

        // 30% 할인, 최대 8000원 한도
        int discount = (int) (totalNonPromoAmount * 0.3);
        return Math.min(discount, 8000);
    }

    private Product getProductByName(String name) {
        return repository.findByName(name).getFirst();
    }

    private List<OrderResponse> process() {
        while (true) {
            try {
                outputView.printOrderMessage();
                String input = inputView.readOrder();
                List<OrderRequest> orderRequests = parser.parseOrders(input);

                validateAllOrdersStock(orderRequests);
                List<OrderResponse> responses = new ArrayList<>();
                for (OrderRequest request : orderRequests) {
                    // 요청을 넣으면 결과(Response)가 나옴
                    OrderResponse response = processOrder(request);
                    responses.add(response);
                }

                return responses;
            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private OrderResponse processOrder(OrderRequest request) {
        List<Product> products = repository.findByName(request.getName());

        // 프로모션 상품과 일반 상품을 가져옴
        Product promoProduct = null;
        Product generalProduct = null;
        for (Product product : products) {
            if (product.hasPromotion()) {
                promoProduct = product;
                continue;
            }

            generalProduct = product;
        }

        int currentQuantity = request.getQuantity();
        int price = products.get(0).getPrice(); // 가격 정보 가져오기

        // 프로모션 상품이 정상적으로프로모션이 가능할 때에 검증을 진행
        if (promoProduct != null && promoProduct.isPromotionApplicable()) {

            // 고객이 해당 수량보다 적게 가져온 경우
            currentQuantity = checkFreeGift(promoProduct, currentQuantity);

            // 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우
            // 일부 수량에 대해 정가로 결제할지 여부에 대한 안내 메시지
            currentQuantity = checkPromotionStockShort(promoProduct, currentQuantity);
        }

        // 증정 개수를 계산
        int promotionCount = calculatePromotionCount(promoProduct, currentQuantity);
        decreaseStock(promoProduct, generalProduct, currentQuantity);

        return new OrderResponse(request.getName(), price, currentQuantity, promotionCount);
    }

    private int calculatePromotionCount(Product promoProduct, int quantity) {
        if (promoProduct == null || !promoProduct.isPromotionApplicable()) {
            return 0;
        }

        Promotion promo = promoProduct.getPromotion();

        // 실제 프로모션 적용 가능한 수량
        int applicableQty = Math.min(quantity, promoProduct.getQuantity());

        int sets = applicableQty / (promo.getBuy() + promo.getGet());
        return sets * promo.getGet();
    }

    private void decreaseStock(Product promoProduct, Product generalProduct, int quantity) {
        // 기존 작성하신 로직 혹은 아래와 같이 구현
        if (promoProduct != null) {
            int deduction = Math.min(promoProduct.getQuantity(), quantity);
            promoProduct.reduceQuantity(deduction);
            quantity -= deduction;
        }

        if (quantity > 0 && generalProduct != null) {
            generalProduct.reduceQuantity(quantity);
        }
    }

    private int checkPromotionStockShort(Product promoProduct, int quantity) {
        if (promoProduct.getQuantity() >= quantity) {
            return quantity;
        }

        Promotion promotion = promoProduct.getPromotion();

        // 프로모션 적용 가능한 최대 세트 수
        int sets = promoProduct.getQuantity() / (promotion.getBuy() + promotion.getGet());
        int promotionCovered = sets * (promotion.getBuy() + promotion.getGet());

        // 정가로 사야 하는 개수
        int nonPromoCount = quantity - promotionCovered;

        if (nonPromoCount > 0) {
            if (!inputView.askShortStorage(promoProduct.getName(), nonPromoCount)) {
                return quantity - nonPromoCount;
            }
        }
        return quantity;
    }

    // 프로모션이 가능한데, 고객이 해당 수량보다 적게 가져온 경우 추가 여부를 체크
    private int checkFreeGift(Product promoProduct, int quantity) {
        Promotion promotion = promoProduct.getPromotion();
        int unit = promotion.getBuy() + promotion.getGet();

        // 조건: (가져온수량) % (Buy+Get) == Buy
        if (quantity % unit == promotion.getBuy()) {
            // 재고가 충분한지 확인
            if (promoProduct.getQuantity() >= quantity + 1) {
                // 사용자 입력 확인
                if (inputView.askOneFreeProduct(promoProduct.getName())) {
                    return quantity + 1;
                }
            }
        }
        return quantity;
    }

    private void validateAllOrdersStock(List<OrderRequest> orderRequests) {
        for (OrderRequest orderRequest : orderRequests) {
            validateOrder(orderRequest.getName(), orderRequest.getQuantity());
        }
    }

    private void validateOrder(String name, int quantity) {
        if (!repository.isExist(name)) {
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
}
