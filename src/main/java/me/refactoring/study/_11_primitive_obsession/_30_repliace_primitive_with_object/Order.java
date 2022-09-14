package me.refactoring.study._11_primitive_obsession._30_repliace_primitive_with_object;

public class Order {

    private String priorityValue;

    private Priority priority;

    public Order(String priority) {
        this(new Priority(priority));
    }

    public Order(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getPriorityValue() {
        return priorityValue;
    }
}
