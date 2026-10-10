import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';

class AccountsScreen extends StatelessWidget {
  const AccountsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Net Worth & Accounts", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.creamBackground,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Net Worth Hero
            MoneyFlowClayCard(
              backgroundColor: Colors.white,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "TOTAL NET LIQUIDITY",
                        style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate),
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: AppColors.sageLight,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: const Text("+3.8% this month", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.sagePrimary)),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    "\$16,270.50",
                    style: TextStyle(fontSize: 34, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal),
                  ),
                  const SizedBox(height: 14),
                  ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.sageContainer,
                      foregroundColor: Colors.white,
                      minimumSize: const Size.fromHeight(48),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
                    ),
                    onPressed: () {},
                    icon: const Icon(Icons.swap_horiz),
                    label: const Text("Transfer Between Accounts", style: TextStyle(fontWeight: FontWeight.bold)),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Accounts List Section
            const Text("CASH & CHECKING", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate)),
            const SizedBox(height: 10),

            MoneyFlowClayCard(
              child: Column(
                children: [
                  _buildAccountRow("Main Checking Account", "Chase •• 4821", "\$8,450.20", Icons.account_balance_wallet, AppColors.sageContainer),
                  const Divider(),
                  _buildAccountRow("High Yield Savings", "Marcus •• 9012", "\$16,400.20", Icons.savings, AppColors.blueContainer),
                  const Divider(),
                  _buildAccountRow("Emergency Fund", "Ally •• 3341", "\$5,000.00", Icons.shield, AppColors.peachContainer),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAccountRow(String name, String sub, String balance, IconData icon, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6.0),
      child: Row(
        children: [
          CircleAvatar(
            backgroundColor: color,
            child: Icon(icon, color: Colors.white, size: 20),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                Text(sub, style: const TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
              ],
            ),
          ),
          Text(balance, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
        ],
      ),
    );
  }
}
