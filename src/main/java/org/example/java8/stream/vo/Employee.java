package org.example.java8.stream.vo;

import java.util.Objects;

public class Employee {
    private String name;
    private String department;
    private Integer salary;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public void copy(Employee dest) {
        this.salary = dest.getSalary();
        this.department = dest.getDepartment();
        this.gender = dest.getGender();
        this.name = dest.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDepartment(), this.getSalary(), this.getGender(), this.getName());
//        return Objects.hashCode(this );
//        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
//        return super.equals(obj);
        Employee p = (Employee) obj;

        return this.getSalary().equals(p.getSalary())
                && this.getDepartment().equals(p.getDepartment())
                && this.getName().equals(p.getName())
                && this.getGender().equals((p.getGender()))
                ;
//        return true;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                '}';
    }
}
