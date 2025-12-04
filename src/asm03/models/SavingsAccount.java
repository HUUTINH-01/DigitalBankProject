package asm03.models;

import asm02.models.Account;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通預金口座（ATM口座）クラス。
 * ASM02 の {@link Account} を継承し、
 * 出金（Withdraw）とレシート出力（ReportService）に対応します。
 */
public class SavingsAccount extends Account implements Withdraw, ReportService {

    // 一般会員：1回の出金上限は 5,000,000 VND
    public static final double SAVINGS_ACCOUNT_MAX_WITHDRAW = 5_000_000;

    // この口座に紐づく出金取引履歴
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param accountNumber 口座番号
     * @param initBalance   初期残高
     */
    public SavingsAccount(String accountNumber, double initBalance) {
        super(accountNumber, initBalance);
    }

    /**
     * 出金リクエストが許可されるかどうかを判定する。
     * 条件:
     * - 最低出金額：50,000
     * - 10,000 の倍数であること
     * - 一般会員の場合：1回あたり 5,000,000 を超えてはいけない
     * - 出金後の残高が 50,000 以上残ること
     */
    @Override
    public boolean isAccepted(double amount) {
        if (amount < 50_000) return false;                   // 最低 50,000
        if (amount % 10_000 != 0) return false;              // 10,000 の倍数

        if (!isPremium() && amount > SAVINGS_ACCOUNT_MAX_WITHDRAW) {
            return false;                                    // 一般会員は 500万まで
        }

        double remainingBalance = getBalance() - amount;
        return remainingBalance >= 50_000;                   // 出金後 50,000 以上残す
    }

    /**
     * 出金処理を実行する。
     * - 許可された場合：残高を減算し、成功の取引履歴を保存してレシートを出力
     * - 許可されない場合：失敗の取引履歴のみを保存
     */
    @Override
    public boolean withdraw(double amount) {
        boolean accepted = isAccepted(amount);

        if (accepted) {
            // 残高を減らす
            setBalance(getBalance() - amount);
            transactions.add(new Transaction(getAccountNumber(), amount, true));
            log(amount);   // レシート出力
        } else {
            transactions.add(new Transaction(getAccountNumber(), amount, false));
        }
        return accepted;
    }

    /**
     * 普通預金口座の出金レシートをコンソールに表示する。
     */
    @Override
    public void log(double amount) {
        System.out.println("+-----------------------------+");
        System.out.println("|  　　普通預金 出金レシート 　  |");
        System.out.println("+-----------------------------+");
        System.out.printf("| 口座番号   : %s%n", getAccountNumber());
        System.out.printf("| 出金額     : %,.0f%n", amount);
        System.out.printf("| 出金後残高 : %,.0f%n", getBalance());
        System.out.println("+-----------------------------+");
    }

    /**
     * この口座に紐づく出金取引履歴一覧を取得する。
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
