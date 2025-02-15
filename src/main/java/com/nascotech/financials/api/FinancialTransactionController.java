package com.nascotech.financials.api;

import com.nascotech.financials.dto.DataListPaymentResponse;
import com.nascotech.financials.dto.FinancialTransactionRequest;
import com.nascotech.financials.service.implementation.FinancialTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
            @RequestParam(required = false) String reference
    ) {
        return financialTransactionService.getFinancialTransactions(
                        dateFrom, dateTo, userId, service, page, size, status, reference)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }


//    @PostMapping("/create")
//    public String createFinancialTransaction(@RequestBody FinancialTransactionRequest transaction) {
//        return financialTransactionService.saveTransaction(transaction);
//    }

}
