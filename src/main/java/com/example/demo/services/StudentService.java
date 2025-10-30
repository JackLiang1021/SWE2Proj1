package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepo;
    private final ProgramRepository programRepo;
    private final ProgramVersionRepository pvRepo;
    private final CourseRepository courseRepo;
    private final RequirementGroupRepository groupRepo;

    public StudentService(StudentRepository studentRepo,
                          ProgramRepository programRepo,
                          ProgramVersionRepository pvRepo,
                          CourseRepository courseRepo,
                          RequirementGroupRepository groupRepo) {
        this.studentRepo = studentRepo;
        this.programRepo = programRepo;
        this.pvRepo = pvRepo;
        this.courseRepo = courseRepo;
        this.groupRepo = groupRepo;
    }

    public Student get(Long id) {
        return studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @Transactional
    public Student create(String externalId, String firstName, String lastName, String email,
                          String password,
                          String programCode, int catalogYear) {
        Program program = programRepo.findById(programCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));
        ProgramVersion pv = pvRepo.findByProgram_CodeAndCatalogYear(programCode, catalogYear)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProgramVersion not found"));

        if (studentRepo.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        Student s = new Student();
        s.setExternalId(externalId);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setPassword(password);
        s.setProgram(program);
        s.setProgramVersion(pv);
        return studentRepo.save(s);
    }

    @Transactional
    public Student update(Long id, Student patch) {
        Student s = get(id);
        if (patch.getExternalId() != null) s.setExternalId(patch.getExternalId());
        if (patch.getFirstName() != null) s.setFirstName(patch.getFirstName());
        if (patch.getLastName() != null) s.setLastName(patch.getLastName());
        if (patch.getEmail() != null) s.setEmail(patch.getEmail());
        if (patch.getPassword() != null) s.setPassword(patch.getPassword());
        if (patch.getProgram() != null) s.setProgram(patch.getProgram());
        if (patch.getProgramVersion() != null) s.setProgramVersion(patch.getProgramVersion());
        return studentRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        Student s = get(id);
        studentRepo.delete(s);
    }

    @Transactional
    public Student addCompletedCourses(Long studentId, Set<String> courseCodes) {
        Student s = get(studentId);
        if (courseCodes == null || courseCodes.isEmpty()) return s;
        for (String code : courseCodes) {
            Course c = courseRepo.findById(code)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + code));
            s.addCompleted(c);
        }
        return studentRepo.save(s);
    }

    @Transactional
    public Student removeCompletedCourse(Long studentId, String courseCode) {
        Student s = get(studentId);
        Course c = courseRepo.findById(courseCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseCode));
        s.removeCompleted(c);
        return studentRepo.save(s);
    }

    public Map<String, Object> progress(Long studentId) {
        Student s = get(studentId);

        List<RequirementGroup> groups = groupRepo.findWithCourseOptionsByProgramVersion_Id(s.getProgramVersion().getId());

        int totalEarned = s.getCompletedCourses().stream().mapToInt(Course::getCredits).sum();

        Map<Long, Integer> groupEarned = new HashMap<>();
        for (RequirementGroup g : groups) groupEarned.put(g.getId(), 0);

        for (Course c : s.getCompletedCourses()) {
            for (RequirementGroup g : groups) {
                if (groupEarned.get(g.getId()) >= g.getMinRequired()) continue;
                boolean inGroup = g.getCourseOptions().stream().anyMatch(opt -> opt.getCode().equals(c.getCode()));
                if (inGroup) {
                    int newVal = Math.min(g.getMinRequired(), groupEarned.get(g.getId()) + c.getCredits());
                    groupEarned.put(g.getId(), newVal);
                }
            }
        }

        List<Map<String, Object>> groupSummaries = groups.stream().map(g -> {
            int earned = groupEarned.getOrDefault(g.getId(), 0);
            int need = Math.max(0, g.getMinRequired() - earned);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("groupId", g.getId());
            m.put("groupName", g.getName());
            m.put("minRequired", g.getMinRequired());
            m.put("earned", earned);
            m.put("remaining", need);
            m.put("coursesInGroup", g.getCourseOptions().stream().map(Course::getCode).toList());
            return m;
        }).collect(Collectors.toList());

        boolean groupsSatisfied = groupSummaries.stream().allMatch(gs -> ((Integer) gs.get("remaining")) == 0);
        boolean totalSatisfied = totalEarned >= s.getProgramVersion().getRequiredCredits();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("studentId", s.getId());
        res.put("studentName", s.getFirstName() + " " + s.getLastName());
        res.put("program", s.getProgram().getCode());
        res.put("catalogYear", s.getProgramVersion().getCatalogYear());
        res.put("requiredCredits", s.getProgramVersion().getRequiredCredits());
        res.put("earnedCredits", totalEarned);
        res.put("groups", groupSummaries);
        res.put("allGroupsSatisfied", groupsSatisfied);
        res.put("totalCreditsSatisfied", totalSatisfied);
        res.put("programComplete", groupsSatisfied && totalSatisfied);
        return res;
    }
}
