package com.accenture.hr.slots;

import java.util.Comparator;

public class PersonComparator implements Comparator<Person> {
    @Override
    public int compare(Person personFirst, Person personSecond) {
        return ((Integer) personFirst.getSerial()).compareTo(personSecond.getSerial());
    }
}
