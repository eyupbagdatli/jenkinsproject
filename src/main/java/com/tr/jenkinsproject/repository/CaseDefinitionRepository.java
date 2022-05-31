package com.tr.jenkinsproject.repository;

import com.tr.jenkinsproject.domain.CaseDefinition;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CaseDefinition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CaseDefinitionRepository extends JpaRepository<CaseDefinition, Long> {}
