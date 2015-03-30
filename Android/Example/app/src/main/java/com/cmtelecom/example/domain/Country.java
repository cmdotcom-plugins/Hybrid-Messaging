package com.cmtelecom.example.domain;

import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Dion on 13/01/15
 */
public class Country  {

    private String countryName;
    private String isoCountryName;
    private int countryCode;

    public Country(String isoCountryName, int countryCode) {
        this.countryName = new Locale("", isoCountryName).getDisplayCountry();
        this.isoCountryName = isoCountryName;
        this.countryCode = countryCode;
    }

    public String getIsoCountryName() {
        return isoCountryName;
    }

    public void setIsoCountryName(String isoCountryName) {
        this.isoCountryName = isoCountryName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public String getCountryCodeAsString() {
        return "+" + countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public static class CustomComparator implements Comparator<Country> {
        @Override
        public int compare(Country o1, Country o2) {
            return o1.getCountryName().compareTo(o2.getCountryName());
        }
    }

}
