package com.finance.service;

import com.finance.dto.FinancialRecordDto;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.FinancialRecord;
import com.finance.model.RecordType;
import com.finance.repository.FinancialRecordRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    public FinancialRecordDto createRecord(FinancialRecordDto dto) {
        FinancialRecord record = mapToEntity(dto);
        return mapToDto(recordRepository.save(record));
    }

    public FinancialRecordDto updateRecord(Long id, FinancialRecordDto dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());

        return mapToDto(recordRepository.save(record));
    }

    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        recordRepository.delete(record);
    }

    public FinancialRecordDto getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        return mapToDto(record);
    }

    public Page<FinancialRecordDto> getRecords(LocalDate startDate, LocalDate endDate, String category, RecordType type, Pageable pageable) {
        Specification<FinancialRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return recordRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    private FinancialRecord mapToEntity(FinancialRecordDto dto) {
        return FinancialRecord.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .category(dto.getCategory())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .deleted(false)
                .build();
    }

    private FinancialRecordDto mapToDto(FinancialRecord record) {
        return FinancialRecordDto.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .build();
    }
}
