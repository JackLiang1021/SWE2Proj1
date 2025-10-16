package com.example.demo.repository;

import com.example.demo.entities.RequirementGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RequirementGroupRepository extends JpaRepository<RequirementGroup, Long> {
    List<RequirementGroup> findByProgramVersion_Id(Long programVersionId);
    List<RequirementGroup> findByProgramVersion_Program_CodeAndProgramVersion_CatalogYear(
            String programCode, int catalogYear);
    Optional<RequirementGroup> findByProgramVersion_IdAndName(Long programVersionId, String name);
    boolean existsByProgramVersion_IdAndName(Long programVersionId, String name);
    List<RequirementGroup> findAllByCourseOptions_Code(String courseCode);
    @EntityGraph(attributePaths = "courseOptions")
    List<RequirementGroup> findWithCourseOptionsByProgramVersion_Id(Long programVersionId);
    @EntityGraph(attributePaths = "courseOptions")
    List<RequirementGroup> findWithCourseOptionsByProgramVersion_Program_CodeAndProgramVersion_CatalogYear(
            String programCode, int catalogYear);
}
