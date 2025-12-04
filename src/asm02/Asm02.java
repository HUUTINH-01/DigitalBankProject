package asm02;

import asm02.models.*;

import java.util.List;
import java.util.Scanner;

/**
 * デジタルバンキングアプリ（ASM02）
 * - メニュー操作：顧客追加・口座追加・表示・検索
 * - 入力エラーを3回まで許容、超えたら戻る
 */
public class Asm02 {
    public static final String AUTHOR = "FX73392";
    public static final String VERSION = "v2.0.0";

    private static final Scanner input = new Scanner(System.in);
    private static final Bank bank = new Bank();

    /**
     * アプリ起動処理：タイトル表示 → メニュー処理ループ
     * 0を選択すると終了
     */
    public static void main(String[] args) {
        printMainTitle();
        while (true) {
            int choice = showMainMenu();
            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> addAccountForCustomer();
                case 3 -> showCustomerList();
                case 4 -> searchCustomerById();
                case 5 -> searchCustomersByName();
                case 0 -> {
                    System.out.println("ご利用ありがとうございました。");
                    return;
                }
            }
        }
    }

    /*======================= 入力ユーティリティ =======================*/

    /**
     * 整数入力（範囲指定・3回まで再入力可能）
     */
    private static Integer readIntInRangeTry(String promptMessage, int minValue, int maxValue, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            try {
                int inputValue = Integer.parseInt(input.nextLine().trim());
                if (inputValue < minValue || inputValue > maxValue) {
                    System.out.printf("入力値は [%d..%d] の範囲内で入力してください。（%d/%d回目）%n",
                            minValue, maxValue, attempt, maxAttempts);
                    continue;
                }
                return inputValue;

            } catch (Exception e) {
                System.out.printf("入力エラー：整数を入力してください。（%d/%d回目）%n", attempt, maxAttempts);
            }
        }
        return null;
    }

    /**
     * 実数入力（最小値指定・3回まで再入力可能）
     */
    private static Double readDoubleMinTry(String promptMessage, double minValue, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            try {
                double inputValue = Double.parseDouble(input.nextLine().trim());
                if (inputValue < minValue) {
                    System.out.printf("入力値は %.2f 以上でなければなりません。（%d/%d回目）%n", minValue, attempt, maxAttempts);
                    continue;
                }
                return inputValue;

            } catch (Exception e) {
                System.out.printf("入力エラー：数値を入力してください。（%d/%d回目）%n", attempt, maxAttempts);
            }
        }
        return null;
    }

    /**
     * 空ではない文字列入力
     */
    private static String readNonEmptyLineTry(String promptMessage, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            String userInput = input.nextLine().trim();
            if (!userInput.isEmpty()) return userInput;
            System.out.printf("空欄は不可です。（%d/%d回目）%n", attempt, maxAttempts);
        }
        return null;
    }

    /**
     * 正規表現に一致する文字列入力
     */
    private static String readLineMatchingTry(String promptMessage, String pattern, String errorMessage, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            String userInput = input.nextLine().trim();
            if (userInput.matches(pattern)) return userInput;
            System.out.printf("%s （%d/%d回目）%n", errorMessage, attempt, maxAttempts);
        }
        return null;
    }

    /*======================= UI =======================*/

    /**
     * タイトル表示
     */
    public static void printMainTitle() {
        System.out.println("--------+----------------+----------------");
        System.out.printf("| デジタルバンク | %s @ %s |%n", AUTHOR, VERSION);
        System.out.println("--------+----------------+----------------");
    }

    /**
     * メニュー表示＆選択入力
     */
    public static int showMainMenu() {
        System.out.println(" 1. 顧客を追加する");
        System.out.println(" 2. 顧客に口座を追加する ");
        System.out.println(" 3. 顧客一覧を表示する");
        System.out.println(" 4. 個人番号で検索");
        System.out.println(" 5. 氏名検索");
        System.out.println(" 0. 終了する");
        System.out.println("-----------------------------------------");
        Integer choice = readIntInRangeTry("機能を選択してください： ", 0, 5, 3);
        return choice == null ? 0 : choice;
    }

    /*======================= 機能 =======================*/

    /**
     * 顧客追加
     */
    public static void addCustomer() {
        String name = readLineMatchingTry(
                "顧客名を入力してください： ",
                "[\\p{L} ]+",
                "名前は文字とスペースのみ使用可能です。",
                3);
        if (name == null) { System.out.println("戻ります。\n"); return; }

        String id = readLineMatchingTry(
                "個人番号（12桁）を入力してください： ",
                "\\d{12}",
                "個人番号は12桁の数字でなければなりません。",
                3);
        if (id == null) { System.out.println("戻ります。\n"); return; }

        try {
            Customer customer = new Customer(name, id);
            if (bank.addCustomer(customer)) {
                System.out.println("顧客の追加が完了しました！\n");
            } else {
                System.out.println("エラー：既に登録されている個人番号です。\n");
            }
        } catch (Exception e) {
            System.out.println("予期しないエラーが発生しました。\n");
        }
    }

    /**
     * 口座追加
     */
    public static void addAccountForCustomer() {
        String id = readLineMatchingTry(
                "個人番号を入力してください： ",
                "\\d{12}",
                "個人番号は12桁の数字でなければなりません。",
                3);
        if (id == null) { System.out.println("戻ります。\n"); return; }

        Customer customer = bank.findCustomerById(id);
        if (customer == null) {
            System.out.println("エラー：顧客が存在しないため、口座追加できません。\n");
            return;
        }

        String accountNumber = readLineMatchingTry(
                "口座番号（6桁）を入力してください： ",
                "\\d{6}",
                "口座番号は6桁で入力してください。",
                3);
        if (accountNumber == null) { System.out.println("戻ります。\n"); return; }

        Double balance = readDoubleMinTry("初期残高を入力してください： ", 0.0, 3);
        if (balance == null) { System.out.println("戻ります。\n"); return; }

        try {
            Account account = new Account(accountNumber, balance);
            if (customer.addAccount(account)) {
                System.out.println("口座追加が完了しました！\n");
            } else {
                System.out.println("エラー：既存の口座番号と重複しています。\n");
            }
        } catch (Exception e) {
            System.out.println("予期しないエラーが発生しました。\n");
        }
    }

    /**
     * 顧客一覧（1行サマリー）
     */
    public static void showCustomerList() {
        List<Customer> customers = bank.getCustomers();
        if (customers == null || customers.isEmpty()) {
            System.out.println("\n登録されている顧客はいません。\n");
            return;
        }

        customers.sort(java.util.Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER));

        System.out.println("\n================ 顧客一覧 ================");
        System.out.printf("%-4s | %-12s | %-20s | %-8s | %15s | %5s%n",
                "No", "個人番号", "氏名", "区分", "総残高", "口座数");
        System.out.println("-----+--------------+----------------------+----------+------------------+-------");

        int index = 1;
        for (Customer customer : customers) {
            String type = customer.isPremiumCustomer() ? "PREMIUM" : "NORMAL";

            System.out.printf("%4d | %-12s | %-20s | %-8s | %,15.2f | %5d%n",
                    index++,
                    customer.getCustomerId(),
                    customer.getName(),
                    type,
                    customer.getTotalBalance(),
                    customer.getAccounts().size()
            );
        }
        System.out.println("===========================================");
    }

    /**
     * 個人番号検索
     */
    public static void searchCustomerById() {
        String id = readLineMatchingTry(
                "検索する個人番号を入力： ",
                "\\d{12}",
                "個人番号は12桁の数字で入力してください。",
                3);
        if (id == null) { System.out.println("戻ります。\n"); return; }

        Customer customer = bank.findCustomerById(id);

        if (customer == null) {
            System.out.println("該当する顧客が見つかりません。\n");
        } else {
            customer.displayInformation();
        }
    }

    /**
     * 氏名検索
     */
    public static void searchCustomersByName() {
        String keyword = readNonEmptyLineTry("氏名キーワードを入力： ", 3);
        if (keyword == null) { System.out.println("戻ります。\n"); return; }

        List<Customer> list = bank.searchCustomersByName(keyword);
        if (list.isEmpty()) {
            System.out.println("一致する顧客が見つかりませんでした。\n");
            return;
        }

        System.out.println("検索結果：");
        for (Customer c : list) {
            c.displayInformation();
            System.out.println("---------------------------");
        }
    }
}
