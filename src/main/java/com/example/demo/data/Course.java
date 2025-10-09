package com.example.demo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record Course(String code, String name, int credits, List<String> prerequisites) {
}
