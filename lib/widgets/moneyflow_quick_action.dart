import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';

class MoneyFlowQuickAction extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;
  final Color iconBgColor;

  const MoneyFlowQuickAction({
    super.key,
    required this.icon,
    required this.label,
    required this.onTap,
    this.iconBgColor = AppColors.sageContainer,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 54,
            height: 54,
            decoration: BoxDecoration(
              color: iconBgColor,
              shape: BoxShape.circle,
              boxShadow: const [
                BoxShadow(
                  color: AppColors.clayShadow,
                  offset: Offset(3, 5),
                  blurRadius: 10,
                ),
              ],
            ),
            child: Icon(icon, color: Colors.white, size: 24),
          ),
          const SizedBox(height: 6),
          Text(
            label,
            style: const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w600,
              color: AppColors.warmCharcoal,
            ),
          ),
        ],
      ),
    );
  }
}
