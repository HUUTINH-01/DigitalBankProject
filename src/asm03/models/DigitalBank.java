package asm03.models;

import asm02.models.Bank;
import asm02.models.Customer;
import asm02.models.Account;

import java.util.List;

/**
 * デジタルバンククラス
 * ASM02 の {@link Bank} を継承し、
 * ASM03 向けの便利メソッドを追加したクラスです。
 */
public class DigitalBank extends Bank {

    /**
     * マイナンバー番号から顧客情報を取得する。
     * 内部的には Bank クラスの findCustomerById(...) を利用します。
     *
     * @param customerId mynumber番号（12桁の数字を想定）
     * @return 対応する {@link Customer} が存在すればそのインスタンス、存在しなければ null
     */
    public Customer getCustomerById(String customerId) {
        return findCustomerById(customerId);
    }

    /**
     * 口座番号がシステム内に存在するかどうかを確認する。
     *
     * @param accountNumber 存在チェックを行いたい口座番号
     * @return 少なくとも 1 つの口座がこの番号を持っていれば true、そうでなければ false
     */
    public boolean isAccountExisted(String accountNumber) {
        List<Customer> customers = getCustomers();
        if (customers == null || customers.isEmpty()) {
            return false;
        }

        for (Customer customer : customers) {
            // Customer クラス側の補助メソッドを利用して検索
            if (customer.findAccountByNumber(accountNumber) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 出金処理を行うメソッド。
     * <ul>
     *   <li>個人番号 から顧客を特定する</li>
     *   <li>口座番号から対象口座を取得する</li>
     *   <li>その口座が Withdraw インターフェースに対応していれば、出金処理を実行する</li>
     * </ul>
     *
     * @param customerId    個人番号（12桁の数字を想定）
     * @param accountNumber 出金したい口座番号
     * @param amount        出金額
     * @return 出金に成功した場合は true、失敗した場合は false
     */
    public boolean withdraw(String customerId, String accountNumber, double amount) {
        Customer customer = getCustomerById(customerId);
        if (customer == null) {
            return false;
        }

        Account account = customer.findAccountByNumber(accountNumber);
        if (account == null) {
            return false;
        }

        // Withdraw インターフェースを実装している口座のみ出金可能
        if (account instanceof Withdraw withdrawableAccount) {
            return withdrawableAccount.withdraw(amount);
        }
        return false;
    }
}
