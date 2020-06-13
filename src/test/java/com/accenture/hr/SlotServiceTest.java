package com.accenture.hr;

import com.accenture.hr.slots.Person;
import com.accenture.hr.slots.SlotService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    private Integer currentLimit = 10;
    private List<Person> peopleInside = new ArrayList<>();
    private List<Person> peopleWaiting = new ArrayList<>();
    private SlotService slotService = new SlotService(currentLimit, peopleInside, peopleWaiting);



    @BeforeEach
    private void init() {
        slotService = new SlotService(currentLimit, peopleInside, peopleWaiting);
    }

    @AfterEach
    public void resetCounter(){
        Person.setCounter(0);
    }
    //Register Tests---------------------------------------------------------------
    @Test
    public void testRegister_hasSpace_registers() {
        long userId = 1L;
        slotService.register(userId);

        Assertions.assertEquals(1, slotService.getPeopleInsideList().size());
    }

    @Test
    public void testRegister_hasSpace_alreadyInside_logsError() {
        long userId = 1L;
        slotService.register(userId);
        slotService.register(userId);

        Assertions.assertEquals(1, slotService.getPeopleInsideList().size());
    }

    @Test
    public void testRegister_noSpace_putsOnWaitingList() {
        for (int i = 0; i < slotService.getCurrentLimit() + 1; i++) {
            slotService.register((long) i);
        }

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(26, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testRegister_noSpace_alreadyOnWaitingList_logsError() {
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.register(30L);
        slotService.register(30L);
        slotService.register(40L);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(27, slotService.getPeopleWaitingList().size());
    }

    //Exit Tests-----------------------------------------------------------
    @Test
    public void testExit_success(){
        long userId = 1L;
        slotService.register(userId);
        slotService.exit(userId);
        Assertions.assertEquals(0, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(1, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testExit_samePersonTryExitTwice_logErrors(){
        long userId = 1L;
        slotService.register(userId);
        slotService.exit(userId);
        slotService.exit(userId);
        Assertions.assertEquals(0, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(1, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testExit_TryExitBeforeEntry_logErrors(){
        long userId = 1L;
        slotService.exit(userId);
        Assertions.assertEquals(0, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(0, slotService.getPeopleWaitingList().size());
    }

    //Entry Tests------------------------------------------------------------

    @Test
    public void testEntry_success(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.exit((long)15);
        long userId = 150;
        slotService.register(userId);
        slotService.entry(userId);
        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(26, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testEntry_tryEnterTwice(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.exit((long)15);
        long userId = 150;
        slotService.register(userId);
        slotService.entry(userId);
        slotService.entry(userId);
        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(26, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testEntry_tryEnterWithoutRegister(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.exit((long)15);
        long userId = 150;
        slotService.entry(userId);

        Assertions.assertEquals(slotService.getCurrentLimit()-1, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(25, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testEntry_inBoundOfCurrentLimit(){
        long userId = 150;
        slotService.register(userId);
        slotService.entry(userId);

        Assertions.assertEquals(1, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(1, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testEntry_tryEnterWithToBehindInLine(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        long userId = 150;
        slotService.register(userId);
        slotService.entry(userId);
        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(26, slotService.getPeopleWaitingList().size());
    }

    //Status Tests---------------------------------------------------
    @Test
    public void testStatus_inBoundOfCurrentLimit(){
        long userId = 150;
        long userId2 = 250;
        long userId3 = 350;
        long userId4 = 450;
        slotService.register(userId);
        slotService.register(userId2);
        slotService.register(userId3);
        slotService.register(userId4);
        slotService.status(userId);

        Assertions.assertEquals(4, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(4, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testStatus_outBoundOfCurrentLimit(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        long userId = 150;
        long userId2 = 250;
        long userId3 = 350;
        long userId4 = 450;
        slotService.register(userId);
        slotService.register(userId2);
        slotService.register(userId3);
        slotService.register(userId4);
        slotService.status(userId);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(29, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testStatus_alreadyEntered_error(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.exit((long)15);
        long userId = 150;
        long userId2 = 250;
        long userId3 = 350;
        long userId4 = 450;
        slotService.register(userId);
        slotService.register(userId2);
        slotService.register(userId3);
        slotService.register(userId4);
        slotService.entry(userId);
        slotService.status(userId);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(29, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testStatus_notEnteredYet(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        slotService.exit((long)15);
        long userId = 150;
        long userId2 = 250;
        long userId3 = 350;
        long userId4 = 450;
        slotService.register(userId);
        slotService.register(userId2);
        slotService.register(userId3);
        slotService.register(userId4);
        slotService.status(userId);

        Assertions.assertEquals(slotService.getCurrentLimit()-1, slotService.getPeopleInsideList().size());
        Assertions.assertEquals(29, slotService.getPeopleWaitingList().size());
    }

    @Test
    public void testStatus_notRegisteredYet_error(){
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.register((long) i);
        }
        long userId = 150;
        long userId2 = 250;
        long userId3 = 350;
        long userId4 = 450;
        slotService.register(userId2);
        slotService.register(userId3);
        slotService.register(userId4);
        slotService.status(userId);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInsideList().size());
        Assertions.assertEquals(28, slotService.getPeopleWaitingList().size());
    }

}

