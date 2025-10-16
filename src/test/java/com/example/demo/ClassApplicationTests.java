package com.example.demo;

import com.example.demo.entities.Course;
import com.example.demo.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

// Example SpringBootTest that writes to the file DB
@SpringBootTest
class ClassApplicationTests {

    @Autowired CourseRepository courseRepo;

    @org.junit.jupiter.api.BeforeAll
    static void ensureFolder() throws Exception {
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("data"));
    }

    @org.junit.jupiter.api.Test
    void writesToFileDb() throws Exception {
        courseRepo.deleteAll();
        courseRepo.save(new Course("C200", "Intro to Programming", 3));

        var found = courseRepo.findByCode("C200");
        assert found != null;

        // Sanity: DB file exists
        var path = java.nio.file.Path.of("data/app.db");
        System.out.println("DB at: " + path.toAbsolutePath());

        System.out.println(found.getName());
        assert java.nio.file.Files.exists(path);
    }
}

