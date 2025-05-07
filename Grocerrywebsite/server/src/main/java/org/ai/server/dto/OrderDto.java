package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.enumPackage.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long id;
    private Long userId;
    private List<OrderItemDto> orderItems;
    private Double amount;
    private AddressDto address;
    private String status;
    private PaymentType paymentType;
    private boolean isPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;





}
