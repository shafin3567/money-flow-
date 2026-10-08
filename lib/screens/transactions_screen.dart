import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../widgets/moneyflow_clay_card.dart';
import '../services/database_service.dart';

class TransactionsScreen extends StatefulWidget {
  const TransactionsScreen({super.key});

  @override
  State<TransactionsScreen> createState() => _TransactionsScreenState();
}

class _TransactionsScreenState extends State<TransactionsScreen> {
  List<Map<String, dynamic>> _transactions = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final list = await DatabaseService.instance.getTransactions();
    setState(() {
      _transactions = list;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Transactions Ledger", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.creamBackground,
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 18.0),
        child: Column(
          children: [
            TextField(
              decoration: InputDecoration(
                hintText: "Search transactions...",
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: AppColors.recessedSurface,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(20),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: ListView.separated(
                itemCount: _transactions.length,
                separatorBuilder: (context, index) => const SizedBox(height: 8),
                itemBuilder: (context, index) {
                  final tx = _transactions[index];
                  final isIncome = tx['type'] == 'income';
                  return MoneyFlowClayCard(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                    borderRadius: 16,
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: isIncome ? AppColors.sageLight : AppColors.peachContainer,
                          child: Icon(
                            isIncome ? Icons.arrow_downward : Icons.receipt_long,
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
                          isIncome ? "+\$${(tx['amount'] / 100).toStringAsFixed(2)}" : "-\$${(tx['amount'] / 100).toStringAsFixed(2)}",
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
            ),
          ],
        ),
      ),
    );
  }
}
