# MoneyFlow — Stitch Implementation Audit & Specification

**Project ID:** `10566049010137516618`
**Title:** MoneyFlow Personal Finance Android App
**Design Theme:** MoneyFlow Clay & Light (`assets/aa4f6c4e035345e7a810c94d33715bbc`)

---

## 1. Executive Summary & Design System Analysis

MoneyFlow's visual identity relies on **claymorphism**, pneumatic elevation, and organic warmth. It rejects traditional cold, flat banking UIs in favor of soft porcelain surfaces, extruded clay pills, recessed wells, and warm pastel highlights.

### Key Visual Tokens & Parameters
* **Primary Accent:** Sage Green (`#42634D` / `#5A7C65`)
* **Secondary Accent:** Soft Warm Peach (`#8D4E2F` / `#FDAB85` / `#F3A27D`)
* **Tertiary Accent:** Breathable Powder Blue (`#325F86` / `#7CA7D1`)
* **Semantic Alert:** Soft Terracotta Coral (`#BA1A1A` / `#E76F51`)
* **Canvas Background:** Rich Ivory (`#FCF9F4` / `#FAF7F2`)
* **Card Surface:** Pure Matte Porcelain White (`#FFFFFF`)
* **Recessed Surface / Wells:** Sunken Soft Gray-Clay (`#F0EDE9` / `#F0ECE4`)
* **Primary Text:** Deep Warm Charcoal (`#1C1C19` / `#2D312E`)
* **Secondary Text:** Slate Stone (`#424843` / `#5C615D`)
* **Font Family:** `Plus Jakarta Sans` across all display, headline, body, and label roles.
* **Border Radius System:** Heavy continuous rounded squircles and pills (`8px`, `16px`, `20px`, `24px`, `32px`, `9999px`).

### Pneumatic Depth Levels
* **Level 0 (Sunken Inset):** `inset 2px 3px 6px rgba(184, 175, 161, 0.45), inset -2px -2px 5px rgba(255, 255, 255, 0.8)`
* **Level 1 (Card Extrusions):** `6px 8px 18px rgba(199, 190, 175, 0.35), -4px -4px 14px rgba(255, 255, 255, 0.95), inset 1px 1px 2px rgba(255, 255, 255, 0.8)`
* **Level 2 (Buttons / Active Navigation):** `8px 12px 24px rgba(184, 175, 161, 0.4), -6px -6px 16px rgba(255, 255, 255, 1.0), inset 1.5px 1.5px 2px rgba(255, 255, 255, 0.9)`
* **Level 3 (Modals / Bottom Sheets):** `0 20px 40px rgba(148, 140, 128, 0.3), 0 -2px 10px rgba(255, 255, 255, 0.8)`

---

## 2. Screen Inventory & Analysis

### Screen 1: Home Dashboard (`d1143b7e23954ab9abef2e95a37c4045`)
* **Header:** Greeting ("Good evening, Alex ✨"), Encrypted Vault status badge ("100% Offline"), notifications, biometrics toggle, profile avatar.
* **Total Balance Card:** Extruded Level 1 clay card with visibility toggle, `$24,850.40`, percentage growth tag (`+5.2%`), sub-breakdown for Checking (`$8,450.20`) and Savings (`$16,400.20`).
* **Quick Actions Row:** 4 extruded pill CTA buttons with circular icon containers:
  1. Add Income (`add`)
  2. Scan Receipt (`qr_code_scanner`)
  3. Transfer Move (`sync_alt`)
  4. Split Bill (`call_split`)
* **Special Widgets:**
  - **Travel Mode Card (Tokyo Summer):** Real-time FX conversion widget (USD to JPY), cached rate timestamp, quick convert action.
  - **Daily Burn Allowance:** Gauge progress ring showing 62% of day budget used.
* **Monthly Burn Rate & Pace Card:** 14-day cycle indicator, safe pace badge, total spent (`$2,140`), cap (`$3,500`), daily safe rate (`$97.14/d`).
* **Budgets Summary Section:** Horizontal/vertical budget cards for Food & Dining and Shopping with progress bars.
* **Savings Goal Card:** Amalfi Coast trip widget (36% achieved, `$720 / $2,000`).
* **Recent Activity Feed:** Transaction list categorized by date with payment method icons and amount badges.
* **Bottom Navigation Bar:** Floating clay pill bar with 5 slots: Home, Transactions, FAB (+), Budgets, More.

### Screen 2: Transactions Ledger (`5aabdf971ea44ec5845bd2ed79fa9057`)
* **Header & Filters:** Search bar with inset Level 0 well, category filter chips (All, Income, Expense, Transfer, Split), date range selector pill.
* **Transaction Groups:** Daily grouped transaction list items with category icons, merchant names, payment method tags, time, and monetary values.

### Screen 3: Accounts & Wallets (`502a8fcfa600472f8f83a4e1e0be136f`)
* **Account Cards Carousel / Stack:** Visual clay cards representing Checking, High Yield Savings, Investment Portfolio, Cash Wallet.
* **Account Action Buttons:** Add Account, Transfer Funds, Reconcile Balance.
* **Account Analytics:** Monthly balance history mini-charts and liquidity ratios.

### Screen 4: Budgets Planner (`839c1ff48fff44528cf5608aa5ed04ff`)
* **Budget Overview Header:** Total monthly budget, total allocated, remaining balance.
* **Category Progress List:** Pneumatic progress bars for Food, Housing, Utilities, Entertainment, Personal. Color-coded status indicators (On Track, Caution, Exceeded).
* **Budget Creation / Edit FAB:** Add new budget limit overlay launcher.

### Screen 5: Analytics & Settings (`f17302a4e33143bcbaf0115441c0b229`)
* **Analytics Tabs:** Income vs Expense breakdown, Cash Flow sankey/bar graphs, Category breakdown donut chart.
* **Settings List:** Security & Biometrics, Currencies & FX, Cloud Backup & Sync, Theme & OLED Mode, Export CSV/JSON.

### Screen 6: New Transaction (`e74bd6e136754a2e9171f858ec101bb7`)
* **Keypad & Inputs:** Tactile dial pad with Level 1 clay buttons, currency switcher, category selector chips, note input field, receipt attachment button, recurring transaction toggle.

### Screen 7: Transaction Details (`c10c348f7b02409ea570472c144d251d`)
* **Detail Card:** Expanded receipt-style view with merchant logo, location map placeholder, category edit, split bill launcher, receipt image preview, transaction delete/edit CTAs.

### Screen 8: Smart Receipt Camera Scanner (`c093c52e60c84f33a9f9dda71b200501`)
* **Viewfinder:** Camera overlay with bounding box scanning guide, flash toggle, auto-capture switch, parsed line-item preview sheet.

### Screen 9: Custom Category Architecture & Rules Engine (`0c4714c503044ac09efd46a5b24eacab`)
* **Category Tree & Auto-Categorization Rules:** Custom icon & color picker for categories, keyword matching rules for automated tagging.

### Screen 10: Shared Household Ledger & Split Tracker (`c1fc2f77464e41d2a2c6446bcb1d8086`)
* **Household Overview:** Owed / Owes balances per member, expense split allocation breakdown, settle-up action flow.

### Screen 11: Savings Goal & Celebration (`1626e3c838704aaea834b5bf722aa30b`)
* **Goal Details:** Interactive savings progress sphere/meter, contribution history, confetti celebration overlay upon milestone achievement.

### Screen 12: OLED Dark Mode Dashboard (`78550b3b4edb45009729ad73a30a9703`)
* **Dark Theme Variant:** Pure black (`#000000`) background with neon-softened clay card extrusions for AMOLED display power saving.

### Screen 13: Android Widgets & Glanceables (`068d2789a2de4b5891668986ac462bb1`)
* **Home Screen Widgets:** Quick Add 2x2 widget, Balance Summary 4x1 widget, Budget Gauge 4x2 widget for Android Glance/RemoteViews.

---

## 3. Implementation Requirements & Screen Mapping Strategy

1. Maintain Jetpack Compose UI architecture.
2. Ensure custom ClaymorphicModifier (`Modifier.clay(...)` / `Modifier.clayCard(...)` / `Modifier.clayButton(...)`) is established and applied across all screen components.
3. Establish exact color, typography, shape, and shadow design tokens in `Theme.kt` and `Color.kt`.
4. Guarantee financial calculation correctness and Room database persistence across all 13 screens.
