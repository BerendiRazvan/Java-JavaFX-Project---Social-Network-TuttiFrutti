package com.example.map_proiect_extins.repository.paging;

import com.example.map_proiect_extins.domain.Entity;
import com.example.map_proiect_extins.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllPaginated(Pageable pageable,Long currentUserId);   // Pageable e un fel de paginator
}
