package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.text.DecimalFormat;
import store.domain.OrderResponse;
import store.domain.Receipt;

public class InputView {
    public String readOrder() {
        return Console.readLine();
    }

    public boolean askOneFreeProduct(String name) {
        // 현재 오렌지주스은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
        return askYesOrNo("현재 " + name + "은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
    }

    private boolean askYesOrNo(String s) {
        System.out.println(s);
        while (true) {
            String input = Console.readLine();
            if ("Y".equalsIgnoreCase(input)) {
                return true;
            }
            if ("N".equalsIgnoreCase(input)) {
                return false;
            }
            System.out.println("[ERROR] Y 또는 N만 입력 가능합니다.");
        }
    }

    public boolean askShortStorage(String name, int nonPromoCount) {
        return askYesOrNo("현재 " + name + " " + nonPromoCount + "개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
    }

    public boolean askMembership() {
        return askYesOrNo("멤버십 할인을 받으시겠습니까? (Y/N)");
    }
}
