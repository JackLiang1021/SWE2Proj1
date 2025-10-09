package com.example.demo.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student {
    private final String id;
    private final String name;
    private List<Course> courses;
    private int currCredit;
    private Degree degree;

    public Student(String id, String name, Degree degree) {
        this.id = id;
        this.name = name;
        this.degree = degree;
        this.currCredit = 0;
        this.courses = new ArrayList<>();
    }

    public void updateClass(Course... courses) {
        for (Course c : courses) {
            if (c == null) continue;
            if (!this.courses.contains(c)) {
                this.courses.add(c);
                this.currCredit += c.credits();
            }
        }
    }

    public void updateDegree(Degree degree) {
        this.degree = degree;
    }

    public String id() { return id; }
    public String name() { return name; }
    public List<Course> courses() { return Collections.unmodifiableList(courses); }
    public int currCredit() { return currCredit; }
    public Degree degree() { return degree; }
    public String degreeName() { return degree.name(); }
}
