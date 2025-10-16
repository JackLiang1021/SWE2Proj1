package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class RequirementGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programVersion_id")
    @JsonBackReference("version-groups")
    private ProgramVersion programVersion;

    private String name;
    private int minRequired;

    @ManyToMany
    @JoinTable(
            name = "requirement_group_course",
            joinColumns = @JoinColumn(name = "requirement_group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_code")
    )
    private Set<Course> courseOptions = new HashSet<>();

    public Long getId() { return id; }
    public ProgramVersion getProgramVersion() { return programVersion; }
    public String getName() { return name; }
    public int getMinRequired() { return minRequired; }
    public Set<Course> getCourseOptions() { return courseOptions; }

    public void setId(Long id) { this.id = id; }
    public void setProgramVersion(ProgramVersion programVersion) {
        if (this.programVersion != null && this.programVersion != programVersion) {
            this.programVersion.getGroups().remove(this);
        }
        this.programVersion = programVersion;
        if (programVersion != null && !programVersion.getGroups().contains(this)) {
            programVersion.getGroups().add(this);
        }
    }
    public void setName(String name) { this.name = name; }
    public void setMinRequired(int minRequired) { this.minRequired = minRequired; }
    public void setCourseOptions(Set<Course> courseOptions) {
        this.courseOptions = (courseOptions != null) ? new HashSet<>(courseOptions) : new HashSet<>();
    }
    public void addCourseOption(Course course) {
        if (course != null) this.courseOptions.add(course);
    }

    public void removeCourseOption(Course course) {
        if (course != null) this.courseOptions.remove(course);
    }
}
