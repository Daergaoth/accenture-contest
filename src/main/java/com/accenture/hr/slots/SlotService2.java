package com.accenture.hr.slots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SlotService2 {

    private static final Logger log = LoggerFactory.getLogger(SlotService2.class);

    private Integer currentLimit;
    private Map<Long,LocalDateTime> peopleInside = new HashMap<>();
    private Map<Long,LocalDateTime> peopleWaiting = new HashMap<>();


    @Autowired
    public SlotService2(Integer currentLimit,
                        Map<Long,LocalDateTime> peopleInside,
                        Map<Long,LocalDateTime> peopleWaiting)
    {
        this.currentLimit = currentLimit;
        this.peopleInside = peopleInside;
        this.peopleWaiting = peopleWaiting;
    }



    public void registerRequest(Long userId) {

        if (peopleInside.containsKey(userId)) {
            log.error("User is already in building! UserId: {}", userId);
        } else if (peopleWaiting.containsKey(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
        } else {
            if (peopleInside.size() < currentLimit) {
                peopleInside.put(userId,LocalDateTime.now());
                log.debug("User checked into building! UserId: {}", userId);
            } else {
                peopleWaiting.put(userId,LocalDateTime.now());
                log.debug("User placed on waitinglist! UserId: {}", userId);
            }
        }
    }
}
