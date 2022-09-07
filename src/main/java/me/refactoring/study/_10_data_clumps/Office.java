package me.refactoring.study._10_data_clumps;

public class Office {

    private String location;

    private String officeAreCode;

    private String officeNumber;

    private TelephoneNumber officePhoneNumber;

    public Office(String location, TelephoneNumber officePhoneNumber) {
        this.location = location;
        this.officePhoneNumber = officePhoneNumber;
    }

    public String officePhoneNumber() {
        return this.officePhoneNumber.toString();
    }

    public String getOfficeAreCode() {
        return this.officePhoneNumber.getAreaCode();
    }

    public void setOfficeAreCode(String officeAreCode) {
        this.setOfficeNumber(officeAreCode);
    }

    public String getOfficeNumber() {
        return this.officePhoneNumber.getNumber();
    }

    public void setOfficeNumber(String officeNumber) {
        this.officePhoneNumber.setNumber(officeNumber);
    }
}
