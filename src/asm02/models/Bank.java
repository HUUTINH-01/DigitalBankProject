package asm02.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 銀行クラス
 * - 銀行IDと顧客リストを管理するクラス
 */
public class Bank {

    // 銀行ID（UUIDで自動生成）
    private final String id;

    // 顧客リスト
    private List<Customer> customers;

    /**
     * コンストラクタ：銀行IDを生成し、顧客リストを初期化する
     */
    public Bank() {
        this.id = UUID.randomUUID().toString();
        this.customers = new ArrayList<>();
    }

    /**
     * 顧客を追加する。
     * すでに同じmynumber番号の顧客が存在する場合は追加しない。
     *
     * @param customer 追加対象の顧客
     * @return 追加成功なら true、重複などで失敗した場合は false
     */
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("この顧客はすでに登録されています。");
            return false;
        }
        customers.add(customer);
        return true;
    }

    /**
     * 個人番号で顧客を検索する。
     * @param id 検索したい個人番号
     * @return 見つかった顧客、見つからない場合は null
     */
    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(id)) return customer;
        }
        return null;
    }

    /**
     * 氏名（部分一致）で顧客を検索する（大文字・小文字は区別しない）。
     *
     * @param name 氏名キーワード
     * @return 条件に一致した顧客リスト
     */
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers) {
            if (customer.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(customer);
            }
        }
        return result;
    }

    /**
     * 全ての顧客リストを返す。
     */
    public List<Customer> getCustomers() {
        return customers;
    }
}
