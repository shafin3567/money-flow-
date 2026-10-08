import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';

class MoreScreen extends StatelessWidget {
  const MoreScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Settings & More", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.creamBackground,
        elevation: 0,
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
        children: [
          MoneyFlowClayCard(
            child: Column(
              children: [
                _buildSettingTile(Icons.security, "Security & Biometrics", "Passcode & Fingerprint"),
                const Divider(),
                _buildSettingTile(Icons.currency_exchange, "Currencies & Rates", "USD (Primary)"),
                const Divider(),
                _buildSettingTile(Icons.cloud_sync, "Backup & Local Sync", "100% Offline Vault"),
                const Divider(),
                _buildSettingTile(Icons.dark_mode_outlined, "OLED Dark Theme", "Automatic / System"),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSettingTile(IconData icon, String title, String subtitle) {
    return ListTile(
      leading: CircleAvatar(
        backgroundColor: AppColors.recessedSurface,
        child: Icon(icon, color: AppColors.warmCharcoal, size: 20),
      ),
      title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
      subtitle: Text(subtitle, style: const TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
      trailing: const Icon(Icons.chevron_right, color: AppColors.mutedSlate),
      onTap: () {},
    );
  }
}
