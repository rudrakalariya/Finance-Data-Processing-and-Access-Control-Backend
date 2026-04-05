package com.finance.dto;

import com.finance.model.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyTrendDto {
    private Integer year;
    private Integer month;
    private RecordType type;
    private BigDecimal totalAmount;
}
