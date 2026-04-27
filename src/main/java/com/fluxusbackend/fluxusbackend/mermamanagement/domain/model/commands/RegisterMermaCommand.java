package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.enums.MermaReason;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.CategoryName;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.ExpirationDate;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.ProductName;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.valueobjects.Quantity;
import java.util.Objects;

public record RegisterMermaCommand(
        ProductName productName,
        CategoryName categoryName,
        Quantity quantity,
        ExpirationDate expirationDate,
        MermaReason reason
) {
    public RegisterMermaCommand {
        Objects.requireNonNull(productName, "Product name is required");
        Objects.requireNonNull(categoryName, "Category name is required");
        Objects.requireNonNull(quantity, "Quantity is required");
        Objects.requireNonNull(expirationDate, "Expiration date is required");
        Objects.requireNonNull(reason, "Reason is required");
    }
}

