package store.controller;

import static store.util.Parser.parseProducts;

import java.util.Map;
import store.domain.Inventory;
import store.domain.Order;
import store.domain.ProductLoader;
import store.domain.PromotionVerifier;
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
        outputView.printMessage("안녕하세요. W편의점입니다.\n");

        Inventory inventory = loader.load();
        outputView.printInventory(inventory);
        Order order = getValidOrder(inventory);

        processFreeGift(order, inventory);
        processStockShortage(order, inventory);

        boolean validMembership = getValidMembership();

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
}
