import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import 'moneyflow_clay_card.dart';

class BurnRateCard extends StatelessWidget {
  const BurnRateCard({super.key});

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
                  Icon(Icons.speed, color: AppColors.peachSecondary, size: 18),
                  SizedBox(width: 6),
                  Text("Monthly Burn Rate & Pace", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.peachContainer,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Text("14 days remaining", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.peachSecondary)),
              ),
            ],
          ),
          const SizedBox(height: 14),
          const Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              Column(
                children: [
                  Text("Spent", style: TextStyle(fontSize: 11, color: AppColors.mutedSlate)),
                  SizedBox(height: 2),
                  Text("\$2,140", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                ],
              ),
              Column(
                children: [
                  Text("Cap", style: TextStyle(fontSize: 11, color: AppColors.mutedSlate)),
                  SizedBox(height: 2),
                  Text("\$3,500", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                ],
              ),
              Column(
                children: [
                  Text("Daily Safe", style: TextStyle(fontSize: 11, color: AppColors.mutedSlate)),
                  SizedBox(height: 2),
                  Text("\$97.14/d", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.sagePrimary)),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}
