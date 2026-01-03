package store.view;

import camp.nextstep.edu.missionutils.Console;

public class InputView {
    public String readItem() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        return Console.readLine();
    }


    public boolean askYesOrNo(String message) {
        System.out.println(message);
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

    // 프로모션 혜택 (1개 더 받을래?)
    public boolean askFreeBonus(String name) {
        return askYesOrNo("현재 " + name + "은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
    }

    // 프로모션 재고 부족 (정가로 살래?)
    public boolean askStockShortage(String name, int count) {
        return askYesOrNo("현재 " + name + " " + count + "개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
    }

    public boolean askMembership() {
        return askYesOrNo("멤버십 할인을 받으시겠습니까? (Y/N)");
    }

    public boolean askAdditionalPurchase() {
        return askYesOrNo("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
    }
}
