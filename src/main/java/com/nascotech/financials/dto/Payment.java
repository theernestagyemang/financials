package com.nascotech.financials.dto;

import com.nascotech.financials.model.FinancialTransaction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    private Long paymentId;
    private FinancialTransaction financialTransaction;

}
