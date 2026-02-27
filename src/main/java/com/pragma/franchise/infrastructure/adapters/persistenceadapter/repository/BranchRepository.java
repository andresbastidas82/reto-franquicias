package com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
}
