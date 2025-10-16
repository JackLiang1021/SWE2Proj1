package com.example.demo.repository;

import com.example.demo.entities.ProgramVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProgramVersionRepository extends JpaRepository<ProgramVersion, Long> {
    Optional<ProgramVersion> findByProgram_CodeAndCatalogYear(String programCode, int catalogYear);
    List<ProgramVersion> findByProgram_Code(String programCode);
    List<ProgramVersion> findByCatalogYear(int catalogYear);
    boolean existsByProgram_CodeAndCatalogYear(String programCode, int catalogYear);

}
