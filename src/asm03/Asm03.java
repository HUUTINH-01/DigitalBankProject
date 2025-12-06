package asm03;

import asm02.models.Customer;
import asm02.models.Account;
import java.util.Scanner;
import asm03.models.*;

import static asm02.Asm02.addCustomer;

/**
 * ASM03 コンソール画面クラス。
 * 機能: 顧客追加、顧客情報表示、ATM/ローン口座追加、出金、取引履歴表示。
 * ASM02 のモデル（Bank, Customer, Account など）を再利用します。
 */
public class Asm03 {
    private static final String AUTHOR = "FX73392";
    private static final String VERSION = "v3.0.0";
    private static final Scanner input = new Scanner(System.in);
    private static final DigitalBank bank = new DigitalBank();

    // デモ用：デフォルト顧客（個人番号は 12 桁）
    private static final String CUSTOMER_ID = "080201009584";
    private static final String CUSTOMER_NAME = "NGUYEN HUU TINH";

    public static void main(String[] args) {
        seed();
        printMainTitle();
        while (true) {
            Integer choice = showMainMenu();   // ASM02 と同じスタイルで入力

            // メインメニュー：3回連続で誤入力 → プログラム終了
            if (choice == null) {
                System.out.println("入力回数が上限を超えたため、プログラムを終了します。");
                return;
            }

            switch (choice) {
                case 1 -> viewCustomerInfo();
                case 2 -> addSavingsAccount();
                case 3 -> addLoanAccount();
                case 4 -> doWithdraw();
                case 5 -> viewHistory();
                case 0 -> {
                    System.out.println("ご利用ありがとうございました。");
                    return;
                }
                default -> System.out.println("無効な選択です。もう一度お試しください。\n");
            }
        }
    }

    /* ================== UI ================== */

    /**
     * タイトルバナーの表示。
     */
    public static void printMainTitle() {
        System.out.println("--------+--------------+------------");
        System.out.printf("| 　デジタルバンク | %s@%s 　|%n", AUTHOR, VERSION);
        System.out.println("--------+--------------+------------");
    }

    /**
     * ASM03 のメインメニューを表示し、ユーザーの選択を受け取る。
     *
     * @return 0〜6 の整数。3回連続で誤入力した場合は null を返す。
     */
    private static Integer showMainMenu() {
        System.out.println("------------------------------------");
        System.out.println(" 1. 顧客情報を表示");
        System.out.println(" 2. ATM 口座を追加");
        System.out.println(" 3. ローン口座を追加");
        System.out.println(" 4. 出金する");
        System.out.println(" 5. 取引履歴を表示 ");
        System.out.println(" 0. 終了");
        System.out.println("------------------------------------");

        return readIntInRangeTry("機能を選択してください: ", 0, 5, 3);
    }

    /* ================== SEED DATA ================== */

    /**
     * デフォルト顧客を 1 件だけ登録しておく。
     * まだ登録されていない場合のみ追加する。
     */
    private static void seed() {
        if (bank.getCustomerById(CUSTOMER_ID) == null) {
            bank.addCustomer(new asm02.models.Customer(CUSTOMER_NAME, CUSTOMER_ID));
        }
    }

    /* ================== メイン機能 ================== */

    /**
     * デモ用の固定顧客（CUSTOMER_ID）の情報を表示する。
     */
    private static void viewCustomerInfo() {
        Customer customer = bank.getCustomerById(CUSTOMER_ID);
        if (customer == null) {
            System.out.println("顧客が登録されていません。");
            return;
        }
        customer.displayInformation();
    }

    /**
     * デモ顧客に対して普通預金（ATM）口座を追加する。
     */
    private static void addSavingsAccount() {
        // ステップ1：口座番号入力
        String accountNumber = readLineMatchingTry(
                "ATM 口座番号（6桁）を入力してください: ",
                "\\d{6}",
                "口座番号は 6 桁の数字で入力してください。",
                3
        );
        if (accountNumber == null) {
            System.out.println("メニューに戻ります。\n");
            return;
        }

        // ステップ2：重複チェック
        if (bank.isAccountExisted(accountNumber)) {
            System.out.println("この口座番号はすでに存在しています。\n");
            return;
        }

        // ステップ3：初期残高入力
        Double initialBalance = readDoubleMinTry("初期残高（0 以上）を入力してください: ", 0, 3);
        if (initialBalance == null) {
            System.out.println("メニューに戻ります。\n");
            return;
        }

        SavingsAccount savingsAccount = new SavingsAccount(accountNumber, initialBalance);
        Customer customer = bank.getCustomerById(CUSTOMER_ID);
        if (customer != null && customer.addAccount(savingsAccount)) {
            System.out.println("ATM 口座の追加に成功しました。\n");
        } else {
            System.out.println("口座を追加できませんでした。\n");
        }
    }

    /**
     * デモ顧客に対してローン口座を追加する。
     */
    private static void addLoanAccount() {
        // ステップ1：ローン口座番号入力
        String accountNumber = readLineMatchingTry(
                "ローン口座番号（6桁）を入力してください: ",
                "\\d{6}",
                "口座番号は 6 桁の数字で入力してください。",
                3
        );
        if (accountNumber == null) {
            System.out.println("メニューに戻ります。\n");
            return;
        }

        // ステップ2：重複チェック
        if (bank.isAccountExisted(accountNumber)) {
            System.out.println("この口座番号はすでに存在しています。\n");
            return;
        }

        // ステップ3：ローン口座作成（初期利用額 = 0）
        LoanAccount loanAccount = new LoanAccount(accountNumber, 0);
        Customer customer = bank.getCustomerById(CUSTOMER_ID);
        if (customer != null && customer.addAccount(loanAccount)) {
            System.out.println("ローン口座の追加に成功しました。\n");
        } else {
            System.out.println("口座を追加できませんでした。\n");
        }
    }

    /**
     * デモ顧客の任意の口座から出金する。
     */
    private static void doWithdraw() {
        // ステップ1：口座番号入力
        String accountNumber = readLineMatchingTry(
                "出金したい口座番号（6桁）を入力してください: ",
                "\\d{6}",
                "口座番号は 6 桁の数字で入力してください。",
                3
        );
        if (accountNumber == null) {
            System.out.println("メニューに戻ります。\n");
            return;
        }

        // ステップ2：金額入力
        Double amount = readDoubleMinTry("出金額を入力してください: ", 1, 3);
        if (amount == null) {
            System.out.println("メニューに戻ります。\n");
            return;
        }

        boolean success = bank.withdraw(CUSTOMER_ID, accountNumber, amount);
        System.out.println(success ? "出金に成功しました。\n" : "出金に失敗しました。\n");
    }

    /**
     * デモ顧客のすべての口座に対する取引履歴を表示する。
     */
    private static void viewHistory() {
        Customer customer = bank.getCustomerById(CUSTOMER_ID);
        if (customer == null || customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            System.out.println("口座が登録されていません。\n");
            return;
        }

        for (Account account : customer.getAccounts()) {
            System.out.println("口座番号: " + account.getAccountNumber());
            if (account instanceof SavingsAccount savingsAccount) {
                savingsAccount.getTransactions()
                        .forEach(transaction -> System.out.println("  " + transaction));
            } else if (account instanceof LoanAccount loanAccount) {
                loanAccount.getTransactions()
                        .forEach(transaction -> System.out.println("  " + transaction));
            }
        }
        System.out.println();
    }

    /* ================== 入力ユーティリティ（ASM02 スタイル） ================== */

    /**
     * 整数を安全に読み込むユーティリティ。
     * 指定した範囲 [minValue..maxValue] で maxAttempts 回まで再入力を許可する。
     *
     * @return 有効な整数、または失敗時は null
     */
    private static Integer readIntInRangeTry(String promptMessage, int minValue, int maxValue, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            try {
                int inputValue = Integer.parseInt(input.nextLine().trim());
                if (inputValue < minValue || inputValue > maxValue) {
                    System.out.printf("値は %d 〜 %d の範囲で入力してください。（%d/%d 回目）%n",
                            minValue, maxValue, attempt, maxAttempts);
                    continue;
                }
                return inputValue;
            } catch (Exception e) {
                System.out.printf("整数で入力してください。（%d/%d 回目）%n", attempt, maxAttempts);
            }
        }
        return null;
    }

    /**
     * 最小値（minValue）以上の実数を安全に読み込むユーティリティ。
     * 最大 maxAttempts 回まで再入力を許可する。
     *
     * @return 有効な double、または失敗時は null
     */
    private static Double readDoubleMinTry(String promptMessage, double minValue, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            try {
                double inputValue = Double.parseDouble(input.nextLine().trim());
                if (inputValue < minValue) {
                    System.out.printf("値は %.0f 以上で入力してください。（%d/%d 回目）%n",
                            minValue, attempt, maxAttempts);
                    continue;
                }
                return inputValue;
            } catch (Exception e) {
                System.out.printf("数値で入力してください。（%d/%d 回目）%n", attempt, maxAttempts);
            }
        }
        return null;
    }

    /**
     * 正規表現 pattern にマッチする文字列を読み込むユーティリティ。
     * 最大 maxAttempts 回まで再入力を許可する。
     *
     * @return マッチした文字列、または失敗時は null
     */
    private static String readLineMatchingTry(String promptMessage,
                                              String pattern,
                                              String errorMessage,
                                              int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print(promptMessage);
            String userInput = input.nextLine().trim();
            if (userInput.matches(pattern)) return userInput;
            System.out.printf("%s （%d/%d 回目）%n", errorMessage, attempt, maxAttempts);
        }
        return null;
    }
}
