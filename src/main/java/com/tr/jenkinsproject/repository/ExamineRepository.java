package com.tr.jenkinsproject.repository;

import com.tr.jenkinsproject.domain.Examine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Examine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamineRepository extends JpaRepository<Examine, Long> {}
