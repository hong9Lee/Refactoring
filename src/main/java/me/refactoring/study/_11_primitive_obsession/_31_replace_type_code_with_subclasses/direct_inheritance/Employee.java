package me.refactoring.study._11_primitive_obsession._31_replace_type_code_with_subclasses.direct_inheritance;

import java.util.List;

public abstract class Employee {

    private String name;

    protected Employee(String name) {
        this.name = name;
    }

    public static Employee createEmployee(String name, String type) {
        switch (type) {
            case "engineer" :
                return new Engineer(name);
            case "manager" :
                return new Manager(name);
            case "salesman" :
                return new Salesman(name);
            default:
                throw new IllegalArgumentException(type);
        }
    }

    protected abstract String getType();

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }
}
