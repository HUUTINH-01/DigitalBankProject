package asm01;

import java.util.Random;
import java.util.Scanner;

public class Asm01 {
    // バナーに表示する情報
    public static final String AUTHOR = "FX73392";   // 作者名
    public static final String VERSION = "v1.0.0";   // バージョン

    // 共通で使うリソース
    static Scanner input = new Scanner(System.in);   // コンソール入力用 Scanner
    static Random rand = new Random();               // 乱数生成用

    /*
     * main: プログラムのライフサイクル
     * - バナー表示 → メインメニューのループ → 処理の振り分け
     * - try–finally で最後に必ず Scanner を close する
     * - 入力ルール: 各ステップで最大3回まで。3回連続で失敗したら戻る / 終了
     */
    public static void main(String[] args) {
        try {
            int choice;
            printMainTitle();                     // タイトルを表示
            do {
                choice = showMainMenu();          // 0/1のみ有効。3回失敗したら 0 を返す
                switch (choice) {
                    case 1:                       // 個人番号(CCCD)メニューへ
                        if (selectAuthenticationMode()) {          // EASY/HARD 認証。false → 戻る
                            String citizenId = enterValidCitizenId(); // 有効な個人番号を入力。null → 戻る
                            if (citizenId == null) break;          // 3回失敗したらメインメニューに戻る
                            handleSubMenu(citizenId);              // 個人番号に基づいてサブメニュー処理
                        }
                        break;
                    case 0:                       // 終了
                        System.out.println("ご利用いただき、ありがとうございました。");
                        break;
                }
            } while (choice != 0);                // 0 が選ばれるまでループ
        } finally {
            input.close();                        // Scanner を閉じてリソースリーク防止
        }
    }

    /*
     * printMainTitle: 作者名とバージョンを含むバナータイトルを表示
     */
    public static void printMainTitle() {
        System.out.println("---------------+-----------------");
        // %n は \n の代わり（プラットフォーム依存を避ける）
        System.out.printf("| デジタルバンク | %s@%s |%n", AUTHOR, VERSION);
        System.out.println("---------------+-----------------");
    }

    /*
     * selectAuthenticationMode: 認証モードの選択 (0/1/2)
     * - 1: EASY (3桁数字), 2: HARD (6文字英数字), 0: 戻る
     * - 型 / 範囲エラーを最大3回まで許容。3回失敗したら false を返して戻る
     */
    public static boolean selectAuthenticationMode() {
        int attempts = 0;                         // 入力エラー回数カウンタ
        while (attempts < 3) {                    // 最大3回まで
            try {
                System.out.println("\n認証モードを選択してください：");
                System.out.println("1. EASY（3桁の数字）");
                System.out.println("2. HARD（6文字の英数字）");
                System.out.println("0. 戻る");
                System.out.print("選択番号： ");
                int mode = input.nextInt();       // 数値以外の場合 InputMismatchException が発生
                input.nextLine();                 // バッファに残った改行を消す

                switch (mode) {
                    case 1:
                        return basicAuthentication();   // EASY 認証（内部で3回まで）
                    case 2:
                        return advancedAuthentication(); // HARD 認証（内部で3回まで）
                    case 0:
                        System.out.println("認証をキャンセルしてメインメニューに戻ります。");
                        return false;              // 戻る
                    default:
                        System.out.println("無効な選択です。");
                        attempts++;                // 範囲外のときカウントアップ
                }
            } catch (Exception e) {                // 型エラーなど
                System.out.println("エラー：数字を入力してください。");
                input.nextLine();                  // バッファの不正トークンをクリア
                attempts++;                        // 例外時もカウントアップ
            }
        }
        System.out.println("3回連続で入力ミスが発生しました。メインメニューに戻ります。");
        return false;                              // 戻る
    }

    /*
     * basicAuthentication: EASY 認証（3桁数字）
     * - [100..999] の乱数コードを生成し、最大3回まで入力させる
     * - 正解なら true、3回ミスしたら false（戻る）
     */
    public static boolean basicAuthentication() {
        int code = 100 + rand.nextInt(900);       // 3桁の乱数を生成
        System.out.println("\n>>> 認証コード: " + code);
        int attempts = 0;                         // 入力ミス回数
        while (true) {
            try {
                System.out.print("認証コードを入力してください： ");
                int userCode = input.nextInt();   // 数字以外なら例外
                input.nextLine();                 // バッファクリア
                if (userCode == code) {           // 一致チェック
                    System.out.println(">>> 認証に成功しました！ <<<");
                    return true;
                }
                System.out.println("コードが違います。もう一度入力してください。");
                if (++attempts >= 3) {            // インクリメントしてから判定
                    System.out.println("3回連続で失敗しました。メニューに戻ります。");
                    return false;
                }
            } catch (Exception e) {               // 文字などを入力した場合
                System.out.println("エラー：数字を入力してください。");
                input.nextLine();                 // バッファクリア
                if (++attempts >= 3) {
                    System.out.println("3回連続で失敗しました。メニューに戻ります。");
                    return false;
                }
            }
        }
    }

    /*
     * advancedAuthentication: HARD 認証（6文字英数字）
     * - 長さ6のランダム文字列（A-Z, a-z, 0-9）を生成
     * - 最大3回まで入力可能。正解 → true、3回ミス → false（戻る）
     */
    public static boolean advancedAuthentication() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(6);  // String 連結より効率的
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rand.nextInt(62))); // 利用可能文字は62種類
        }
        String authCode = sb.toString();          // 認証コード
        System.out.println("\n>>> 認証コード: " + authCode);

        int attempts = 0;                         // 入力ミス回数
        while (true) {
            try {
                System.out.print("認証コードを入力してください： ");
                String userCode = input.next();   // 空白まで読み込み
                input.nextLine();                 // バッファクリア
                if (authCode.equals(userCode)) {  // 大文字小文字も含めて一致判定
                    System.out.println(">>> 認証に成功しました！ <<<");
                    return true;
                }
                System.out.println("コードが違います。もう一度入力してください。");
                if (++attempts >= 3) {
                    System.out.println("3回連続で失敗しました。メニューに戻ります。");
                    return false;
                }
            } catch (Exception e) {               // ほぼ起きないが念のため
                System.out.println("入力エラーが発生しました。もう一度やり直してください。");
                input.nextLine();                 // バッファクリア
                if (++attempts >= 3) {
                    System.out.println("3回連続で失敗しました。メニューに戻ります。");
                    return false;
                }
            }
        }
    }

    /*
     * showMainMenu: メインメニューの表示と入力
     * - 有効値は 0（終了）または 1（個人番号入力）
     * - 型/範囲エラーを最大3回まで許容。3回失敗したら 0 を返す
     */
    public static int showMainMenu() {
        int attempts = 0;                         // 入力ミス回数
        while (attempts < 3) {                    // 最大3回
            try {
                System.out.println("     機能を選択してください");
                System.out.println(" 1. 個人番号の入力");
                System.out.println(" 0. 終了");
                System.out.println("---------------+-----------------");
                System.out.print("選択番号： ");
                int choice = input.nextInt();     // 数値以外なら例外
                input.nextLine();                 // バッファクリア
                if (choice == 0 || choice == 1) { // 0/1 のみ有効
                    return choice;
                }
                System.out.println("無効な選択です。");
                attempts++;                       // 範囲外
            } catch (Exception e) {               // 型エラー
                System.out.println("エラー：数字を入力してください。");
                input.nextLine();                 // バッファクリア
                attempts++;                       // カウントアップ
            }
        }
        System.out.println("3回連続で入力ミスが発生しました。プログラムを終了します。");
        return 0;                                 // 終了 / 戻る
    }

    /*
     * showSubMenu: 個人番号(CCCD)に関するサブメニュー
     * - 有効値: 0..3。型/範囲エラー最大3回 → 0 を返して戻る
     */
    public static int showSubMenu() {
        int attempts = 0;                         // 入力ミス回数
        while (attempts < 3) {
            try {
                System.out.println("\n-----------------------------------");
                System.out.println(" 1. 出生地（省・市）を確認する");
                System.out.println(" 2. 生年月日・性別を確認する");
                System.out.println(" 3. 個人識別コード（6桁）を確認する");
                System.out.println(" 0. 戻る");
                System.out.println("-----------------------------------");
                System.out.print("選択番号： ");
                int choice = input.nextInt();     // 数値入力
                input.nextLine();                 // バッファクリア
                if (choice >= 0 && choice <= 3) { // 0..3 のみ有効
                    return choice;
                }
                System.out.println("無効な選択です。");
                attempts++;
            } catch (Exception e) {
                System.out.println("エラー：数字を入力してください。");
                input.nextLine();                 // バッファクリア
                attempts++;
            }
        }
        System.out.println("3回連続で入力ミスが発生しました。メニューに戻ります。");
        return 0;                                 // 戻る
    }

    /*
     * enterValidCitizenId: 有効な個人番号を入力させる
     * - 条件: 数字12桁、かつ先頭3桁が有効な省コード
     * - 有効な場合はその文字列を返す。3回失敗した場合は null を返す（戻る）
     */
    private static String enterValidCitizenId() {
        int attempts = 0;                         // 入力ミス回数
        while (attempts < 3) {
            try {
                System.out.print("個人番号（12桁数字）を入力してください： ");
                String citizenId = input.next();  // 空白なしで1トークン読み込み
                input.nextLine();                 // バッファクリア
                if (citizenId != null && citizenId.matches("\\d{12}")) { // 12桁数字かどうか
                    String provinceName = getProvinceName(citizenId);    // 省コードも同時にチェック
                    if (!"Mã tỉnh không hợp lệ!".equals(provinceName)) {
                        System.out.println(">>> 個人番号は有効です。 <<<");
                        return citizenId;         // OK
                    }
                    System.out.println("省コードが無効です。");
                } else {
                    System.out.println("個人番号は12桁の数字でなければなりません。");
                }
                attempts++;
            } catch (Exception e) {
                System.out.println("入力エラーが発生しました。もう一度やり直してください。");
                input.nextLine();                 // バッファクリア
                attempts++;
            }
        }
        System.out.println("3回連続で入力ミスが発生しました。メニューに戻ります。");
        return null;                              // 戻る
    }

    /*
     * handleSubMenu: 有効な個人番号に対してサブメニュー機能を実行
     * - 出生地・性別＋生年・個人識別コード(6桁)を表示
     */
    private static void handleSubMenu(String citizenId) {
        String personalIdCode = citizenId.substring(6); // 後ろ6桁（個人識別コード）
        int subChoice;
        do {
            subChoice = showSubMenu();           // 0..3（内部で最大3回チェック済み）
            switch (subChoice) {
                case 1:
                    System.out.println("出生地： " + getProvinceName(citizenId)); // 先頭3桁
                    break;
                case 2:
                    System.out.println(getGenderAndBirthYear(citizenId));           // 4〜6桁目
                    break;
                case 3:
                    System.out.println("個人識別コード（6桁）： " + personalIdCode);         // 後ろ6桁
                    break;
                case 0:
                    break;                      // サブメニューを抜ける
            }
        } while (subChoice != 0);               // 0 が選択されるまでループ
    }

    /*
     * getProvinceName: 個人番号の先頭3桁から省/市の名前を返す
     * - 入力はすでに12桁の数字である前提
     * - 対応しないコードの場合は "Mã tỉnh không hợp lệ!" を返す（既存仕様を維持）
     */
    public static String getProvinceName(String citizenId) {
        String provinceCode = citizenId.substring(0, 3); // 先頭3桁
        switch (provinceCode) {
            case "001": return "Hà Nội";
            case "002": return "Hà Giang";
            case "004": return "Cao Bằng";
            case "006": return "Bắc Kạn";
            case "008": return "Tuyên Quang";
            case "010": return "Lào Cai";
            case "011": return "Điện Biên";
            case "012": return "Lai Châu";
            case "014": return "Sơn La";
            case "015": return "Yên Bái";
            case "017": return "Hoà Bình";
            case "019": return "Thái Nguyên";
            case "020": return "Lạng Sơn";
            case "022": return "Quảng Ninh";
            case "024": return "Bắc Giang";
            case "025": return "Phú Thọ";
            case "026": return "Vĩnh Phúc";
            case "027": return "Bắc Ninh";
            case "030": return "Hải Dương";
            case "031": return "Hải Phòng";
            case "033": return "Hưng Yên";
            case "034": return "Thái Bình";
            case "035": return "Hà Nam";
            case "036": return "Nam Định";
            case "037": return "Ninh Bình";
            case "038": return "Thanh Hoá";
            case "040": return "Nghệ An";
            case "042": return "Hà Tĩnh";
            case "044": return "Quảng Bình";
            case "045": return "Quảng Trị";
            case "046": return "Thừa Thiên Huế";
            case "048": return "Đà Nẵng";
            case "049": return "Quảng Nam";
            case "051": return "Quảng Ngãi";
            case "052": return "Bình Định";
            case "054": return "Phú Yên";
            case "056": return "Khánh Hoà";
            case "058": return "Ninh Thuận";
            case "060": return "Bình Thuận";
            case "062": return "Kon Tum";
            case "064": return "Gia Lai";
            case "066": return "Đắk Lắk";
            case "067": return "Đắk Nông";
            case "068": return "Lâm Đồng";
            case "070": return "Bình Phước";
            case "072": return "Tây Ninh";
            case "074": return "Bình Dương";
            case "075": return "Đồng Nai";
            case "077": return "Bà Rịa - Vũng Tàu";
            case "079": return "TP. Hồ Chí Minh";
            case "080": return "Long An";
            case "082": return "Tiền Giang";
            case "083": return "Bến Tre";
            case "084": return "Trà Vinh";
            case "086": return "Vĩnh Long";
            case "087": return "Đồng Tháp";
            case "089": return "An Giang";
            case "091": return "Kiên Giang";
            case "092": return "Cần Thơ";
            case "093": return "Hậu Giang";
            case "094": return "Sóc Trăng";
            case "095": return "Bạc Liêu";
            case "096": return "Cà Mau";
            default: return "都道府県コードが無効です!"; // 仕様そのまま維持
        }
    }

    /*
     * getGenderAndBirthYear: 個人番号から性別＋生年を判定
     * - 4桁目の数字（インデックス3）が性別・世紀コード(0..9)
     * - 次の2桁（インデックス4..5）が生年の下2桁
     */
    public static String getGenderAndBirthYear(String citizenId) {
        char genderCode = citizenId.charAt(3);     // 性別・世紀コード
        String year = citizenId.substring(4, 6);   // 生年の下2桁
        switch (genderCode) {
            case '0': return "性別: 男性 | 生年: 19" + year; // 1900-1999（男性）
            case '1': return "性別: 女性 | 生年: 19" + year; // 1900-1999（女性）
            case '2': return "性別: 男性 | 生年: 20" + year; // 2000-2099（男性）
            case '3': return "性別: 女性 | 生年: 20" + year; // 2000-2099（女性）
            case '4': return "性別: 男性 | 生年: 21" + year;
            case '5': return "性別: 女性 | 生年: 21" + year;
            case '6': return "性別: 男性 | 生年: 22" + year;
            case '7': return "性別: 女性 | 生年: 22" + year;
            case '8': return "性別: 男性 | 生年: 23" + year;
            case '9': return "性別: 女性 | 生年: 23" + year;
            default:  return "性別コードが不正です。";          // 0..9 以外
        }
    }
}
