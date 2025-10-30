package com.example.demo.dataSeeder;

import com.example.demo.entities.Course;
import com.example.demo.entities.Program;
import com.example.demo.entities.ProgramVersion;
import com.example.demo.entities.RequirementGroup;
import com.example.demo.entities.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.repository.ProgramVersionRepository;
import com.example.demo.repository.RequirementGroupRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.services.CourseService;
import com.example.demo.services.ProgramService;
import com.example.demo.services.RequirementGroupService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepo;
    private final ProgramRepository programRepo;
    private final ProgramVersionRepository pvRepo;
    private final RequirementGroupRepository rgRepo;
    private final StudentRepository studentRepo;

    private final CourseService courseService;
    private final ProgramService programService;
    private final RequirementGroupService requirementGroupService;

    public DataSeeder(CourseRepository courseRepo,
                      ProgramRepository programRepo,
                      ProgramVersionRepository pvRepo,
                      RequirementGroupRepository rgRepo,
                      StudentRepository studentRepo,
                      CourseService courseService,
                      ProgramService programService,
                      RequirementGroupService requirementGroupService) {
        this.courseRepo = courseRepo;
        this.programRepo = programRepo;
        this.pvRepo = pvRepo;
        this.rgRepo = rgRepo;
        this.studentRepo = studentRepo;
        this.courseService = courseService;
        this.programService = programService;
        this.requirementGroupService = requirementGroupService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        final String PROGRAM_CODE = "CS";
        final String PROGRAM_NAME = "Computer Science";
        final int CATALOG_YEAR = 2024;
        final int PROGRAM_REQUIRED_CREDITS = 80;

        System.out.println("[DataSeeder] Seeding demo data…");

        // ---------------------------
        // 1) COURSES
        // ---------------------------
        List<Course> seedCourses = List.of(
                // CS Foundations & Core
                new Course("CSCI-C200", "Intro to Programming", 4),
                new Course("CSCI-C211", "Intro to Computer Science", 4),
                new Course("CSCI-C241", "Discrete Structures", 3),
                new Course("CSCI-C343", "Data Structures", 3),
                new Course("CSCI-C335", "Computer Structures", 3),
                new Course("CSCI-B403", "Introduction to Algorithms", 3),
                new Course("CSCI-P436", "Operating Systems", 3),
                new Course("CSCI-B438", "Computer Networks", 3),
                new Course("CSCI-B430", "Database Systems", 3),
                new Course("CSCI-B351", "Introduction to Artificial Intelligence", 3),
                new Course("CSCI-B455", "Machine Learning", 3),
                new Course("CSCI-B441", "Computer Graphics", 3),
                new Course("CSCI-B422", "Software Engineering", 3),
                new Course("CSCI-B453", "Security Fundamentals", 3),
                new Course("CSCI-B465", "Data Mining", 3),
                new Course("CSCI-B464", "Parallel & Cloud Computing", 3),
                new Course("CSCI-B447", "Cryptography", 3),
                new Course("CSCI-B457", "Bioinformatics", 3),
                new Course("CSCI-B429", "Information Retrieval", 3),
                new Course("CSCI-C490", "Senior Capstone Project", 3),

                // Math & Stats
                new Course("MATH-M211", "Calculus I", 4),
                new Course("MATH-M212", "Calculus II", 4),
                new Course("MATH-M311", "Linear Algebra", 3),
                new Course("STAT-S320", "Probability", 3),
                new Course("STAT-S350", "Statistics", 3),

                // Science
                new Course("PHYS-P221", "Physics I (Mechanics)", 5),
                new Course("PHYS-P222", "Physics II (E&M)", 5),
                new Course("CHEM-C101", "Elementary Chemistry I", 3),
                new Course("CHEM-C121", "Chemistry Laboratory I", 2),

                // Writing/GenEd
                new Course("ENG-W131", "Reading, Writing & Inquiry", 3),
                new Course("COMM-R110", "Fundamentals of Speech", 3),
                new Course("HUMA-H101", "Humanities: World Culture", 3),
                new Course("SOC-S101", "Introduction to Sociology", 3),

                // More CS electives
                new Course("CSCI-B348", "Introduction to Data Visualization", 3),
                new Course("CSCI-B352", "Intelligent Systems", 3),
                new Course("CSCI-B371", "Programming Language Concepts", 3),
                new Course("CSCI-B461", "Database Design", 3),
                new Course("CSCI-B456", "Applied Machine Learning", 3),
                new Course("CSCI-B463", "High Performance Computing", 3),
                new Course("CSCI-B444", "Advanced Graphics & Visualization", 3),
                new Course("CSCI-B425", "Distributed Systems", 3),
                new Course("CSCI-B426", "Software Testing & QA", 3)
        );

        for (Course c : seedCourses) {
            courseRepo.findById(c.getCode()).orElseGet(() -> courseRepo.save(c));
        }
        System.out.println("[DataSeeder] Courses upserted: " + seedCourses.size());

        // ---------------------------
        // 2) PREREQUISITES
        // ---------------------------
        Map<String, List<String>> prereqMap = new LinkedHashMap<>();
        // Foundations
        prereqMap.put("CSCI-C211", List.of("CSCI-C200"));
        prereqMap.put("CSCI-C241", List.of("CSCI-C200"));
        prereqMap.put("CSCI-C343", List.of("CSCI-C211", "CSCI-C241"));
        prereqMap.put("CSCI-C335", List.of("CSCI-C211"));

        // Core
        prereqMap.put("CSCI-B403", List.of("CSCI-C343", "CSCI-C241"));
        prereqMap.put("CSCI-P436", List.of("CSCI-C343", "CSCI-C335"));
        prereqMap.put("CSCI-B430", List.of("CSCI-C343"));
        prereqMap.put("CSCI-B438", List.of("CSCI-C343"));

        // AI/ML & data
        prereqMap.put("CSCI-B351", List.of("CSCI-C343", "STAT-S350"));
        prereqMap.put("CSCI-B455", List.of("CSCI-B351", "MATH-M311"));
        prereqMap.put("CSCI-B456", List.of("CSCI-B351"));
        prereqMap.put("CSCI-B465", List.of("CSCI-C343", "STAT-S350"));

        // Other CS
        prereqMap.put("CSCI-B441", List.of("CSCI-C343", "MATH-M311"));
        prereqMap.put("CSCI-B444", List.of("CSCI-B441"));
        prereqMap.put("CSCI-B463", List.of("CSCI-C343"));
        prereqMap.put("CSCI-B447", List.of("CSCI-C343", "MATH-M311"));
        prereqMap.put("CSCI-B453", List.of("CSCI-C343"));
        prereqMap.put("CSCI-B429", List.of("CSCI-C343"));
        prereqMap.put("CSCI-B422", List.of("CSCI-C343"));
        prereqMap.put("CSCI-B426", List.of("CSCI-B422"));
        prereqMap.put("CSCI-B425", List.of("CSCI-P436"));

        // Capstone
        prereqMap.put("CSCI-C490", List.of("CSCI-B403", "CSCI-P436", "CSCI-B430", "CSCI-B438"));

        // Math/Science sequences
        prereqMap.put("MATH-M212", List.of("MATH-M211"));
        prereqMap.put("MATH-M311", List.of("MATH-M212"));
        prereqMap.put("STAT-S320", List.of("MATH-M212"));
        prereqMap.put("PHYS-P222", List.of("PHYS-P221", "MATH-M212"));
        prereqMap.put("CHEM-C121", List.of("CHEM-C101"));

        for (Map.Entry<String, List<String>> e : prereqMap.entrySet()) {
            String courseCode = e.getKey();
            Set<String> prereqCodes = new LinkedHashSet<>(e.getValue());
            courseService.addPrerequisites(courseCode, prereqCodes);
        }
        System.out.println("[DataSeeder] Prerequisites set for " + prereqMap.size() + " course(s)");

        // ---------------------------
        // 3) PROGRAM (UPSERT)
        // ---------------------------
        Program program = programRepo.findById(PROGRAM_CODE)
                .orElseGet(() -> programService.create(new Program(PROGRAM_CODE, PROGRAM_NAME)));
        System.out.println("[DataSeeder] Program ready: " + program.getCode());

        // ---------------------------
        // 4) PROGRAM VERSION (UPSERT)
        // ---------------------------
        ProgramVersion version = pvRepo.findByProgram_CodeAndCatalogYear(PROGRAM_CODE, CATALOG_YEAR)
                .orElseGet(() -> {
                    ProgramVersion v = new ProgramVersion();
                    v.setCatalogYear(CATALOG_YEAR);
                    v.setRequiredCredits(PROGRAM_REQUIRED_CREDITS);
                    v.setProgram(program);
                    return pvRepo.save(v);
                });
        System.out.println("[DataSeeder] ProgramVersion ready: " + version.getCatalogYear());

        // ---------------------------
        // 5) REQUIREMENT GROUPS
        // ---------------------------
        List<ReqGroupSeed> groupsToCreate = List.of(
                // Math
                new ReqGroupSeed("Math Core", 11, Set.of("MATH-M211", "MATH-M212", "MATH-M311")),
                new ReqGroupSeed("Statistics Core", 3, Set.of("STAT-S350", "STAT-S320")),

                // CS Foundations
                new ReqGroupSeed("CS Foundations", 13, Set.of("CSCI-C211", "CSCI-C241", "CSCI-C343", "CSCI-C335")),

                // Core Areas
                new ReqGroupSeed("Algorithms Core", 3, Set.of("CSCI-B403")),
                new ReqGroupSeed("Systems Core", 3, Set.of("CSCI-P436")),
                new ReqGroupSeed("Databases Core", 3, Set.of("CSCI-B430")),
                new ReqGroupSeed("Networks Core", 3, Set.of("CSCI-B438")),

                // AI/ML & Data
                new ReqGroupSeed("AI/ML Electives", 6, Set.of("CSCI-B351", "CSCI-B455", "CSCI-B456", "CSCI-B465")),
                new ReqGroupSeed("CS Electives", 9, Set.of(
                        "CSCI-B441","CSCI-B444","CSCI-B463","CSCI-B447","CSCI-B453","CSCI-B429",
                        "CSCI-B422","CSCI-B426","CSCI-B425","CSCI-B348","CSCI-B352","CSCI-B371","CSCI-B461"
                )),

                // Science Sequences (one path suffices)
                new ReqGroupSeed("Science Sequence", 10, Set.of(
                        "PHYS-P221","PHYS-P222","CHEM-C101","CHEM-C121"
                )),

                // Gen Ed
                new ReqGroupSeed("Writing", 3, Set.of("ENG-W131")),
                new ReqGroupSeed("Communication", 3, Set.of("COMM-R110")),
                new ReqGroupSeed("Humanities", 3, Set.of("HUMA-H101")),
                new ReqGroupSeed("Social Science", 3, Set.of("SOC-S101")),

                // Capstone
                new ReqGroupSeed("Capstone", 3, Set.of("CSCI-C490"))
        );

        for (ReqGroupSeed spec : groupsToCreate) {
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

            Set<Course> options = spec.courseCodes.stream()
                    .map(code -> courseRepo.findById(code).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing course for group: " + code)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            group.setCourseOptions(options);
            rgRepo.save(group);
        }

        long totalGroups = rgRepo.findByProgramVersion_Id(version.getId()).size();
        long totalCourses = courseRepo.count();
        System.out.println("[DataSeeder] Groups=" + totalGroups + ", Courses=" + totalCourses);

        // ---------------------------
        // 6) STUDENTS (login-ready; plaintext passwords for testing)
        // ---------------------------
        // NOTE: Student entity must have fields: email (unique, not null) and password (not null).
        upsertStudent(
                "stu-0001",
                "alex.rivera@example.edu",
                "test123",                 // password (plaintext; for testing only)
                "Alex", "Rivera",
                program, version,
                Set.of("CSCI-C200", "MATH-M211", "ENG-W131", "PHYS-P221")
        );

        upsertStudent(
                "stu-0002",
                "jamie.lee@example.edu",
                "passw0rd",
                "Jamie", "Lee",
                program, version,
                Set.of("CSCI-C200", "CSCI-C211", "CSCI-C241", "MATH-M211", "HUMA-H101")
        );

        upsertStudent(
                "stu-0003",
                "priya.patel@example.edu",
                "secret",
                "Priya", "Patel",
                program, version,
                Set.of("CSCI-C200", "MATH-M211", "MATH-M212", "STAT-S350", "SOC-S101")
        );
    }
    @Transactional
    void upsertStudent(String externalId,
                               String email,
                               String password,
                               String firstName,
                               String lastName,
                               Program program,
                               ProgramVersion version,
                               Set<String> completedCodes) {

        Student s = studentRepo.findByExternalId(externalId).orElse(null);
        if (s == null) {
            s = new Student();
            s.setExternalId(externalId);
        }
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setPassword(password); // ⚠️ plaintext for testing only
        s.setProgram(program);
        s.setProgramVersion(version);

        // Attach completed courses (merge repeat-safe)
        Set<Course> completed = completedCodes.stream()
                .map(code -> courseRepo.findById(code).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing course: " + code)))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Course> merged = new LinkedHashSet<>(s.getCompletedCourses());
        merged.addAll(completed);
        s.setCompletedCourses(merged);

        s = studentRepo.save(s);
        System.out.println("[DataSeeder] Upserted student " + externalId + " (id=" + s.getId() + ", email=" + email + ")");
    }

    private record ReqGroupSeed(String name, int minRequired, Set<String> courseCodes) {}
}
