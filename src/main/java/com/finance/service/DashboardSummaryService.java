package com.finance.service;

import com.finance.dto.CategoryTotalDto;
import com.finance.dto.DashboardSummaryDto;
import com.finance.dto.MonthlyTrendDto;
import com.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardSummaryService {

    private final FinancialRecordRepository repository;

    public DashboardSummaryDto getDashboardSummary() {
        BigDecimal totalIncome = repository.getTotalIncome();
        BigDecimal totalExpenses = repository.getTotalExpenses();
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<CategoryTotalDto> categoryTotals = repository.getCategoryTotals();
        
        // Data for the last 6 months
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        List<MonthlyTrendDto> monthlyTrends = repository.getMonthlyTrends(sixMonthsAgo);

        return DashboardSummaryDto.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .build();
    }
}
