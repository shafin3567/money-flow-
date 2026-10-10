import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import 'moneyflow_clay_card.dart';

class DailyBurnAllowanceCard extends StatelessWidget {
  const DailyBurnAllowanceCard({super.key});

  @override
  Widget build(BuildContext context) {
    return MoneyFlowClayCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("DAILY BURN ALLOWANCE", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate)),
                  SizedBox(height: 4),
                  Text("¥10,800 left (~\$69 USD)", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.sageLight,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Text("On Track", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.sagePrimary)),
              ),
            ],
          ),
          const SizedBox(height: 12),
          ClipRRect(
            borderRadius: BorderRadius.circular(10),
            child: const LinearProgressIndicator(
              value: 0.62,
              minHeight: 10,
              backgroundColor: AppColors.recessedSurface,
              color: AppColors.sageContainer,
            ),
          ),
          const SizedBox(height: 6),
          const Text("62% of day budget used", style: TextStyle(fontSize: 11, color: AppColors.slateStone)),
        ],
      ),
    );
  }
}
