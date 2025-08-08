package com.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.main.models.Case;

@Repository
public interface CaseRepository extends JpaRepository <Case, Long> {

}
