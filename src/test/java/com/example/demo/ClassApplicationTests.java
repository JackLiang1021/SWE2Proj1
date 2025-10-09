package com.example.demo;

import com.example.demo.data.Degree;
import com.example.demo.data.Student;
import com.example.demo.data.helper.HelperMethods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class ClassApplicationTests {
    HelperMethods helperMethods = new HelperMethods();
    Map<String, Degree> degreeMap = helperMethods.LoadDegreeCatalogAsMap();
	@Test
	void contextLoads() {
        Student test = new Student("123456789", "Jack", degreeMap.get("CS"));
        Assertions.assertEquals(degreeMap.get("CS"), test.degree());
	}

}
