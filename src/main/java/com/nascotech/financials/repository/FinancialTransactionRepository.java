package com.nascotech.financials.repository;

import com.nascotech.financials.model.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    Page<FinancialTransaction> findByUserIdAndServiceAndStatusAndReferenceAndDateBetween(
            Long userId, String service, String status, String reference, String dateFrom, String dateTo, Pageable pageable);
}
