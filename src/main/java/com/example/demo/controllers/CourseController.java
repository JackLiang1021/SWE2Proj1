package com.example.demo.controllers;


import com.example.demo.entities.Course;
import com.example.demo.services.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.saveCourse(course);
    }

    @GetMapping("/{code}")
    public Course getCourseByCode(@PathVariable String code) {
        return courseService.getByCode(code);
    }
    @GetMapping("/name/{name}")
    public Course getCourseByName(@PathVariable String name) {
        return courseService.getByName(name);
    }
    @PostMapping("/{code}/prerequisites")
    public Course addPrerequisites(@PathVariable String code, @RequestBody Set<String> prereqCodes) {
        return courseService.addPrerequisites(code, prereqCodes);
    }

    @GetMapping("/{code}/prerequisites")
    public Set<Course> getPrerequisites(@PathVariable String code) {
        return courseService.getPrerequisites(code);
    }
}
