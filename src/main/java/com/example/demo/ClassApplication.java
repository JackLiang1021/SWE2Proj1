package com.example.demo;

import com.example.demo.data.Course;
import com.example.demo.data.Degree;
import com.example.demo.data.helper.HelperMethods;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ClassApplication {

	public static void main(String[] args) {
        HelperMethods helperMethods = new HelperMethods();

        List<Course> courses = helperMethods.LoadCourseCatalog();
        Map<String, Course> courseMap = helperMethods.LoadCourseCatalogAsMap();
        Map<String, Degree> degreeMap = helperMethods.LoadDegreeCatalogAsMap();

        List<Degree> degrees = helperMethods.LoadDegreeProgramCatalog();

        for(Degree d : degrees){
            System.out.println(d);
            for(Degree.Version v : d.versions()){
                System.out.println(v);
            }
        }




//		SpringApplication.run(ClassApplication.class, args);
	}

}
