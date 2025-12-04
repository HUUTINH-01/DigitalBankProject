package asm03.models;

/**
 * 出金取引のレシート（明細）を出力するためのインターフェース。
 */
public interface ReportService {

    /**
     * 出金金額を受け取り、レシート情報を表示・出力する。
     *
     * @param amount 出金額
     */
    void log(double amount);
}
