package com.nascotech.financials.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataListPaymentResponse extends RepresentationModel<DataListPaymentResponse> {
    private List<Payment> payments;
}
