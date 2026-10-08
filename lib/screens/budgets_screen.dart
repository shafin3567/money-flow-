import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';

class BudgetsScreen extends StatelessWidget {
  const BudgetsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Budgets Planner", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.creamBackground,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0),
        child: Column(
          children: [
            const MoneyFlowClayCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("MONTHLY BUDGET CAP", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.mutedSlate)),
                  SizedBox(height: 6),
                  Text("\$3,500.00", style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal)),
                  SizedBox(height: 8),
                  LinearProgressIndicator(value: 0.61, backgroundColor: AppColors.recessedSurface, color: AppColors.sageContainer),
                  SizedBox(height: 6),
                  Text("Spent \$2,140 of \$3,500 • Safe Pace", style: TextStyle(fontSize: 12, color: AppColors.slateStone)),
                ],
              ),
            ),
            const SizedBox(height: 16),
            MoneyFlowClayCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text("Food & Dining", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Text("\$320 / \$400", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    ],
                  ),
                  const SizedBox(height: 8),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: const LinearProgressIndicator(value: 0.80, minHeight: 8, backgroundColor: AppColors.recessedSurface, color: AppColors.peachSecondary),
                  ),
                  const SizedBox(height: 6),
                  const Text("80% spent • \$80 left", style: TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
