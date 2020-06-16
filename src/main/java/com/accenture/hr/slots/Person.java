package com.accenture.hr.slots;

public class Person {

    private final Long userId;

    private final int serial;


    public Person(Long userId, int counter) {

        this.userId = userId;
        serial = counter;
    }

    public long getUserId() {
        return userId;
    }

    public int getSerial() {
        return serial;
    }

}
