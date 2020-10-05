package com.example.springsecuritydemo.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/students")
public class StudentController {

    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "Venkat"),
            new Student(2, "Dasari"),
            new Student(3, "Mary")
    );

    @GetMapping("{studentId}")
    public Student getStudent(@PathVariable("studentId") Integer studentId){
        return STUDENTS.stream()
                .filter(student-> student!= null && studentId.equals(student.getStrudentId()))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("Student "+ studentId +"does not exists"));
    }
}
