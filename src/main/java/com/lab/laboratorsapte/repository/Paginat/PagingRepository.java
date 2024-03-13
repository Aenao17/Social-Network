package com.lab.laboratorsapte.repository.Paginat;

import com.lab.laboratorsapte.domain.Entity;
import com.lab.laboratorsapte.repository.Repository;

public interface PagingRepository <ID, E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAllPaginat(Pageable pageable);
}
