package com.nascotech.financials.service.implementation;

import com.nascotech.financials.dto.DataListPaymentResponse;
import com.nascotech.financials.dto.FinancialTransactionRequest;
import com.nascotech.financials.dto.PaymentResponse;
import com.nascotech.financials.model.FinancialTransaction;
import com.nascotech.financials.repository.FinancialTransactionRepository;
import com.nascotech.financials.service.interfaces.FinancialTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialTransactionServiceImpl implements FinancialTransactionService {

    private final FinancialTransactionRepository repository;


    @Override
    public Mono<ResponseEntity<DataListPaymentResponse>> processFilteredTransactions(
            String dateFrom, String dateTo, Long userId, String service, Pageable pageable, String status, String reference) {

        return getFilteredTransactions(dateFrom, dateTo, userId, service, pageable, status, reference)
                .map(transactions -> {
                    List<PaymentResponse> sortedPayments = transactions.stream()
                            .map(this::retrieveFinancialTransaction)
                            .sorted((p1, p2) -> p2.getPaymentId().compareTo(p1.getPaymentId()))
                            .toList();

                    return ResponseEntity.ok(new DataListPaymentResponse(sortedPayments));
                })
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(new DataListPaymentResponse(List.of()))));
    }

    @Override
    public Mono<List<FinancialTransaction>> getFilteredTransactions(String dateFrom, String dateTo, Long userId, String service, Pageable pageable, String status, String reference) {
        return Mono.fromCallable(() ->
                repository.findByUserIdAndServiceAndStatusAndReferenceAndDateBetween(userId, service, status, reference, dateFrom, dateTo, pageable).getContent()
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public PaymentResponse retrieveFinancialTransaction(FinancialTransaction transaction) {
        return new PaymentResponse(transaction.getId(), transaction.getService(), transaction.getStatus());
    }

    @Override
    public String saveTransaction(FinancialTransactionRequest transaction) {
        repository.save(FinancialTransaction.builder()
                .userId(transaction.getUserId())
                .service(transaction.getService())
                .status(transaction.getStatus())
                .reference(transaction.getReference())
                .date(transaction.getDate())
                .build());
        return "Transaction saved successfully";
    }
}
