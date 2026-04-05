package com.finance.service;

import com.finance.dto.FinancialRecordDto;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.FinancialRecord;
import com.finance.model.RecordType;
import com.finance.repository.FinancialRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository repository;

    @InjectMocks
    private FinancialRecordService service;

    private FinancialRecord record;
    private FinancialRecordDto recordDto;

    @BeforeEach
    void setUp() {
        record = FinancialRecord.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .type(RecordType.INCOME)
                .category("SALARY")
                .date(LocalDate.now())
                .deleted(false)
                .build();

        recordDto = FinancialRecordDto.builder()
                .amount(new BigDecimal("100.00"))
                .type(RecordType.INCOME)
                .category("SALARY")
                .date(LocalDate.now())
                .build();
    }

    @Test
    void testCreateRecord() {
        when(repository.save(any(FinancialRecord.class))).thenReturn(record);

        FinancialRecordDto created = service.createRecord(recordDto);

        assertNotNull(created);
        assertEquals(new BigDecimal("100.00"), created.getAmount());
        assertEquals(RecordType.INCOME, created.getType());
        verify(repository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void testGetRecordById() {
        when(repository.findById(1L)).thenReturn(Optional.of(record));

        FinancialRecordDto found = service.getRecordById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetRecordById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getRecordById(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRecordsWithPagination() {
        Page<FinancialRecord> page = new PageImpl<>(List.of(record));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<FinancialRecordDto> result = service.getRecords(null, null, null, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }
}
