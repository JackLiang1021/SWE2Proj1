package com.example.demo.dataSeeder;

import com.example.demo.entities.Course;
import com.example.demo.entities.Program;
import com.example.demo.entities.ProgramVersion;
import com.example.demo.entities.RequirementGroup;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.repository.ProgramVersionRepository;
import com.example.demo.repository.RequirementGroupRepository;
import com.example.demo.services.CourseService;
import com.example.demo.services.ProgramService;
import com.example.demo.services.RequirementGroupService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Seeds the database with a realistic Program -> ProgramVersion -> RequirementGroups
 * plus Courses and their prerequisites.
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepo;
    private final ProgramRepository programRepo;
    private final ProgramVersionRepository pvRepo;
    private final RequirementGroupRepository rgRepo;

    private final CourseService courseService;
    private final ProgramService programService;
    private final RequirementGroupService requirementGroupService;

    public DataSeeder(CourseRepository courseRepo,
                      ProgramRepository programRepo,
                      ProgramVersionRepository pvRepo,
                      RequirementGroupRepository rgRepo,
                      CourseService courseService,
                      ProgramService programService,
                      RequirementGroupService requirementGroupService) {
        this.courseRepo = courseRepo;
        this.programRepo = programRepo;
        this.pvRepo = pvRepo;
        this.rgRepo = rgRepo;
        this.courseService = courseService;
        this.programService = programService;
        this.requirementGroupService = requirementGroupService;
    }

    @Override
    public void run(String... args) {
        // ---- Parameters you can tweak ----
        final String PROGRAM_CODE = "CS";
        final String PROGRAM_NAME = "Computer Science";
        final int CATALOG_YEAR = 2024;
        final int PROGRAM_REQUIRED_CREDITS = 120;

        System.out.println("[DataSeeder] Seeding demo data…");

        // 1) COURSES
        // code, name, credits
        List<Course> seedCourses = List.of(
                new Course("CSCI-C200", "Intro to Programming", 4),
                new Course("CSCI-C211", "Intro to Computer Science", 4),
                new Course("CSCI-C241", "Discrete Structures", 3),
                new Course("CSCI-C343", "Data Structures", 3),
                new Course("CSCI-C335", "Computer Structures", 3),
                new Course("MATH-M211", "Calculus I", 4),
                new Course("MATH-M212", "Calculus II", 4),
                new Course("STAT-S350", "Statistics", 3),
                new Course("ENG-W131", "Reading, Writing & Inquiry", 3),
                new Course("CSCI-B351", "Intro to AI", 3)
        );

        // upsert courses
        for (Course c : seedCourses) {
            courseRepo.findById(c.getCode()).orElseGet(() -> courseRepo.save(c));
        }
        System.out.println("[DataSeeder] Courses upserted: " + seedCourses.size());

        // 2) PREREQUISITES
        // Map course -> prerequisite codes
        Map<String, List<String>> prereqMap = new LinkedHashMap<>();
        prereqMap.put("CSCI-C211", List.of("CSCI-C200"));
        prereqMap.put("CSCI-C241", List.of("CSCI-C200"));
        prereqMap.put("CSCI-C343", List.of("CSCI-C211", "CSCI-C241"));
        prereqMap.put("CSCI-C335", List.of("CSCI-C211"));
        prereqMap.put("MATH-M212", List.of("MATH-M211"));
        prereqMap.put("STAT-S350", List.of("MATH-M212"));
        prereqMap.put("CSCI-B351", List.of("CSCI-C343", "STAT-S350"));

        for (Map.Entry<String, List<String>> e : prereqMap.entrySet()) {
            String courseCode = e.getKey();
            Set<String> prereqCodes = new LinkedHashSet<>(e.getValue());
            // Will throw 404 if any code missing — which is fine for surfacing mistakes
            courseService.addPrerequisites(courseCode, prereqCodes);
        }
        System.out.println("[DataSeeder] Prerequisites configured for: " + prereqMap.size() + " course(s)");

        // 3) PROGRAM (upsert)
        Program program = programRepo.findById(PROGRAM_CODE)
                .orElseGet(() -> programService.create(new Program(PROGRAM_CODE, PROGRAM_NAME)));
        System.out.println("[DataSeeder] Program ready: " + program.getCode());

        // 4) PROGRAM VERSION (upsert by unique (program_code, catalogYear))
        ProgramVersion version = pvRepo.findByProgram_CodeAndCatalogYear(PROGRAM_CODE, CATALOG_YEAR)
                .orElseGet(() -> {
                    ProgramVersion v = new ProgramVersion();
                    v.setCatalogYear(CATALOG_YEAR);
                    v.setRequiredCredits(PROGRAM_REQUIRED_CREDITS);
                    v.setProgram(program); // maintain owning side
                    return pvRepo.save(v);
                });
        System.out.println("[DataSeeder] ProgramVersion ready: year=" + version.getCatalogYear());

        // 5) REQUIREMENT GROUPS (create if missing), and attach course options
        // We'll create 4 groups that simulate a real plan
        // - Math Core (choose among Calc I, Calc II)
        // - Statistics Core (Stats)
        // - CS Foundations (C211, C241, C343, C335)
        // - Gen Ed (e.g., W131)
        List<ReqGroupSeed> groupsToCreate = List.of(
                new ReqGroupSeed("Math Core", 8,  // typically Calc I + Calc II
                        Set.of("MATH-M211", "MATH-M212")),
                new ReqGroupSeed("Statistics Core", 3,
                        Set.of("STAT-S350")),
                new ReqGroupSeed("CS Foundations", 12,
                        Set.of("CSCI-C211", "CSCI-C241", "CSCI-C343", "CSCI-C335")),
                new ReqGroupSeed("Gen Ed Writing", 3,
                        Set.of("ENG-W131"))
        );

        for (ReqGroupSeed spec : groupsToCreate) {
            // Find existing by name in this version or create it
            RequirementGroup group = rgRepo.findByProgramVersion_Id(version.getId()).stream()
                    .filter(g -> g.getName().equalsIgnoreCase(spec.name))
                    .findFirst()
                    .orElseGet(() -> {
                        RequirementGroup ng = new RequirementGroup();
                        ng.setName(spec.name);
                        ng.setMinRequired(spec.minRequired);
                        ng.setProgramVersion(version);
                        return rgRepo.save(ng);
                    });

            // Resolve course codes to managed entities and set as courseOptions (replace semantics)
            Set<Course> options = spec.courseCodes.stream()
                    .map(code -> courseRepo.findById(code).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing course for group: " + code)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            group.setCourseOptions(options);
            rgRepo.save(group);
        }
        System.out.println("[DataSeeder] Requirement groups created/updated: " + groupsToCreate.size());

        // Sanity log
        long totalGroups = rgRepo.findByProgramVersion_Id(version.getId()).size();
        long totalCourses = courseRepo.count();
        System.out.println("[DataSeeder] DONE. Courses=" + totalCourses + ", Groups=" + totalGroups);
    }

    // Simple holder for group seed input
    private record ReqGroupSeed(String name, int minRequired, Set<String> courseCodes) {}
}
