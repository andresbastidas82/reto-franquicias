package com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);

    @Query("""
            SELECT 
                b.id AS branch_id,
                b.name AS branch_name,
                p.id AS product_id,
                p.name AS product_name,
                p.stock
            FROM product p
            JOIN branch b ON p.branch_id = b.id
            WHERE b.franchise_id = :franchiseId
            AND p.stock = (
                SELECT MAX(p2.stock)
                FROM product p2
                WHERE p2.branch_id = b.id
            )
            ORDER BY p.stock DESC
            """)
    Flux<TopStockProduct> findTopStockProductsByFranchise(Long franchiseId);
}
