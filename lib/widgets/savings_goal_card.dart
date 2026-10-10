import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import 'moneyflow_clay_card.dart';

class SavingsGoalCard extends StatelessWidget {
  const SavingsGoalCard({super.key});

  @override
  Widget build(BuildContext context) {
    return MoneyFlowClayCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Row(
                children: [
                  Icon(Icons.flight_takeoff, color: AppColors.peachSecondary, size: 20),
                  SizedBox(width: 8),
                  Text("Summer Vacation", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.peachContainer,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Text("36% Saved", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.peachSecondary)),
              ),
            ],
          ),
          const SizedBox(height: 6),
          const Text("Target: Amalfi Coast ☀️", style: TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
          const SizedBox(height: 10),
          ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: const LinearProgressIndicator(
              value: 0.36,
              minHeight: 10,
              backgroundColor: AppColors.recessedSurface,
              color: AppColors.peachSecondary,
            ),
          ),
          const SizedBox(height: 8),
          const Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text("\$720 saved of \$2,000", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal)),
              Text("\$1,280 to go", style: TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
            ],
          ),
        ],
      ),
    );
  }
}
