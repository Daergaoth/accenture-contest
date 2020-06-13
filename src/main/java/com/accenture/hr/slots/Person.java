package com.accenture.hr.slots;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class Person implements Comparable<Person>{
    private final LocalDateTime registerTime;
    private final long userId;
    private static int counter = 0;
    private final int serial;


    public Person(long userId) {
        this.registerTime = LocalDateTime.now();
        this.userId = userId;
        counter++;
        serial = Person.counter;
    }

    public long getUserId() {
        return userId;
    }

    public int getSerial() {
        return serial;
    }

    public static void setCounter(int counter) {
        Person.counter = counter;
    }

    @Override
    public int compareTo(Person person) {
        if (this.userId < person.userId) {
            return -1;
        } else if (this.userId > person.userId) {
            return 1;
        }
        return 0;
    }
}
