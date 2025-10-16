package com.example.demo.dataNOTUSED;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProgramCatalog(
        List<Degree> programs
) {}
