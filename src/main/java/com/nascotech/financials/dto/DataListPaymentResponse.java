package com.nascotech.financials.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataListPaymentResponse extends RepresentationModel<DataListPaymentResponse> {
    private List<PaymentResponse> payments;
}