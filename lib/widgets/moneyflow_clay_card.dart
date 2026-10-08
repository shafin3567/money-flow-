import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';

class MoneyFlowClayCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final double borderRadius;
  final Color backgroundColor;
  final VoidCallback? onTap;

  const MoneyFlowClayCard({
    super.key,
    required this.child,
    this.padding,
    this.borderRadius = 24.0,
    this.backgroundColor = AppColors.porcelainWhite,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(borderRadius),
        border: Border.all(color: AppColors.cardBorder, width: 1.0),
        boxShadow: const [
          BoxShadow(
            color: AppColors.clayShadow,
            offset: Offset(4, 6),
            blurRadius: 16,
            spreadRadius: 0,
          ),
          BoxShadow(
            color: Colors.white,
            offset: Offset(-3, -3),
            blurRadius: 10,
            spreadRadius: 0,
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        borderRadius: BorderRadius.circular(borderRadius),
        child: InkWell(
          borderRadius: BorderRadius.circular(borderRadius),
          onTap: onTap,
          child: Padding(
            padding: padding ?? const EdgeInsets.all(18.0),
            child: child,
          ),
        ),
      ),
    );
  }
}
