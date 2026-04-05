package com.finance.service;

import com.finance.dto.CategoryTotalDto;
import com.finance.dto.DashboardSummaryDto;
import com.finance.repository.FinancialRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardSummaryServiceTest {

    @Mock
    private FinancialRecordRepository repository;

    @InjectMocks
    private DashboardSummaryService service;

    @Test
    void testGetDashboardSummary() {
        BigDecimal income = new BigDecimal("5000.00");
        BigDecimal expenses = new BigDecimal("2000.00");
        
        when(repository.getTotalIncome()).thenReturn(income);
        when(repository.getTotalExpenses()).thenReturn(expenses);
        when(repository.getCategoryTotals()).thenReturn(List.of(
                new CategoryTotalDto("SALARY", new BigDecimal("5000.00")),
                new CategoryTotalDto("FOOD", new BigDecimal("2000.00"))
        ));
        when(repository.getMonthlyTrends(any(LocalDate.class))).thenReturn(new ArrayList<>());

        DashboardSummaryDto summary = service.getDashboardSummary();

        assertNotNull(summary);
        assertEquals(income, summary.getTotalIncome());
        assertEquals(expenses, summary.getTotalExpenses());
        assertEquals(new BigDecimal("3000.00"), summary.getNetBalance()); // 5000 - 2000
        assertEquals(2, summary.getCategoryTotals().size());
        assertEquals(0, summary.getMonthlyTrends().size());
    }
}
