package com.accenture.hr.slots;

import com.accenture.hr.Statuses;
import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.ResgisterResponse;
import com.accenture.hr.responses.StatusResponse;
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
    private int counter = 0;
    private static final Logger log = LoggerFactory.getLogger(SlotService.class);
    private final int currentLimit;
    private int currentFreePlaces;
    private final List<Person> peopleInsideList = new ArrayList<>();
    private final List<Person> peopleWaitingList = new ArrayList<>();

    @Autowired
    public SlotService(Integer currentLimit) {
        this.currentLimit = 250 * currentLimit / 100;
        this.currentFreePlaces = this.currentLimit;
        counter++;
    }

    //enter once/day
    public ResgisterResponse register(Long userId) {
        Person personOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personOnInsideList = searchByUserIdOnInsideList(userId);
        ResgisterResponse resgisterResponse = new ResgisterResponse();
        if (personOnWaitingList != null && personOnInsideList != null) {
            log.error("User is already in building! UserId: {}", userId);
            resgisterResponse.setStatus(Statuses.ALREADY_IN_BUILDING);
        } else if (personOnWaitingList != null) {
            log.error("User is already on waiting list! UserId: {}", userId);
            resgisterResponse.setStatus(Statuses.ALREADY_ON_WAITINGLIST);
        } else if (personOnInsideList == null) {
            Person person = new Person(userId,counter);
            counter++;
            if (person.getSerial() <= currentLimit) {
                peopleInsideList.add(person);
                currentFreePlaces--;
                //log.debug("You can enter the building whenever you want! UserId: {}", userId);
                resgisterResponse.setStatus(Statuses.CAN_ENTER);
            } else {
                int positionInLine = calculatePositionInQueue(person);
                log.debug("Your position in the line is: {}.", positionInLine);
                resgisterResponse.setStatus(Statuses.SUCCESS);
                peopleWaitingList.add(person);
            }
        }
        return resgisterResponse;
    }

    public StatusResponse status(long userId) {
        Person personOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personOnInsideList = searchByUserIdOnInsideList(userId);
        StatusResponse statusResponse = new StatusResponse();
        if (personOnInsideList != null){
            if (personOnInsideList.getSerial()<currentLimit){
                log.debug("You can enter the building whenever you want! UserId: {}", userId);
                statusResponse.setStatus(Statuses.CAN_ENTER);
            }else {
                log.error("This person is already inside. UserId: {}", userId);
                statusResponse.setStatus(Statuses.ALREADY_IN_BUILDING);
            }
        }else if(personOnWaitingList != null){
            int positionInLine = calculatePositionInQueue(personOnWaitingList);
            if (positionInLine <= currentFreePlaces){
                log.debug("You can enter the building whenever you want! UserId: {}", userId);
                statusResponse.setStatus(Statuses.CAN_ENTER);
            }else{
                positionInLine -= currentFreePlaces;
                log.debug("Your position in the line is: {}.", positionInLine);
                statusResponse.setStatus(Statuses.POSITION_IN_LINE);
                statusResponse.setQueuePosition(positionInLine);
            }
        }else{
            log.error("You have to register first. UserId: {}", userId);
            statusResponse.setStatus(Statuses.NOT_REGISTERED);
        }
        return statusResponse;
    }

    public EntryResponse entry(Long userId) {
        peopleInsideList.sort(new PersonComparator());
        Person personToEnterOnWaitingList = searchByUserIdOnWaitingList(userId);
        Person personToEnterOnInsideList = searchByUserIdOnInsideList(userId);
        EntryResponse entryResponse = new EntryResponse();
        if (personToEnterOnInsideList != null) {
            if (personToEnterOnInsideList.getSerial() < currentLimit) {
                log.debug("Successfully entered the building. UserId: {}", userId);
                entryResponse.setStatus(Statuses.SUCCESS);
            } else {
                log.error("This person is already inside. UserId: {}", userId);
                entryResponse.setStatus(Statuses.ALREADY_IN_BUILDING);
            }
        } else if (personToEnterOnWaitingList != null) {
            int positionInLine = calculatePositionInQueue(personToEnterOnWaitingList);
            if (positionInLine <= currentFreePlaces) {
                peopleInsideList.add(personToEnterOnWaitingList);
                peopleWaitingList.remove(personToEnterOnWaitingList);
                currentFreePlaces--;
                log.debug("Successfully entered the building. UserId: {}", userId);
                entryResponse.setStatus(Statuses.SUCCESS);
            } else {
                log.error("Cannot enter now. UserId: {}", personToEnterOnWaitingList.getUserId());
                //log.error("Your Position in line is: {}", positionInLine);
                entryResponse.setStatus(Statuses.WAIT_MORE);
            }
        } else {
            log.error("You have to register first. UserId: {}", userId);
            entryResponse.setStatus(Statuses.NOT_REGISTERED);
        }
        return entryResponse;
    }

    public ExitResponse exit(Long userId) {
        ExitResponse exitResponse = new ExitResponse();
        Person personToExit = searchByUserIdOnInsideList(userId);
        if (personToExit != null) {
            peopleInsideList.remove(personToExit);
            currentFreePlaces++;
            log.debug("Successfully leaved the building. UserId: {}", userId);
            exitResponse.setStatus(Statuses.SUCCESS);
        } else {
            log.error("This person isn't inside the building. UserId: {}", userId);
            exitResponse.setStatus(Statuses.NOT_INSIDE);
        }
        return exitResponse;

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
        peopleInsideList.sort(new PersonComparator());
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

