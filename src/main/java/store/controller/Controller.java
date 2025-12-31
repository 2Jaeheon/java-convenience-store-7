package store.controller;

import static store.util.Parser.parseProducts;

import java.util.Map;
import store.domain.Inventory;
import store.domain.Order;
import store.domain.ProductLoader;
import store.domain.PromotionVerifier;
import store.domain.Receipt;
import store.domain.ReceiptCalculator;
import store.view.InputView;
import store.view.OutputView;

public class Controller {
    private InputView inputView;
    private OutputView outputView;
    private ProductLoader loader;
    private PromotionVerifier verifier;

    public Controller(InputView inputView, OutputView outputView, ProductLoader loader, PromotionVerifier verifier) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.loader = loader;
        this.verifier = verifier;
    }

    public void run() {
        Inventory inventory = loader.load();

        // 전체 흐름 반복 (재구매)
        boolean shopping = true;
        while (shopping) {
            outputView.printMessage("안녕하세요. W편의점입니다.\n"); // 매 반복마다 인사? 요구사항 예시엔 처음에만 있음. 하지만 예시2 재구매시 다시 인사함.
            // 요구사항: 재구매 시 "안녕하세요..."부터 다시 출력
            processOrderSequence(inventory);
            shopping = askForAdditionalPurchase();
        }
    }

    private void processOrderSequence(Inventory inventory) {
        outputView.printInventory(inventory);

        Order order = getValidOrder(inventory);

        // 1. 프로모션 혜택 안내 (증정품 추가 여부)
        processFreeGift(order, inventory);

        // 2. 재고 부족 안내 (정가 결제 여부)
        processStockShortage(order, inventory);

        // 3. 멤버십 할인 여부
        boolean applyMembership = getValidMembership();

        // 4. 최종 계산
        ReceiptCalculator calculator = new ReceiptCalculator(inventory);
        Receipt receipt = calculator.calculate(order, applyMembership);

        // 5. 영수증 출력
        outputView.printReceipt(receipt, inventory);

        // 6. 재고 차감
        inventory.updateStock(order);
    }

    private boolean getValidMembership() {
        while (true) {
            try {
                outputView.printMessage("\n멤버십 할인을 받으시겠습니까? (Y/N)");
                String membershipInput = inputView.readMembership();
                return "Y".equalsIgnoreCase(membershipInput);

            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private void validateYesNo(String input) {
        if (!"Y".equalsIgnoreCase(input) && !"N".equalsIgnoreCase(input)) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }

    private void processFreeGift(Order order, Inventory inventory) {
        Map<String, Integer> gifts = verifier.verifyFreeGift(order, inventory);
        for (String name : gifts.keySet()) {
            outputView.printFreeGift(gifts);

            String response = inputView.readYesNo();
            if ("Y".equalsIgnoreCase(response)) {
                order.addQuantity(name, 1);
            }
        }
    }

    private void processStockShortage(Order order, Inventory inventory) {
        Map<String, Integer> shortages = verifier.checkStockShortage(order, inventory);

        for (Map.Entry<String, Integer> entry : shortages.entrySet()) {
            String name = entry.getKey();
            int shortageCount = entry.getValue();

            outputView.printMessage(
                    String.format("\n현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)", name, shortageCount));

            String response = inputView.readYesNo();

            if ("N".equalsIgnoreCase(response)) {
                order.decreaseQuantity(name, shortageCount);
            }
        }
    }

    private Order getValidOrder(Inventory inventory) {
        while (true) {
            try {
                outputView.printMessage("\n구매하실 상품명과 수량을 입력해 주세요. (예: [콜라-10],[사이다-3])");
                String input = inputView.readProducts();

                Order order = parseProducts(input);
                inventory.validateOrder(order);
                return order;

            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private boolean askForAdditionalPurchase() {
        while (true) {
            try {
                outputView.printMessage("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
                String response = inputView.readYesNo();
                validateYesNo(response);
                return "Y".equalsIgnoreCase(response);
            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }
}
