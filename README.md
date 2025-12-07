# 📘 銀行管理システム（Java OOP Project）


---

## 🧾 概要

このプロジェクトは、銀行システムの基本機能を模擬する  
**Java コンソールアプリケーション**です。

機能は 4 つの段階（**ASM01 → ASM02 → ASM03 → ASM04**）に分かれており、  
段階的に機能を拡張する構成になっています。

---

## 📂 ディレクトリ構成

```plaintext
src/
├── asm01/
│   └── Asm01.java
│
├── asm02/
│   ├── Asm02.java
│   └── models/
│       ├── Account.java
│       ├── Bank.java
│       ├── Customer.java
│       └── User.java
│
└── asm03/
    ├── Asm03.java
    └── models/
        ├── DigitalBank.java
        ├── LoanAccount.java
        ├── ReportService.java
        ├── SavingsAccount.java
        ├── Transaction.java
        └── Withdraw.java
```
### 🔹 ASM01 – 個人番号検証モジュール

- 12桁の **個人番号 ID** をコンソールから入力
- 形式チェック（数字・桁数）
- 先頭3桁から **出生地（都道府県コード）** を判定
- 性別コード・年生まれ（19xx / 20xx）を解析
- 入力エラーや無効なコードに対して、メッセージで案内

👉 目的：文字列処理、条件分岐、switch、ループ、入力バリデーション の習得。

---

### 🔹 ASM02 – 顧客 & 口座管理モジュール

- クラス設計：`User`, `Customer`, `Account`, `Bank`
- `個人番号` で顧客を一意に管理
- 新規顧客の追加・重複チェック
- 口座番号（6桁）で `Account` を管理・追加
- 各顧客の **総残高** を計算
- プレミアム判定：
    - いずれかの口座が一定金額以上 ⇒ **PREMIUM**
    - それ以外 ⇒ **NORMAL**
- コンソールで **顧客一覧表示・検索（ID / 名前）** に対応

👉 目的：基本的な OOP（継承・カプセル化）、ArrayList、複数クラス設計 の強化。

---

### 🔹 ASM03 – デジタルバンクシステム

#### クラス追加：
- `DigitalBank`（銀行の中核ロジック）
- `SavingsAccount`（ATM / 普通預金口座）
- `LoanAccount`（ローン口座）
- `Transaction`（取引履歴）
- `Withdraw`（インターフェース：出金処理）
- `ReportService`（インターフェース：レシート印刷）

#### 機能一覧:
- 💳 **ATM 口座を追加** (`SavingsAccount`)
- 💳 **ローン口座を追加** (`LoanAccount`)
- 💸 **出金する** 
- 🧾 **レシート（出金明細）の印刷** (implements `ReportService`)
- 📜 **取引履歴を表示** (`Transaction history`) 

👉 目的：インターフェース、ポリモーフィズム、業務ロジックの実装、モデル分離設計 の習得。 

---

## 👨‍💻 作者情報

- 氏名：**NGUYEN HUU TINH（グエン フウ ティン）**
- Email：**huutinh2701@gmail.com**
- 学校：**ECCコンピューター専門学校**
    - マルチメディア学科システムエンジニアコース

---

## 🎯 学習目的

このプロジェクトを通じて、次の技術を実践・強化しました：

- ✅ オブジェクト指向設計（クラス設計・継承・カプセル化・インターフェース）
- ✅ 文字列処理・バリデーション（ID検証・正規表現）
- ✅ 条件分岐・ループ・例外処理
- ✅ `Scanner` によるコンソール入力処理
- ✅ `ArrayList` による動的データ管理
- ✅ 実務を意識した **コンソール UI** の設計  
