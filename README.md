# MoneyFlow — Stitch Personal Finance Android App (Flutter)

MoneyFlow is an offline-first, claymorphic personal finance Android application built in Flutter, matching the official Stitch design specification (`projects/10566049010137516618`).

## 🚀 Opening in Android Studio

This project is structured as a standard Flutter application and can be opened directly in **Android Studio**:

1. Launch **Android Studio**.
2. Select **Open** and choose the repository root directory (`/app` or repository base).
3. Android Studio will automatically detect the Flutter project (`pubspec.yaml`) and the Android module (`android/`).
4. Ensure the Flutter & Dart plugins are installed in Android Studio.
5. Click **Run** or press `Shift + F10` to launch MoneyFlow on an Android Emulator or connected device.

## 🛠 Features & Architecture

- **100% Offline Vault:** All accounts, transactions, and budgets are persisted locally in SQLite (`sqflite`). Zero external network dependencies.
- **Claymorphism Design System:** Soft porcelain surfaces, extruded clay cards, pneumatic depth, and custom bottom floating navigation.
- **Financial Integrity:** Integer minor units used for exact money calculations ($1.00 = 100 cents) without floating point imprecision.

## 🧪 Testing & Verification

Run tests and static analysis from terminal:

```bash
flutter pub get
flutter test
flutter analyze
```
