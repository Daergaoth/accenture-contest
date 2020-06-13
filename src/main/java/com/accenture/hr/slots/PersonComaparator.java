package com.accenture.hr.slots;

import java.util.Comparator;

public class PersonComaparator implements Comparator<Person> {
    @Override
    public int compare(Person personFirst, Person personSecond) {
        return personFirst.compareTo(personSecond);
    }
}
