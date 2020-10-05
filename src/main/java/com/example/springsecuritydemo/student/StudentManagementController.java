package com.example.springsecuritydemo.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {
    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "Venkat"),
            new Student(2, "Dasari"),
            new Student(3, "Mary")
    );

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMINTRAINEE')")  // replace antMatchers in securityConfig 1. add "@EnableGlobalMethodSecurity(prePostEnabled = true)" 2. preAuthorize
    public List<Student> getAllStudent(){
        return STUDENTS;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerNewStudent(@RequestBody Student student){
        System.out.println(student);
    }

    @DeleteMapping(path ="{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId){
        System.out.println(studentId);
    }

    @PutMapping(path ="{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@RequestBody Student student, @PathVariable("studentId") Integer studentId){
        System.out.println(String.format("%s %s,student,studentId"));
    }

}
