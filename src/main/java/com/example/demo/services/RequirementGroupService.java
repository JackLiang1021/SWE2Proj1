package com.example.demo.services;

import com.example.demo.entities.Course;
import com.example.demo.entities.ProgramVersion;
import com.example.demo.entities.RequirementGroup;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ProgramVersionRepository;
import com.example.demo.repository.RequirementGroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequirementGroupService {
    private final ProgramVersionRepository pvRepo;
    private final RequirementGroupRepository rgRepo;
    private final CourseRepository courseRepo;

    public RequirementGroupService(ProgramVersionRepository pvRepo,
                                   RequirementGroupRepository rgRepo,
                                   CourseRepository courseRepo) {
        this.pvRepo = pvRepo;
        this.rgRepo = rgRepo;
        this.courseRepo = courseRepo;
    }

    private ProgramVersion getPV(String code, int year) {
        return pvRepo.findByProgram_CodeAndCatalogYear(code, year)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProgramVersion not found"));
    }

    public List<RequirementGroup> list(String code, int year) {
        ProgramVersion pv = getPV(code, year);
        return rgRepo.findByProgramVersion_Id(pv.getId());
    }

    @Transactional
    public RequirementGroup create(String code, int year, RequirementGroup group) {
        ProgramVersion pv = getPV(code, year);
        if (group.getName() == null || group.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name required");
        }
        if (group.getCourseOptions() != null && !group.getCourseOptions().isEmpty()) {
            group.setCourseOptions(resolveCourses(group.getCourseOptions()));
        }

        group.setProgramVersion(pv);
        return rgRepo.save(group);
    }

    public RequirementGroup get(String code, int year, Long groupId) {
        ProgramVersion pv = getPV(code, year);
        return rgRepo.findById(groupId)
                .filter(g -> g.getProgramVersion().getId().equals(pv.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
    }

    @Transactional
    public RequirementGroup update(String code, int year, Long groupId, RequirementGroup patch) {
        RequirementGroup g = get(code, year, groupId);

        if (patch.getName() != null) {
            g.setName(patch.getName());
        }
        if (patch.getMinRequired() != 0) {
            g.setMinRequired(patch.getMinRequired());
        }
        if (patch.getCourseOptions() != null) {
            Set<Course> resolved = resolveCourses(patch.getCourseOptions());
            g.setCourseOptions(resolved);
        }
        return rgRepo.save(g);
    }

    @Transactional
    public void delete(String code, int year, Long groupId) {
        RequirementGroup g = get(code, year, groupId);
        rgRepo.delete(g);
    }

    private Set<Course> resolveCourses(Set<Course> posted) {
        return posted.stream()
                .map(c -> {
                    String code = c.getCode();
                    if (code == null || code.isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code required in courseOptions");
                    }
                    return courseRepo.findById(code)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + code));
                })
                .collect(Collectors.toSet());
    }
}
