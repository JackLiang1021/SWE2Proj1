package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_code")
    @JsonBackReference("student-program")
    private Program program;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_version_id")
    @JsonBackReference("student-programVersion")
    private ProgramVersion programVersion;

    @ManyToMany
    @JoinTable(
            name = "student_completed_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_code")
    )
    private Set<Course> completedCourses = new LinkedHashSet<>();

    public Student() {}

    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public Program getProgram() { return program; }
    public ProgramVersion getProgramVersion() { return programVersion; }
    public Set<Course> getCompletedCourses() { return completedCourses; }

    public void setId(Long id) { this.id = id; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPassword(String password) { this.password = password; }
    public void setProgram(Program program) { this.program = program; }
    public void setProgramVersion(ProgramVersion programVersion) { this.programVersion = programVersion; }
    public void setCompletedCourses(Set<Course> completedCourses) {
        this.completedCourses = (completedCourses != null) ? new LinkedHashSet<>(completedCourses) : new LinkedHashSet<>();
    }

    public void addCompleted(Course c) { if (c != null) this.completedCourses.add(c); }
    public void removeCompleted(Course c) { if (c != null) this.completedCourses.remove(c); }
}
