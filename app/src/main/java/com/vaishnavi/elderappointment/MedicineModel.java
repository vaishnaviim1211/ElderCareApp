package com.vaishnavi.elderappointment;

public class MedicineModel {
    private String name;
    private String details;
    private boolean isChecked;

    public MedicineModel(String name, String details) {
        this.name = name;
        this.details = details;
        this.isChecked = false;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}