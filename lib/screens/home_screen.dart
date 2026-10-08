import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';
import '../widgets/moneyflow_quick_action.dart';
import '../services/database_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<Map<String, dynamic>> _transactions = [];

  @override
  void initState() {
    super.initState();
    _loadTransactions();
  }

  Future<void> _loadTransactions() async {
    final txs = await DatabaseService.instance.getTransactions();
    setState(() {
      _transactions = txs;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(
                              color: AppColors.recessedSurface,
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: const Row(
                              children: [
                                Icon(Icons.lock, size: 12, color: AppColors.sagePrimary),
                                SizedBox(width: 4),
                                Text(
                                  "100% Offline",
                                  style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.slateStone),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 4),
                      const Text(
                        "Good evening, Alex ✨",
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal),
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      IconButton(onPressed: () {}, icon: const Icon(Icons.notifications_outlined)),
                      IconButton(onPressed: () {}, icon: const Icon(Icons.fingerprint)),
                    ],
                  )
                ],
              ),
              const SizedBox(height: 16),

              // Total Balance Card
              MoneyFlowClayCard(
                backgroundColor: AppColors.porcelainWhite,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          "TOTAL BALANCE",
                          style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                          decoration: BoxDecoration(
                            color: AppColors.sageLight,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: const Text(
                            "+5.2%",
                            style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.sagePrimary),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    const Text(
                      "\$24,850.40",
                      style: TextStyle(fontSize: 34, fontWeight: FontWeight.bold, color: AppColors.warmCharcoal),
                    ),
                    const SizedBox(height: 14),
                    const Row(
                      children: [
                        Expanded(
                          child: Row(
                            children: [
                              Icon(Icons.account_balance_wallet_outlined, size: 16, color: AppColors.sagePrimary),
                              SizedBox(width: 6),
                              Text("Checking: \$8,450.20", style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                            ],
                          ),
                        ),
                        Expanded(
                          child: Row(
                            children: [
                              Icon(Icons.savings_outlined, size: 16, color: AppColors.blueTertiary),
                              SizedBox(width: 6),
                              Text("Savings: \$16,400.20", style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                            ],
                          ),
                        ),
                      ],
                    )
                  ],
                ),
              ),
              const SizedBox(height: 20),

              // Quick Actions
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  MoneyFlowQuickAction(
                    icon: Icons.add,
                    label: "Add Income",
                    iconBgColor: AppColors.sageContainer,
                    onTap: () {},
                  ),
                  MoneyFlowQuickAction(
                    icon: Icons.qr_code_scanner,
                    label: "Scan Receipt",
                    iconBgColor: AppColors.peachContainer,
                    onTap: () {},
                  ),
                  MoneyFlowQuickAction(
                    icon: Icons.sync_alt,
                    label: "Transfer",
                    iconBgColor: AppColors.blueContainer,
                    onTap: () {},
                  ),
                  MoneyFlowQuickAction(
                    icon: Icons.call_split,
                    label: "Split Bill",
                    iconBgColor: AppColors.slateStone,
                    onTap: () {},
                  ),
                ],
              ),
              const SizedBox(height: 20),

              // Recent Activity Header
              const Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    "RECENT ACTIVITY",
                    style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 1.2, color: AppColors.mutedSlate),
                  ),
                  Text(
                    "View All",
                    style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.blueTertiary),
                  ),
                ],
              ),
              const SizedBox(height: 10),

              // Transactions List
              ListView.separated(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: _transactions.length,
                separatorBuilder: (context, index) => const SizedBox(height: 8),
                itemBuilder: (context, index) {
                  final tx = _transactions[index];
                  final isIncome = tx['type'] == 'income';
                  final amountText = isIncome
                      ? "+\$${(tx['amount'] / 100).toStringAsFixed(2)}"
                      : "-\$${(tx['amount'] / 100).toStringAsFixed(2)}";

                  return MoneyFlowClayCard(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                    borderRadius: 16,
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: isIncome ? AppColors.sageLight : AppColors.peachContainer,
                          child: Icon(
                            isIncome ? Icons.arrow_downward : Icons.shopping_bag_outlined,
                            color: isIncome ? AppColors.sagePrimary : AppColors.peachSecondary,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(tx['title'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                              Text(tx['category'], style: const TextStyle(fontSize: 12, color: AppColors.mutedSlate)),
                            ],
                          ),
                        ),
                        Text(
                          amountText,
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                            color: isIncome ? AppColors.sagePrimary : AppColors.terracottaCoral,
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
