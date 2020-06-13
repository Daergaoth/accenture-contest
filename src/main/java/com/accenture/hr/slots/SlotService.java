package com.accenture.hr.slots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class SlotService {

    private static final Logger log = LoggerFactory.getLogger(SlotService.class);

    private final int currentLimit;
    private int currentFreePlaces;
    private List<Person> peopleInsideList = new ArrayList<>();
    private List<Person> peopleWaitingList = new ArrayList<>();

    @Autowired
    public SlotService(Integer currentLimit, List<Person> peopleInsideList, List<Person> peopleWaitingList) {
        this.currentLimit = 250 * currentLimit / 100;
        this.peopleInsideList = peopleInsideList;
        this.peopleWaitingList = peopleWaitingList;
        this.currentFreePlaces = this.currentLimit;
    }

    //enter once/day
    public void register(Long userId) {
        Person personOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personOnInsideList = searchByUserIdOnInsideList(userId);
        if (personOnWaitingList != null && personOnInsideList != null) {
            log.error("User is already in building! UserId: {}", userId);
        } else if (personOnWaitingList != null && personOnInsideList == null) {
            log.error("User is already on waiting list! UserId: {}", userId);
        } else if (personOnWaitingList == null && personOnInsideList == null) {
            Person person = new Person(userId);
            peopleWaitingList.add(person);
            if (person.getSerial() <= currentLimit) {
                peopleInsideList.add(person);
                currentFreePlaces--;
                log.debug("You can enter the building whenever you want! UserId: {}", userId);
            } else {
                int positionInLine = calculatePositionInQueue(person);
                log.debug("Your position in the line is: {}.", positionInLine);
            }
        }
    }

    public void status(long userId) {
        Person personOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personOnInsideList = searchByUserIdOnInsideList(userId);
        if (personOnInsideList != null){
            if (personOnInsideList.getSerial()<currentLimit){
                log.debug("You can enter the building whenever you want! UserId: {}", userId);
            }else {
                log.error("This person is already inside. UserId: {}", userId);
            }
        }else if(personOnWaitingList != null){
            int positionInLine = calculatePositionInQueue(personOnWaitingList);
            if (positionInLine <= currentFreePlaces){
                log.debug("You can enter the building whenever you want! UserId: {}", userId);
            }else{
                positionInLine -= currentFreePlaces;
                log.debug("Your position in the line is: {}.", positionInLine);
            }
        }else{
            log.error("You have to register first. UserId: {}", userId);
        }
    }

    public void entry(Long userId) {
        peopleInsideList.sort(new PersonComaparator());
        Person personToEnterOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personToEnterOnInsideList = searchByUserIdOnInsideList(userId);
        if (personToEnterOnInsideList != null) {
            if (personToEnterOnInsideList.getSerial() < currentLimit) {
                log.debug("Successfully entered the building. UserId: {}", userId);
            } else {
                log.error("This person is already inside. UserId: {}", userId);
            }
        } else if (personToEnterOnWaitingList != null) {
            int positionInLine = calculatePositionInQueue(personToEnterOnWaitingList);
            if (positionInLine <= currentFreePlaces) {
                peopleInsideList.add(personToEnterOnWaitingList);
                currentFreePlaces--;
                log.debug("Successfully entered the building. UserId: {}", userId);
            } else {
                log.error("Cannot enter now. UserId: {}", personToEnterOnWaitingList.getUserId());
                log.error("Your Position in line is: {}", positionInLine);
            }
        } else {
            log.error("You have to register first. UserId: {}", userId);
        }
    }

    public void exit(Long userId) {
        Person personToExit = searchByUserIdOnInsideList(userId);
        if (personToExit != null) {
            peopleInsideList.remove(personToExit);
            currentFreePlaces++;
            log.debug("Successfully leaved the building. UserId: {}", userId);
        } else {
            log.error("This person isn't inside the building. UserId: {}", userId);
        }

    }

    private Person searchByUserIdOnInsideList(Long userId) {
        for (Person person : peopleInsideList) {
            if (person.getUserId() == userId) {
                return person;
            }
        }
        return null;
    }

    private int calculatePositionInQueue(Person person) {
        peopleInsideList.sort(new PersonComaparator());
        Person lastPersonEntered;
        try {
            lastPersonEntered = peopleInsideList.get(peopleInsideList.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            return 1;
        }
        return (person.getSerial() - lastPersonEntered.getSerial());
    }

    private Person searchByUserIdOnWaitingList(Long userId) {
        for (Person person : peopleWaitingList) {
            if (person.getUserId() == userId) {
                return person;
            }
        }
        return null;
    }

    public Integer getCurrentLimit() {
        return currentLimit;
    }

    public List<Person> getPeopleInsideList() {
        return peopleInsideList;
    }

    public List<Person> getPeopleWaitingList() {
        return peopleWaitingList;
    }
}

