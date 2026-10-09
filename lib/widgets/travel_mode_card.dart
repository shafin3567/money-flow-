import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import 'moneyflow_clay_card.dart';

class TravelModeCard extends StatelessWidget {
  const TravelModeCard({super.key});

  @override
  Widget build(BuildContext context) {
    return MoneyFlowClayCard(
      backgroundColor: const Color(0xFFF0EDE9),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Row(
                children: [
                  Icon(Icons.flight_takeoff, color: AppColors.blueTertiary, size: 18),
                  SizedBox(width: 8),
                  Text("Travel Mode: Tokyo Summer", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.blueLight,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Row(
                  children: [
                    Icon(Icons.offline_bolt, size: 12, color: AppColors.blueTertiary),
                    SizedBox(width: 4),
                    Text("Cached 4h ago • 100% Offline", style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: AppColors.blueTertiary)),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.cardBorder),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("1 USD = 156.40 JPY", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    Text("Mid-market FX", style: TextStyle(fontSize: 11, color: AppColors.mutedSlate)),
                  ],
                ),
                ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.blueTertiary,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  onPressed: () {},
                  icon: const Icon(Icons.swap_horiz, size: 16),
                  label: const Text("Quick Convert", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
