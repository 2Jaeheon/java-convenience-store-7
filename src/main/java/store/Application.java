package store;

import store.controller.Controller;
import store.domain.ProductLoader;
import store.domain.PromotionVerifier;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ProductLoader productLoader = new ProductLoader();
        PromotionVerifier verifier = new PromotionVerifier();
        Controller controller = new Controller(inputView, outputView, productLoader, verifier);

        controller.run();
    }
}
