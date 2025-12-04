package asm03.models;

/**
 * 出金処理のためのインターフェース。
 * 口座からの出金可否チェックと、実際の出金処理を定義します。
 */
public interface Withdraw {

    /**
     * 出金処理を実行する。
     *
     * @param amount 出金額
     * @return 出金に成功した場合は true、失敗した場合は false
     */
    boolean withdraw(double amount);

    /**
     * 指定された金額の出金が可能かどうかを判定する。
     *
     * @param amount 出金額
     * @return 出金可能であれば true、不可能であれば false
     */
    boolean isAccepted(double amount);
}
