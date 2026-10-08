import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'app_colors.dart';

class AppTheme {
  static ThemeData get lightTheme {
    final textTheme = GoogleFonts.plusJakartaSansTextTheme();

    return ThemeData(
      useMaterial3: true,
      scaffoldBackgroundColor: AppColors.creamBackground,
      colorScheme: const ColorScheme.light(
        primary: AppColors.sagePrimary,
        primaryContainer: AppColors.sageContainer,
        secondary: AppColors.peachSecondary,
        secondaryContainer: AppColors.peachContainer,
        tertiary: AppColors.blueTertiary,
        surface: AppColors.porcelainWhite,
        error: AppColors.terracottaCoral,
        onPrimary: Colors.white,
        onSurface: AppColors.warmCharcoal,
        onSurfaceVariant: AppColors.slateStone,
      ),
      textTheme: textTheme.copyWith(
        displayLarge: GoogleFonts.plusJakartaSans(
          fontSize: 34,
          fontWeight: FontWeight.bold,
          color: AppColors.warmCharcoal,
          letterSpacing: -0.8,
        ),
        headlineLarge: GoogleFonts.plusJakartaSans(
          fontSize: 26,
          fontWeight: FontWeight.w700,
          color: AppColors.warmCharcoal,
        ),
        headlineMedium: GoogleFonts.plusJakartaSans(
          fontSize: 20,
          fontWeight: FontWeight.w600,
          color: AppColors.warmCharcoal,
        ),
        bodyLarge: GoogleFonts.plusJakartaSans(
          fontSize: 16,
          fontWeight: FontWeight.w400,
          color: AppColors.warmCharcoal,
        ),
        bodyMedium: GoogleFonts.plusJakartaSans(
          fontSize: 14,
          fontWeight: FontWeight.w400,
          color: AppColors.slateStone,
        ),
        labelLarge: GoogleFonts.plusJakartaSans(
          fontSize: 14,
          fontWeight: FontWeight.w600,
          color: AppColors.warmCharcoal,
        ),
        labelSmall: GoogleFonts.plusJakartaSans(
          fontSize: 10,
          fontWeight: FontWeight.w700,
          color: AppColors.mutedSlate,
          letterSpacing: 0.8,
        ),
      ),
    );
  }

  static ThemeData get darkTheme {
    final textTheme = GoogleFonts.plusJakartaSansTextTheme();

    return ThemeData(
      useMaterial3: true,
      scaffoldBackgroundColor: AppColors.darkBackground,
      colorScheme: const ColorScheme.dark(
        primary: AppColors.sageLight,
        surface: AppColors.darkSurface,
        onSurface: AppColors.darkTextPrimary,
        onSurfaceVariant: AppColors.darkTextSecondary,
      ),
      textTheme: textTheme.copyWith(
        displayLarge: GoogleFonts.plusJakartaSans(
          fontSize: 34,
          fontWeight: FontWeight.bold,
          color: AppColors.darkTextPrimary,
        ),
        headlineLarge: GoogleFonts.plusJakartaSans(
          fontSize: 26,
          fontWeight: FontWeight.w700,
          color: AppColors.darkTextPrimary,
        ),
        bodyLarge: GoogleFonts.plusJakartaSans(
          fontSize: 16,
          color: AppColors.darkTextPrimary,
        ),
        bodyMedium: GoogleFonts.plusJakartaSans(
          fontSize: 14,
          color: AppColors.darkTextSecondary,
        ),
      ),
    );
  }
}
