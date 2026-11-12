
package com.example.salary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SalaryController {

    @GetMapping("/salary")
    public String getSalary() {

        return "Employee: Sandeep, Salary: 55000";

    }
}
