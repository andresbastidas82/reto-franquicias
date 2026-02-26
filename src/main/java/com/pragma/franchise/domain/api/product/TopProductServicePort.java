package com.pragma.franchise.domain.api.product;

import com.pragma.franchise.domain.model.TopStockProduct;
import reactor.core.publisher.Flux;

public interface TopProductServicePort {

    Flux<TopStockProduct> getTopStockProducts(Long franchiseId);
}
