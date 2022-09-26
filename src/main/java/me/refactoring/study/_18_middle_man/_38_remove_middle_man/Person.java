package me.refactoring.study._18_middle_man._38_remove_middle_man;

public class Person {

    private Department department;

    private String name;

    public Person(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    public Person getManager() {
        return this.department.getManager();
    }

    /** getter를 이용해 접근을 허용 (위임 숨기기의 반대 작업) */
    public Department getDepartment() {
        return department;
    }
}
