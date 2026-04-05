package com.finance.repository;

import com.finance.dto.CategoryTotalDto;
import com.finance.dto.MonthlyTrendDto;
import com.finance.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'INCOME'")
    BigDecimal getTotalIncome();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'EXPENSE'")
    BigDecimal getTotalExpenses();

    @Query("SELECT new com.finance.dto.CategoryTotalDto(f.category, SUM(f.amount)) " +
           "FROM FinancialRecord f " +
           "GROUP BY f.category")
    List<CategoryTotalDto> getCategoryTotals();

    @Query("SELECT new com.finance.dto.MonthlyTrendDto(EXTRACT(YEAR FROM f.date), EXTRACT(MONTH FROM f.date), f.type, SUM(f.amount)) " +
           "FROM FinancialRecord f " +
           "WHERE f.date >= :startDate " +
           "GROUP BY EXTRACT(YEAR FROM f.date), EXTRACT(MONTH FROM f.date), f.type " +
           "ORDER BY EXTRACT(YEAR FROM f.date) ASC, EXTRACT(MONTH FROM f.date) ASC")
    List<MonthlyTrendDto> getMonthlyTrends(@Param("startDate") LocalDate startDate);
}
