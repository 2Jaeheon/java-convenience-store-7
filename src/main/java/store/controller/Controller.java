package store.controller;

import java.util.List;
import store.domain.Order;
import store.domain.Product;
import store.domain.ProductRepository;
import store.domain.Promotion;
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

        List<Order> processedOrders = process();


    }

    private List<Order> process() {
        while (true) {
            try {
                outputView.printOrderMessage();
                String input = inputView.readOrder();
                List<Order> orders = parser.parseOrders(input);

                validateAllOrdersStock(orders);
                for (Order order : orders) {
                    processOrder(order);
                }

                return orders;
            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private void processOrder(Order order) {

    }

    private void validateAllOrdersStock(List<Order> orders) {
        for (Order order : orders) {
            validateOrder(order.getName(), order.getQuantity());
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
