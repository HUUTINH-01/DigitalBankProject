package asm03.models;

import asm02.models.Account;
import java.util.ArrayList;
import java.util.List;

/**
 * ローン口座クラス
 * ASM02 の {@link Account} を継承し、
 * 出金（借入）機能と利用明細（レシート出力）機能を持つクラスです。
 */
public class LoanAccount extends Account implements Withdraw, ReportService {

    // 一般会員の手数料率：5%
    public static final double LOAN_ACCOUNT_WITHDRAW_FEE = 0.05;

    // プレミアム会員の手数料率：1%
    public static final double LOAN_ACCOUNT_WITHDRAW_PREMIUM_FEE = 0.01;

    // ローン利用限度額（100,000,000 VND）
    public static final double LOAN_ACCOUNT_MAX_BALANCE = 100_000_000;

    // この口座に紐づく取引履歴（出金のみを想定）
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * コンストラクタ
     * @param accountNumber 口座番号
     * @param initUsed      初期利用額（すでに借入済みの金額として扱う）
     */
    public LoanAccount(String accountNumber, double initUsed) {
        // 親クラス Account の balance を「すでに利用した金額」として使う
        super(accountNumber, initUsed);
    }

    /**
     * 現在の会員種別に応じた手数料率を返す。
     * プレミアム会員なら 1%、それ以外は 5%。
     */
    private double feeRate() {
        return isPremium() ? LOAN_ACCOUNT_WITHDRAW_PREMIUM_FEE : LOAN_ACCOUNT_WITHDRAW_FEE;
    }

    /**
     * 与えられた金額に対する手数料額を計算する。
     */
    public double getFee(double amount) {
        return amount * feeRate();
    }

    /**
     * 出金（借入）要求が許可されるかどうかを判定する。
     * 条件:
     * - 金額が 0 以下の場合は不可
     * - 手数料を含めた利用額が限度額を超えないこと
     * - 処理後の「残り利用可能枠」が 50,000 以上残っていること
     */
    @Override
    public boolean isAccepted(double amount) {
        if (amount <= 0) return false;

        double fee = getFee(amount);
        double usedAmountAfter = getBalance() + amount + fee;        // 出金後の利用額

        // 利用額が限度額を超える場合は不可
        if (usedAmountAfter > LOAN_ACCOUNT_MAX_BALANCE) return false;

        double remainingLimit = LOAN_ACCOUNT_MAX_BALANCE - usedAmountAfter;
        // 残り利用可能枠が 50,000 未満なら不可
        return remainingLimit >= 50_000;
    }

    /**
     * 出金（借入）処理を実行する。
     * - 許可される場合: 利用額(balance)を増やし、成功の取引履歴を追加し、レシートを出力する。
     * - 許可されない場合: 失敗の取引履歴のみを追加する。
     */
    @Override
    public boolean withdraw(double amount) {
        boolean accepted = isAccepted(amount);

        if (accepted) {
            double fee = getFee(amount);
            // balance は「すでに利用した金額」として扱うため、借入＋手数料分を加算
            setBalance(getBalance() + amount + fee);

            transactions.add(new Transaction(getAccountNumber(), amount, true));
            log(amount);
        } else {
            transactions.add(new Transaction(getAccountNumber(), amount, false));
        }
        return accepted;
    }

    /**
     * ローン出金レシートをコンソールに表示する。
     */
    @Override
    public void log(double amount) {
        double fee = getFee(amount);
        double usedAmount = getBalance();
        double remainingLimit = LOAN_ACCOUNT_MAX_BALANCE - usedAmount;

        System.out.println("+--------------------------+");
        System.out.println("|   　ローン出金レシート    　|");
        System.out.println("+--------------------------+");
        System.out.printf("| 口座番号 : %s%n", getAccountNumber());
        System.out.printf("| 出金額   : %,.0f%n", amount);
        System.out.printf("| 手数料   : %,.0f (%.2f%%)%n", fee, feeRate() * 100);
        System.out.printf("| 残り利用可能枠 : %,.0f%n", remainingLimit);
        System.out.println("+--------------------------+");
    }

    /**
     * このローン口座に紐づく取引履歴一覧を返す。
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
