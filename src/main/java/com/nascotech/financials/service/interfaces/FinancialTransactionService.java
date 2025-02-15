package com.nascotech.financials.service.interfaces;

import com.nascotech.financials.dto.DataListPaymentResponse;
import com.nascotech.financials.dto.FinancialTransactionRequest;
import com.nascotech.financials.dto.PaymentResponse;
import com.nascotech.financials.model.FinancialTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface FinancialTransactionService {

    Mono<ResponseEntity<DataListPaymentResponse>> processFilteredTransactions(
            String dateFrom, String dateTo, Long userId, String service, Pageable pageable, String status, String reference);

    Mono<List<FinancialTransaction>> getFilteredTransactions(String dateFrom, String dateTo, Long userId, String service, Pageable pageable, String status, String reference);

    PaymentResponse retrieveFinancialTransaction(FinancialTransaction transaction);

    String saveTransaction(FinancialTransactionRequest transaction);
}
