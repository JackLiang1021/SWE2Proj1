package com.example.demo.dataNOTUSED;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Degree(
        String code,
        String name,
        List<Version> versions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Version(
            int catalogYear,
            int requiredCredits,
            List<RequiredCourseGroup> groups
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RequiredCourseGroup(
            String name,
            int minRequired,
            List<String> courseCodes
    ) {}
}
