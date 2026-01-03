package store;

import java.util.List;
import java.util.Map;
import store.controller.StoreController;
import store.model.Product;
import store.model.ProductRepository;
import store.model.Promotion;
import store.util.Parser;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        Parser parser = new Parser();
        FileLoader fileLoader = new FileLoader();
        ProductRepository productRepository = new ProductRepository();

        // 파일 읽어서 Repository에 저장
        Map<String, Promotion> promotions = fileLoader.loadPromotions();
        List<Product> products = fileLoader.loadProducts(promotions);

        for (Product product : products) {
            productRepository.save(product);
        }

        // Controller 실행
        StoreController controller = new StoreController(inputView, outputView, productRepository, parser);
        controller.run();
    }
}
