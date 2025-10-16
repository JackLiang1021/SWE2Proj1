package com.example.demo.dataNOTUSED.helper;

import com.example.demo.dataNOTUSED.Course;
import com.example.demo.dataNOTUSED.CourseCatalog;
import com.example.demo.dataNOTUSED.Degree;
import com.example.demo.dataNOTUSED.DegreeCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelperMethods {
    public HelperMethods(){}


    public List<Course> LoadCourseCatalog(){
        try (InputStream in = HelperMethods.class.getResourceAsStream("/CourseCatalogData.json")) {
            if (in == null) {
                throw new IOException("CourseCatalogData.json not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            CourseCatalog catalog = mapper.readValue(in, CourseCatalog.class);
            List<Course> courses = catalog.courses();

//            for(Course c : courses){
//                System.out.println(c.name());
//            }
            return courses;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public List<Degree> LoadDegreeProgramCatalog(){
        try (InputStream in = HelperMethods.class.getResourceAsStream("/DegreeProgramCatalog.json")) {
            if (in == null) {
                throw new IOException("DegreeProgramCatalog.json not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            DegreeCatalog catalog = mapper.readValue(in, DegreeCatalog.class);
            List<Degree> degrees = catalog.programs();
//            for(Degree d : degrees){
//                System.out.println(d.name());
//            }
            return degrees;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Course> LoadCourseCatalogAsMap() {
        List<Course> courses = LoadCourseCatalog();
        if (courses == null) return Map.of();
        return courses.stream()
                .collect(Collectors.toMap(
                        Course::code,
                        c -> c
                ));
    }

    public Map<String, Degree> LoadDegreeCatalogAsMap() {
        List<Degree> degrees = LoadDegreeProgramCatalog();
        if (degrees == null) return Map.of();
        return degrees.stream()
                .collect(Collectors.toMap(
                        Degree::code,
                        c -> c
                ));
    }

    public Degree getDegree(Map<String, Degree> degreeMap, String code) {
        if (degreeMap == null || degreeMap.isEmpty()) {
            throw new IllegalStateException("Degree map is null or empty.");
        }
        Degree degree = degreeMap.get(code);
        if (degree == null) {
            throw new IllegalArgumentException("Degree with code '" + code + "' not found. Available codes: " + degreeMap.keySet());
        }
        return degree;
    }

    public Degree.Version getDegreeVersion(Map<String, Degree> degreeMap, String code, int versionIndex) {
        Degree degree = getDegree(degreeMap, code);
        List<Degree.Version> versions = degree.versions();
        if (versions == null || versions.isEmpty()) {
            throw new IllegalStateException("No versions found for degree code '" + code + "'.");
        }
        if (versionIndex < 0 || versionIndex >= versions.size()) {
            throw new IndexOutOfBoundsException("Version index " + versionIndex + " out of range for degree '" + code + "'. Total versions: " + versions.size());
        }
        return versions.get(versionIndex);
    }

    public Degree.RequiredCourseGroup getRequiredCourseGroup(Map<String, Degree> degreeMap, String code, int versionIndex, int groupIndex) {
        Degree.Version version = getDegreeVersion(degreeMap, code, versionIndex);
        List<Degree.RequiredCourseGroup> groups = version.groups();
        if (groups == null || groups.isEmpty()) {
            throw new IllegalStateException("No groups found for degree '" + code + "', version " + version.catalogYear() + ".");
        }
        if (groupIndex < 0 || groupIndex >= groups.size()) {
            throw new IndexOutOfBoundsException("Group index " + groupIndex + " out of range for degree '" + code + "', version " + version.catalogYear() + ". Total groups: " + groups.size());
        }
        return groups.get(groupIndex);
    }
    public Degree getDegree(String code) {
        return getDegree(LoadDegreeCatalogAsMap(), code);
    }

    public Degree.Version getDegreeVersion(String code, int versionIndex) {
        return getDegreeVersion(LoadDegreeCatalogAsMap(), code, versionIndex);
    }

    public Degree.RequiredCourseGroup getRequiredCourseGroup(String code, int versionIndex, int groupIndex) {
        return getRequiredCourseGroup(LoadDegreeCatalogAsMap(), code, versionIndex, groupIndex);
    }


}
