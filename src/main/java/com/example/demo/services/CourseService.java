package com.example.demo.services;

import com.example.demo.entities.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return  courseRepository.findAll();
    }
    public Course getByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public Course getByName(String name) {
        return courseRepository.findByName(name);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Course addPrerequisites(String courseCode, Set<String> prereqCodes) {
        Course course = getByCode(courseCode);

        Set<Course> prereqs = prereqCodes.stream()
                .map(this::getByCode)
                .collect(Collectors.toSet());

        course.getPrerequisites().addAll(prereqs);
        return courseRepository.save(course);
    }

    public Set<Course> getPrerequisites(String courseCode) {
        Course course = getByCode(courseCode);
        return course.getPrerequisites();
    }
}
