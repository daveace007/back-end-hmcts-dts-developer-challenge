package com.richard.task;

public enum Status {

    TODO("To do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ON_HOLD("On Hold"),
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    REVIEWING("Reviewing"),
    FAILED("Failed"),
    DEFERRED("Deferred");

    private String label;

    Status(String label){
        this.label = label;
    }

    public String label(){
        return label;
    }

    public void label(String label){
        this.label = label;
    }


}
