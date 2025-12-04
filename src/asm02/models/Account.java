package asm02.models;

public class Account {
    // プレミアム口座と判定するための残高の閾値
    private static final double PREMIUM_THRESHOLD = 10_000_000;

    private String accountNumber; // 口座番号（6桁の数字）
    private double balance;       // 残高

    /**
     * コンストラクタ：口座番号と残高を設定
     */
    public Account(String accountNumber, double balance) {
        setAccountNumber(accountNumber);
        setBalance(balance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * 口座番号を設定する（6桁の数字のみ許可）
     */
    public void setAccountNumber(String accountNumber) {
        if (accountNumber != null && accountNumber.matches("\\d{6}")) {
            this.accountNumber = accountNumber;
        } else {
            throw new IllegalArgumentException("口座番号は6桁の数字でなければなりません。");
        }
    }

    public double getBalance() {
        return balance;
    }

    /**
     * 残高を設定する（マイナスは不可）
     */
    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("残高はマイナスにできません。");
        }
        this.balance = balance;
    }

    /**
     * プレミアム口座かどうか判定する（残高が1000万以上）
     */
    public boolean isPremium() {
        return balance >= PREMIUM_THRESHOLD;
    }

    /**
     * 口座区分を文字列で返す（"PREMIUM" または "NORMAL"）
     */
    public String getAccountType() {
        return isPremium() ? "PREMIUM" : "NORMAL";
    }

    @Override
    public String toString() {
        // デバッグ用の簡易表示。画面表示用のフォーマットは Customer.displayInformation() 側で行う。
        return String.format("%-10s | %,15.2f", accountNumber, balance);
    }
}
