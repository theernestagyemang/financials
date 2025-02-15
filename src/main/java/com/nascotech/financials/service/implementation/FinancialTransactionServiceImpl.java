package com.nascotech.financials.service.implementation;

import com.nascotech.financials.api.FinancialTransactionController;
import com.nascotech.financials.dto.DataListPaymentResponse;
import com.nascotech.financials.dto.Payment;
import com.nascotech.financials.model.FinancialTransaction;
import com.nascotech.financials.repository.FinancialTransactionRepository;
import com.nascotech.financials.service.interfaces.FinancialTransactionService;
import com.nascotech.financials.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialTransactionServiceImpl implements FinancialTransactionService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final WebClient webClient;

    public Mono<DataListPaymentResponse> getFinancialTransactions(
            String dateFrom, String dateTo, Long userId, String service,
            int page, int size, String status, String reference) {

        // Step 1: Create Pageable Object
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentId"));

        // Step 2: Retrieve Financial Transactions
        return Mono.fromCallable(() -> financialTransactionRepository
                        .findByFilters(DateUtil.parseDate(dateFrom), DateUtil.parseDate(dateTo), userId, service, status, reference, pageable))
                .flatMapMany(Flux::fromIterable) // Step 3: Extract and Transform Data
                .flatMap(this::processFinancialTransaction) // Step 4: Process Each Financial Transaction
                .collectList()
                .map(sortedPayments -> { // Step 5: Sort Payments (already sorted by pageable)
                    // Step 6: Create Response Object
                    DataListPaymentResponse response = new DataListPaymentResponse();
                    response.setPayments(sortedPayments);
                    response.add(linkTo(methodOn(FinancialTransactionController.class)
                            .getFinancialTransactions(dateFrom, dateTo, userId, service, page, size, status, reference))
                            .withSelfRel());
                    return response;
                })
                .onErrorResume(WebClientResponseException.class, ex -> { // Step 8: Error Handling
                    log.error("Error fetching financial transactions: {}", ex.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Payment> processFinancialTransaction(FinancialTransaction transaction) {
        return retrieveFinancialTransaction(transaction.getPaymentId()) // Fetch details
                .map(payment -> {
                    payment.setFinancialTransaction(transaction); // Associate transaction
                    return payment;
                });
    }

    private Mono<Payment> retrieveFinancialTransaction(Long paymentId) {
        return webClient.get()
                .uri("/api/payments/{id}", paymentId)
                .retrieve()
                .bodyToMono(Payment.class);
    }

}
