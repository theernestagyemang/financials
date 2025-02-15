package com.nascotech.financials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionRequest {
    private Long id;
    private Long userId;
    private String service;
    private String status;
    private String reference;
    private String date;
}
