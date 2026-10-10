import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';

class AnalyticsScreen extends StatelessWidget {
  const AnalyticsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Analytics & Insights", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.creamBackground,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            MoneyFlowClayCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text("CASH FLOW RATIO", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate)),
                  const SizedBox(height: 6),
                  const Text("74.2% Savings Rate", style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: AppColors.sagePrimary)),
                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _buildMetric("Income", "\$5,400.00", AppColors.sagePrimary),
                      _buildMetric("Expenses", "\$1,390.00", AppColors.terracottaCoral),
                      _buildMetric("Net Flow", "+\$4,010.00", AppColors.blueTertiary),
                    ],
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetric(String label, String value, Color color) {
    return Column(
      children: [
        Text(label, style: const TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
        const SizedBox(height: 4),
        Text(value, style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: color)),
      ],
    );
  }
}
