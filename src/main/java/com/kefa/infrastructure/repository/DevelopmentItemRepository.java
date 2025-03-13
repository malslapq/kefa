package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.DevelopmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevelopmentItemRepository extends JpaRepository<DevelopmentItem, Long> {

    @Query("SELECT di FROM DevelopmentItem di JOIN FETCH di.company c JOIN FETCH c.account WHERE c.id = :companyId")
    List<DevelopmentItem> findAllByCompanyIdWithAccount(@Param("companyId") Long companyId);

    @Query("SELECT di FROM DevelopmentItem di JOIN FETCH di.company c JOIN FETCH c.account WHERE di.id = :itemId")
    Optional<DevelopmentItem> findByIdWithCompanyAndAccount(@Param("itemId") Long itemId);

}
