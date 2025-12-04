package asm03.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 出金取引クラス。
 * 口座の出金履歴を保存するために使用します。
 */
public class Transaction {

    // 取引ID生成用の文字セット
    private static final String ID_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 8;

    private final String id;            // 取引ID
    private final String accountNumber; // 対象口座番号
    private final double amount;        // 取引金額
    private final String time;          // 取引日時（yyyy-MM-dd HH:mm:ss）
    private final boolean status;       // 取引結果: true = 成功, false = 失敗

    /**
     * 新しい取引インスタンスを生成するコンストラクタ。
     *
     * @param accountNumber 対象口座番号
     * @param amount        取引金額
     * @param status        取引結果（成功 / 失敗）
     */
    public Transaction(String accountNumber, double amount, boolean status) {
        this.id = generateRandomId();
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.status = status;
    }

    /**
     * 固定長のランダムな取引IDを生成する。
     */
    private String generateRandomId() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(ID_CHARSET.length());
            builder.append(ID_CHARSET.charAt(index));
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %,.0f | %s | %s",
                id,
                accountNumber,
                amount,
                time,
                status ? "成功" : "失敗"
        );
    }
}
