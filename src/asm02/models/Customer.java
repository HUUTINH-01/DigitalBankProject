package asm02.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 顧客クラス
 * - Userクラスを継承し、銀行顧客としての情報と口座リストを管理する
 */
public class Customer extends User {

    // 顧客が保有している口座リスト
    private final List<Account> accounts;

    /**
     * コンストラクタ：氏名と個人番号を受け取り、口座リストを初期化する
     */
    public Customer(String name, String customerId) {
        super(name, customerId);
        this.accounts = new ArrayList<>();
    }

    /**
     * 口座リストを取得する
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * 口座を追加する
     * - nullは不可
     * - すでに同じ口座番号が存在する場合は追加しない
     *
     * @param account 追加する口座
     * @return 追加成功なら true、失敗なら false
     */
    public boolean addAccount(Account account) {
        if (account == null) {
            return false;
        }

        // 口座番号の重複チェック
        for (Account existing : accounts) {
            if (existing.getAccountNumber().equals(account.getAccountNumber())) {
                return false;
            }
        }
        accounts.add(account);
        return true;
    }

    /**
     * 全口座の合計残高を取得する
     */
    public double getTotalBalance() {
        double sum = 0;
        for (Account acc : accounts) {
            sum += acc.getBalance();
        }
        return sum;
    }

    /**
     * 顧客がプレミアム顧客かどうかを判定する
     * - 少なくとも1つのプレミアム口座を持っていればプレミアム顧客
     */
    public boolean isPremiumCustomer() {
        for (Account acc : accounts) {
            if (acc.isPremium()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 顧客情報と口座一覧を表示する
     * 1. 顧客情報：個人番号・氏名・顧客区分・総残高
     * 2. 口座情報：口座番号・口座区分・残高
     */
    public void displayInformation() {
        String customerType = isPremiumCustomer() ? "PREMIUM" : "NORMAL";

        // 1. 顧客情報
        System.out.println("=========== 顧客情報 ===========");
        System.out.printf("個人番号　　: %s%n", getCustomerId());
        System.out.printf("氏名       : %s%n", getName());
        System.out.printf("顧客区分    : %s%n", customerType);
        System.out.printf("総残高     : %,15.2f%n", getTotalBalance());

        // 2. 口座情報
        System.out.println("----------- 口座一覧 -----------");
        if (accounts.isEmpty()) {
            System.out.println("口座はまだ登録されていません。");
        } else {
            System.out.printf("%-10s | %-8s | %15s%n", "口座番号", "区分", "残高");
            System.out.println("--------------------------------");
            for (Account acc : accounts) {
                System.out.printf(
                        "%-10s | %-8s | %,15.2f%n",
                        acc.getAccountNumber(),
                        acc.getAccountType(),   // "PREMIUM" または "NORMAL"
                        acc.getBalance()
                );
            }
        }
        System.out.println(); // 空行
    }

    /**
     * 口座番号から口座を検索する。
     *
     * @param accountNumber 検索したい口座番号
     * @return 対象の口座が見つかった場合はその Account、見つからない場合は null
     */
    public Account findAccountByNumber(String accountNumber) {
        if (accountNumber == null) return null;
        for (Account account : accounts) {
            if (accountNumber.equals(account.getAccountNumber())) {
                return account;
            }
        }
        return null;
    }

}
