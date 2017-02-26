package com.example.aei2.peopleplaces.dto;

/**
 * Created by aei2 on 2/3/2017.
 */

public class PeopleDTO {
    private String cacheID;
    private String latitude;
    private String longitude;
    private String blockFips;
    private String address;
    private double incomeBelowPoverty;
    private int medianIncome;
    private double incomeLessThan25;
    private double incomeBetween25to50;
    private double incomeBetween50to100;
    private double incomeBetween100to200;
    private double incomeGreater200;
    private double educationHighSchoolGraduate;
    private double educationBachelorOrGreater;

    public String getCacheID() {
        return cacheID;
    }

    public void setCacheID(String cacheID) {
        this.cacheID = cacheID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBlockFips() {
        return blockFips;
    }

    public void setBlockFips(String blockFips) {
        this.blockFips = blockFips;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getIncomeBelowPoverty() {
        return incomeBelowPoverty;
    }

    public void setIncomeBelowPoverty(double incomeBelowPoverty) {
        this.incomeBelowPoverty = incomeBelowPoverty;
    }

    public int getMedianIncome() {
        return medianIncome;
    }

    public void setMedianIncome(int medianIncome) {
        this.medianIncome = medianIncome;
    }

    public double getIncomeLessThan25() {
        return incomeLessThan25;
    }

    public void setIncomeLessThan25(double incomeLessThan25) {
        this.incomeLessThan25 = incomeLessThan25;
    }

    public double getIncomeBetween25to50() {
        return incomeBetween25to50;
    }

    public void setIncomeBetween25to50(double incomeBetween25to50) {
        this.incomeBetween25to50 = incomeBetween25to50;
    }

    public double getIncomeBetween50to100() {
        return incomeBetween50to100;
    }

    public void setIncomeBetween50to100(double incomeBetween50to100) {
        this.incomeBetween50to100 = incomeBetween50to100;
    }

    public double getIncomeBetween100to200() {
        return incomeBetween100to200;
    }

    public void setIncomeBetween100to200(double incomeBetween100to200) {
        this.incomeBetween100to200 = incomeBetween100to200;
    }

    public double getIncomeGreater200() {
        return incomeGreater200;
    }

    public void setIncomeGreater200(double incomeGreater200) {
        this.incomeGreater200 = incomeGreater200;
    }

    public double getEducationHighSchoolGraduate() {
        return educationHighSchoolGraduate;
    }

    public void setEducationHighSchoolGraduate(double educationHighSchoolGraduate) {
        this.educationHighSchoolGraduate = educationHighSchoolGraduate;
    }

    public double getEducationBachelorOrGreater() {
        return educationBachelorOrGreater;
    }

    public void setEducationBachelorOrGreater(double educationBachelorOrGreater) {
        this.educationBachelorOrGreater = educationBachelorOrGreater;
    }

}
