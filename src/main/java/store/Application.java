package store;

import java.util.List;
import java.util.Map;
import store.controller.Controller;
import store.domain.Product;
import store.domain.ProductRepository;
import store.domain.Promotion;
import store.util.Parser;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ProductRepository repository = new ProductRepository();
        Parser parser = new Parser();
        FileLoader loader = new FileLoader();

        Map<String, Promotion> promotionMap = loader.loadPromotions();
        List<Product> products = loader.loadProducts(promotionMap);

        for (Product product : products) {
            repository.save(product);
        }

        Controller controller = new Controller(inputView, outputView, parser, repository);
        controller.run();
    }
}
