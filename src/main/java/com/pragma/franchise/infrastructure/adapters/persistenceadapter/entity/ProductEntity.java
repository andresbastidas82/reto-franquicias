package com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "product")
@Getter
@Setter
@RequiredArgsConstructor
public class ProductEntity {

    @Id
    private Long id;

    private String name;

    private Integer stock;

    @Column("branch_id")
    private Long branchId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
