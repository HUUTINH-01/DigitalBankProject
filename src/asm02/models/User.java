package asm02.models;

/**
 * 抽象クラス User
 * - 顧客や将来的なユーザー種別（例：社員、管理者）の共通フィールドとバリデーションを管理
 * - 氏名と個人番号（ベトナム国民識別番号）を保持
 */
public abstract class User {

    private String name;        // 氏名
    private String customerId;  // 個人番号（12桁）

    /**
     * コンストラクタ：氏名と個人番号を受け取り初期化する
     */
    public User(String name, String customerId){
        this.name = name;
        setCustomerId(customerId);   // 不正な場合は例外を投げる
    }

    /* ======= 氏名 ======= */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /* ======= 個人番号 ======= */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 個人番号の設定（バリデーション付き）
     */
    public void setCustomerId(String customerId) {
        if (isValidCitizenId(customerId)) {
            this.customerId = customerId;
        } else {
            throw new IllegalArgumentException("個人番号が無効です。正しい12桁を入力してください。");
        }
    }

    /**
     * 個人番号バリデーション
     * - 12桁の数字であること
     * - 先頭3桁が有効な省コードであること
     */
    private boolean isValidCitizenId(String id) {
        if (id == null || !id.matches("\\d{12}")) return false;

        String province = id.substring(0, 3);
        return isValidProvinceCode(province);
    }

    /**
     * 有効なベトナム省コードかどうかを判定
     */
    private boolean isValidProvinceCode(String code) {
        String[] validCodes = {
                "001", "002", "004", "006", "008", "010", "011", "012", "014", "015",
                "017", "019", "020", "022", "024", "025", "026", "027", "030", "031",
                "033", "034", "035", "036", "037", "038", "040", "042", "044", "045",
                "046", "048", "049", "051", "052", "054", "056", "058", "060", "062",
                "064", "066", "067", "068", "070", "072", "074", "075", "077", "079",
                "080", "082", "083", "084", "086", "087", "089", "091", "092", "093",
                "094", "095", "096"
        };
        for (String valid : validCodes) {
            if (valid.equals(code)) return true;
        }
        return false;
    }
}
