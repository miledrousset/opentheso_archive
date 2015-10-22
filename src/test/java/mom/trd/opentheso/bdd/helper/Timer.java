/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import java.util.TimerTask;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import it.sauronsoftware.cron4j.Scheduler;
import mom.trd.opentheso.core.cron.TimerExportThesaurus;

/**
 *
 * @author miled.rousset
 */
public class Timer {
    
    public Timer() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @org.junit.Test
    public void testCron() {
    
        // Prepares the task.
        TimerExportThesaurus task = new TimerExportThesaurus(null,null);
        // Creates the scheduler.
        Scheduler scheduler = new Scheduler();
        /*
        Following are examples of crontab expressions and how they would interpreted as a recurring schedule.

            * * * * *
            This pattern causes a task to be launched every minute.

            5 * * * *
            This pattern causes a task to be launched once every hour and at the fifth minute of the hour (00:05, 01:05, 02:05 etc.).

            * 12 * * Mon
            This pattern causes a task to be launched every minute during the 12th hour of Monday.

            * 12 16 * Mon
            This pattern causes a task to be launched every minute during the 12th hour of Monday, 16th, but only if the day is the 16th of the month.

            59 11 * * 1,2,3,4,5
            This pattern causes a task to be launched at 11:59AM on Monday, Tuesday, Wednesday, Thursday and Friday. Every sub-pattern can contain two or more comma separated values.

            59 11 * * 1-5
        */
        // Schedules the task, once every minute.
        scheduler.schedule("* * * * *", task);
        // Starts the scheduler.
        scheduler.start();
        // Stays alive for five minutes.
    /*    try {
            Thread.sleep(5L * 60L * 1000L);
        } catch (InterruptedException e) {
            System.err.println("erreur");
        }*/
        // Stops the scheduler.
        scheduler.stop();
    }
}
