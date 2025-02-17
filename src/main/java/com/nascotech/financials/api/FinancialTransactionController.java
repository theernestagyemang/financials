package com.nascotech.financials.api;

import com.nascotech.financials.dto.DataListPaymentResponse;
import com.nascotech.financials.dto.FinancialTransactionRequest;
import com.nascotech.financials.dto.PaymentResponse;
import com.nascotech.financials.service.implementation.FinancialTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {
    private final FinancialTransactionServiceImpl financialTransactionService;


    @GetMapping
    public Mono<ResponseEntity<DataListPaymentResponse>> getFinancialTransactions(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String service,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reference) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return financialTransactionService.getFilteredTransactions(dateFrom, dateTo, userId, service, pageable, status, reference)
                .map(transactions -> {
                    List<PaymentResponse> sortedPayments = transactions.stream()
                            .map(financialTransactionService::retrieveFinancialTransaction)
                            .sorted(Comparator.comparing(PaymentResponse::getPaymentId).reversed())
                            .collect(Collectors.toList());
                    DataListPaymentResponse response = new DataListPaymentResponse(sortedPayments);
                    Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FinancialTransactionController.class)
                            .getFinancialTransactions(dateFrom, dateTo, userId, service, page, size, status, reference)).withSelfRel();
                    response.add(selfLink);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(new DataListPaymentResponse(Collections.emptyList()))));
    }

    @PostMapping("/create")
    public String createFinancialTransaction(@RequestBody FinancialTransactionRequest transaction) {
        return financialTransactionService.saveTransaction(transaction);
    }

}
