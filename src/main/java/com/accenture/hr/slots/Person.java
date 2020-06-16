package com.accenture.hr.slots;

import org.springframework.stereotype.Component;
@Component
public class Person{

    private final long userId;

    private final int serial;


    public Person(long userId,int counter) {

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
