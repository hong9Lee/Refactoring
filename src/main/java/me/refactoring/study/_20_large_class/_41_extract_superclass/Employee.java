package me.refactoring.study._20_large_class._41_extract_superclass;

public class Employee extends Party {

    public Employee(String name) {
        super(name);
    }

    private Integer id;

    private double monthlyCost;

    public double annualCost() {
        return this.monthlyCost * 12;
    }

    public Integer getId() {
        return id;
    }


    @Override
    public double monthlyCost() {
        return monthlyCost;
    }
}
