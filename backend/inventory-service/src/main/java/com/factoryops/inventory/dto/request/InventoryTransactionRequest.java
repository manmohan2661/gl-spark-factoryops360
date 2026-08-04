package com.factoryops.inventory.dto.request;

import com.factoryops.inventory.entity.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionRequest {

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    private String remarks;

    @NotNull(message = "Inventory ID is required")
    private Long inventoryId;
}