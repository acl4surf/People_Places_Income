package com.example.aei2.peopleplaces.dao;

import com.example.aei2.peopleplaces.dto.PeopleDTO;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by aei2 on 2/7/2017.
 * For testing purposes.
 */

public class PeopleDAOStub {

    public PeopleDTO fetchPeopleData(String latitude, String longitude)
            throws IOException, JSONException {
        PeopleDTO people = new PeopleDTO();
        people.setBlockFips(String.valueOf(123456789));
        people.setIncomeBelowPoverty(.10);
        people.setMedianIncome(120000);
        people.setIncomeLessThan25(.10);
        people.setIncomeBetween25to50(.20);
        people.setIncomeBetween50to100(.20);
        people.setIncomeBetween100to200(.30);
        people.setIncomeGreater200(.10);
        people.setEducationHighSchoolGraduate(.90);
        people.setEducationBachelorOrGreater(.25);

        return people;
    }
}
