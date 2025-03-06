package com.kefa.infrastructure.repository;

import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.domain.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findAllByAccountId(Long id);

    @EntityGraph(attributePaths = "account")
    Optional<Company> findCompanyById(Long id);

}
