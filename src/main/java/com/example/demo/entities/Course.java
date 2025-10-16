package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Course {
    @Id
    private String code;

    private String name;
    private int credits;

    @ManyToMany
    @JoinTable(
            name = "course_prerequisite",
            joinColumns = @JoinColumn(name = "course_code"),
            inverseJoinColumns = @JoinColumn(name = "prereq_code")
    )
    private Set<Course> prerequisites = new HashSet<>();

    public Course() {}
    public Course(String code, String name, int credits) {
        this.code = code; this.name = name; this.credits = credits;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public Set<Course> getPrerequisites() { return prerequisites; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setPrerequisites(Set<Course> prerequisites) { this.prerequisites = prerequisites; }
}