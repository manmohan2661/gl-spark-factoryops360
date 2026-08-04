package com.factoryops.inventory.dto.response;

import com.factoryops.inventory.entity.TransactionType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {

    private Long id;

    private TransactionType transactionType;

    private Integer quantity;

    private String referenceNumber;

    private LocalDateTime transactionDate;

    private String remarks;

    private Long inventoryId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
