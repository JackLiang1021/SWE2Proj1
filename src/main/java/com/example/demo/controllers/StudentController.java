package com.example.demo.controllers;

import com.example.demo.entities.Student;
import com.example.demo.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService svc;

    public StudentController(StudentService svc) {
        this.svc = svc;
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable Long id) {
        return svc.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@RequestBody CreateStudentRequest req) {
        return svc.create(
                req.externalId,
                req.firstName,
                req.lastName,
                req.email,
                req.password,
                req.programCode,
                req.catalogYear
        );
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student patch) {
        return svc.update(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }

    @PostMapping("/{id}/completed")
    public Student addCompleted(@PathVariable Long id, @RequestBody Set<String> courseCodes) {
        return svc.addCompletedCourses(id, courseCodes);
    }

    @DeleteMapping("/{id}/completed/{courseCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompleted(@PathVariable Long id, @PathVariable String courseCode) {
        svc.removeCompletedCourse(id, courseCode);
    }

    @GetMapping("/{id}/progress")
    public Map<String, Object> progress(@PathVariable Long id) {
        return svc.progress(id);
    }

    public static class CreateStudentRequest {
        public String externalId;
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String programCode;
        public int catalogYear;
    }
}
