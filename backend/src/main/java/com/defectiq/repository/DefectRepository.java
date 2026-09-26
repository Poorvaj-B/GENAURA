package com.defectiq.repository;

import com.defectiq.model.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DefectRepository extends JpaRepository<Defect,Long> {
    List<Defect> findTop20ByOrderByCreatedAtDesc();
}
