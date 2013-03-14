/**
 * Created by IntelliJ IDEA.
 * User: Roshan
 * Date: 10/31/12
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;

//Event, EventList and , rv classes adapted and imported from Professor Jason Jue's M/M/1 simulation code in C++

class rv
{
    public static Double Seed = 1111.00;

    public static Double uni_rv()
    {
        Double k = 16807.0;
        Double m = 2.147483647e9;
        Double rv;

        Seed=(k*Seed) % m;
        rv=Seed/m;
        return rv;
    }

    public static Double exp_rv(Double lambda)
    {
        Double exp;
        exp = ((-1) / lambda) * Math.log(uni_rv());
        //System.out.println("exp : " + exp);
        return exp;
    }

}

class Event
{
    Double time;            // Time at which Event takes place
    Integer type;               // Type of Event
    Event next;            // Points to next event in list
    Double timeInterval;

    public Event(Double t, Integer i)
    {
        time = t;
        type = i;
        next = null;
    }

    public Event(Double t, Integer i,Double eTime)
    {
        time = t;
        type = i;
        next = null;
        timeInterval = eTime;
    }

}

class EventList
{
    Event head;           // Points to first Event in EventList
    Integer event_count;       // Total number of Events in EventList

    public EventList() { event_count = 0; head = null;}

    public void insert(Double time, Integer type)
    {
        insert(time, type, 0.0);

    }

    public void insert(Double time, Integer type, Double iTime)
    {
        event_count++;                        // Increment number of events in list.
        Event eptr = new Event(time, type, iTime);
        if (head == null) {                      // If EventList is empty,
            head = eptr;                        // put new event at head of list.
            eptr.next = null;
        }
        else if (head.time >= eptr.time) {  // If the event is earlier than
            eptr.next = head;                  // all existing events, place it
            head = eptr;                        // at the head of the list.
        }
        else {                                // Otherwise, search for the
            Event eindex;                      // correct location sorted by time.
            eindex = head;
            while (eindex.next != null) {
                if (eindex.next.time < eptr.time) {
                    eindex = eindex.next;
                }
                else {
                    break;
                }
            }
            eptr.next = eindex.next;
            eindex.next = eptr;
        }

    }

    public Event get()
    {
        if (event_count == 0) {
            return null;
        }
        else {
            event_count--;
            Event eptr;
            eptr = head;
            head = head.next;
            eptr.next = null;
            return eptr;
        }
    }

    public void clear()
    {
        Event eptr;
        while(head != null)
        {
            eptr = head;
            head = head.next;
            eptr.next = null;
            //delete eptr;
        }
        event_count = 0;
    }

    public Event remove(Integer type)
    {
        if (event_count == 0 || head == null) {
            return null;
        }
        else {
            Event eptr;
            Event eptr_prev = null;
            eptr = head;

            while(eptr != null){
                if (eptr.type == type) {
                    if (eptr_prev == null) {
                        head = eptr.next;
                        eptr.next = null;
                        return eptr;
                    }
                    else {
                        eptr_prev.next = eptr.next;
                        eptr.next = null;
                        return eptr;
                    }
                }
                else {
                    eptr_prev = eptr;
                    eptr = eptr.next;
                }
            }
            return null;
        }
    }

}

public class QueueingSystem  {

    public static void main(String args[]) throws IOException
    {

        final int ARR = 0, DEP = 1;                 // Define the event types

        EventList Elist = new EventList();                // Create event list
        EventList BufferQueue = new EventList();
        Integer bufferQueueAvailability;

        Double mu = 1.0;                // Service rate
        Integer K = 0;
        Integer m = 0;
        Integer L = 0;

        Integer mAvailabilityCount;

        Double clock = 0.0;             // System clock
        Integer N = 0;                      // Number of customers in system
        Integer Ndep = 0;                   // Number of departures from system
        Double EN = 0.0;                // For calculating E[N]

        Integer done = 0;                   // End condition satisfied?

        List<Double> lambdaList = Arrays.asList(0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0);

        Event CurrentEvent;

        System.out.println("Enter K : ");

        BufferedReader in1=new BufferedReader(new InputStreamReader(System.in));
        String temp=in1.readLine();
        K = Integer.parseInt(temp);

        do {
            System.out.println("Enter m (Value should be less than K) : ");

            in1=new BufferedReader(new InputStreamReader(System.in));
            temp=in1.readLine();
            m = Integer.parseInt(temp);

        } while (m>K);

        System.out.println("Enter L : ");
        in1=new BufferedReader(new InputStreamReader(System.in));
        temp=in1.readLine();
        L = Integer.parseInt(temp);

        System.out.println("Enter mu : ");
        in1=new BufferedReader(new InputStreamReader(System.in));
        temp=in1.readLine();
        mu = Double.parseDouble(temp);

        for (Double lambda:lambdaList)
        {
            System.out.println("LAMBDA :: "+lambda);
            clock = 0.0;             // System clock
            N = 0;                      // Number of customers in system
            Ndep = 0;                   // Number of departures from system
            EN = 0.0;                // For calculating E[N]
            done = 0;                   // End condition satisfied?

            mAvailabilityCount = m;
            bufferQueueAvailability = K - m;

            BufferQueue.clear();
            Elist.clear();

            for (int i=0; i<L; i++)
            {
                Elist.insert(rv.exp_rv(lambda), ARR); // Generate first L arrival event
            }
            Double iter = 0.0;
            Double nTotal = 0.0;

            Double totalTime = 0.0;

            Double totalArrivalCount = 0.0;
            Double totalBlockCount = 0.0;
            Double totalUtil = 0.0;

            while (done != 1)
            {
                CurrentEvent = Elist.get();               // Get next Event from list
                iter++;
                nTotal += N;
                Double prev = clock;                      // Store old clock value
                clock=CurrentEvent.time;                 // Update system clock
                totalUtil += (m - mAvailabilityCount)/m*(clock-prev);
                //               System.out.println("System Size : "+N+"/"+K);
                // System.out.println("mAvailabilityCount : " + mAvailabilityCount+"/"+m);
                // System.out.println("bufferQueueAvailability : "+ bufferQueueAvailability+"/"+(K-m));

                switch (CurrentEvent.type) {
                    case ARR:                                 // If arrival
                        if (mAvailabilityCount > 0)
                        {                                           // If server is available
                            Double departureTime = clock+rv.exp_rv(mu);
                            Double timeInterval = clock + rv.exp_rv(mu) - CurrentEvent.time;       //wrong
                            Elist.insert(departureTime,DEP,timeInterval);   // , generate departure
                            mAvailabilityCount--;
                            EN += N*(clock-prev);                   //  update system statistics
                            N++;

                        }
                        else if (bufferQueueAvailability > 0)
                        {
                            BufferQueue.insert(clock,ARR);
                            bufferQueueAvailability--;
                            EN += N*(clock-prev);                   //  update system statistics
                            N++;
                        }
                        else
                        {
                            EN += N*(clock-prev);                   //  update system statistics
                            Elist.insert(clock+rv.exp_rv(lambda),ARR);
                            totalBlockCount ++;
                        }
                        totalArrivalCount++;
                        // System.out.println("N : " +N);

                        break;

                    case DEP:                                 // If departure

                        totalTime += CurrentEvent.timeInterval;
                        EN += N*(clock-prev);                   //  update system statistics
                        N--;

                        //  decrement system size
                        //  System.out.println("N : " +N);
                        Ndep++;                                 //  increment num. of departures

                        //Elist.insert(clock+rv.exp_rv(mu),DEP);   //  generate next departure

                        if(bufferQueueAvailability == (K-m))
                        {
                            mAvailabilityCount++;
                        }
                        else
                        {
                            bufferQueueAvailability++;
                            Double etime = BufferQueue.get().time;
                            Double ftime = clock+rv.exp_rv(mu);
                            Elist.insert(ftime ,DEP,ftime-etime);
                        }
                        Elist.insert(clock+rv.exp_rv(lambda),ARR);

                        break;
                }
                //delete CurrentEvent;
                if (Ndep > 100000) done=1;        // End condition
            }
            //           System.out.println(" || " +nTotal +":"+iter);
//            System.out.println(" || Clock : " + clock);

            //   //    System.out.println(" || Avg System Size : " + (nTotal/iter));
            System.out.println(" || Avg Time : " + (totalTime/Ndep));
            System.out.println(" || Blocking Fraction : " + (totalBlockCount/totalArrivalCount));

//            System.out.println(" || Current number of customers in system: " + N);
            System.out.println(" || Average System Size : " + EN/clock);
            System.out.println(" || Utilization : " + totalUtil/clock);
            System.out.println();
        }
    }

}
