package com.swapper.monolith.dto;

import lombok.Data;

public record PaginationRequest(
        int pageSize,
        int pageNumber
) {
}
