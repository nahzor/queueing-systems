/**
 * Created by IntelliJ IDEA.
 * User: Roshan
 * Date: 10/31/12
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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
    Integer priority;       //high = 1, low = 0
    Integer queue;
    Event next;            // Points to next event in list
    Double timeInterval;

    public Event(Double t, Integer i, Integer p, Integer q)
    {
        time = t;
        type = i;
        priority = p;
        queue = q;
        next = null;
    }

    public Event(Double t, Integer i, Integer p, Integer q, Double eTime)
    {
        time = t;
        type = i;
        priority = p;
        queue = q;
        next = null;
        timeInterval = eTime;
    }

}

class EventList
{
    Event head;           // Points to first Event in EventList
    Integer event_count;       // Total number of Events in EventList

    public EventList() { event_count = 0; head = null;}

    public void insert(Double time, Integer type, Integer priority, Integer queue)
    {
       // System.out.println("Time :" + time);
        insert(time, type, priority, queue,0.0);

    }

    public void insert(Double time, Integer type, Integer priority, Integer queue, Double iTime)
    {
        event_count++;                        // Increment number of events in list.
        Event eptr = new Event(time, type,priority, queue, iTime);
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

    static Double pH = (2.0/5);
    static Double pL;
    static Double r2d;
    static Double a2;
    static Double a1 = 0.5;
    static Double mu1 = 40.0;
    static Double mu2 = 50.0;
    static Double r21 =(3.0/4);
    static Double lambdaH;
    static Double lambdaL;
    static Double highPriorityEventCountForQueue1;
    static Double lowPriorityEventCountForQueue1;
    static Double highPriorityEventCountForQueue2;
    static Double lowPriorityEventCountForQueue2;

       static Double highQ1Total = 0.0;
       static Double lowQ1Total = 0.0;
       static Double highQ2Total = 0.0;
       static Double lowQ2Total = 0.0;

       static Double highQ1 = 0.0;
       static Double lowQ1 = 0.0;
       static Double highQ2 = 0.0;
       static Double lowQ2 = 0.0;

    static final int ARR = 0, DEP = 1;                 // Define the event types
    static final int HIGH = 1, LOW = 0;
    static final int Q1 = 1, Q2 = 2;

    static EventList Elist = new EventList();                // Create event list for queues

    public static void getUserInputs() throws IOException
    {
        System.out.println("Enter pH : ");

               BufferedReader in1=new BufferedReader(new InputStreamReader(System.in));
               String temp=in1.readLine();
               pH = Double.parseDouble(temp);

        System.out.println("Enter a1 : ");

               in1=new BufferedReader(new InputStreamReader(System.in));
               temp=in1.readLine();
               a1 = Double.parseDouble(temp);

        System.out.println("Enter mu1 : ");

               in1=new BufferedReader(new InputStreamReader(System.in));
               temp=in1.readLine();
               mu1 = Double.parseDouble(temp);

        System.out.println("Enter mu2 : ");

               in1=new BufferedReader(new InputStreamReader(System.in));
               temp=in1.readLine();
               mu2 = Double.parseDouble(temp);

        System.out.println("Enter r21 : ");

               in1=new BufferedReader(new InputStreamReader(System.in));
               temp=in1.readLine();
               r21 = Double.parseDouble(temp);

    }

    public static Double performNewInsert(Double clock)
    {
        Double returnValue = 0.0;
        //perform insertion
        //public void insert(Double time, Integer type, Integer priority, Integer queue)
        if(rv.uni_rv() <= pH)
        {
         //   System.out.println("New Insert : HIGH");
           if(rv.uni_rv() <= a1)
           {
               highPriorityEventCountForQueue1++;
               Elist.insert(returnValue = (clock+rv.exp_rv(lambdaH)),ARR,HIGH,Q1);
           }
           else
           {
               Elist.insert(returnValue = (clock+rv.exp_rv(lambdaH)),ARR,HIGH,Q2);
               highPriorityEventCountForQueue2++;

           }

        }
        else
        {
            Elist.insert(returnValue = (clock + rv.exp_rv(lambdaL)), ARR, LOW, Q1);
            lowPriorityEventCountForQueue1++;
       //     System.out.println("New Insert : LOW");
        }
        return returnValue;

    }

    public static void main(String args[]) throws IOException
    {
        Double clock = 0.0;             // System clock
        Integer N = 0;                      // Number of customers in system
        Integer Ndep = 0;                   // Number of departures from system
        Double lambdaa;
        Integer iter;
        Integer maxArrivals = 5000  ;
        Integer maxDepartures = 5000 ;
        Double ENQ1 = 0.0;
        Double ENQ2 = 0.0;

        Double ENQ1Low = 0.0;
        Double ENQ2Low = 0.0;
        Double ENQ1High = 0.0;
        Double ENQ2High = 0.0;

        Double Q1Prev = 0.0;
        Double Q2Prev = 0.0;
        Double Q1Size = 0.0;
        Double Q2Size = 0.0;

        List<Double> lambdaList = Arrays.asList(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0);

        Event CurrentEvent;

        QueueingSystem.getUserInputs();

        a2 = 1 - a1;
        pL = 1 - pH;
        r2d = 1 - r21;

        for (Double lambda:lambdaList)
        {
            lambdaa = lambda;// 10.0;
            System.out.println("LAMBDA :: "+lambdaa);

            lambdaH = lambdaa * pH;
            lambdaL = lambdaa * pL;
            System.out.println("LAMBDAH :: "+lambdaH);
            System.out.println("LAMBDAL :: "+lambdaL);


            clock = 0.0;             // System clock
            N = 0;                      // Number of customers in system
            Ndep = 0;                   // Number of departures from system
            iter = 0;
            ENQ1 = 0.0;
            ENQ2 = 0.0;
            Q1Prev = 0.0;
            Q2Prev = 0.0;
            Q1Size = 0.0;
            Q2Size = 0.0;
            highQ1Total = 0.0;
            lowQ1Total = 0.0;
            highQ2Total = 0.0;
            lowQ2Total = 0.0;

            highQ1 = 0.0;
            lowQ1 = 0.0;
            highQ2 = 0.0;
            lowQ2 = 0.0;

            highPriorityEventCountForQueue1 = 0.0;
            lowPriorityEventCountForQueue1 = 0.0;
            highPriorityEventCountForQueue2 = 0.0;
            lowPriorityEventCountForQueue2 = 0.0;

        ENQ1Low = 0.0;
        ENQ2Low = 0.0;
        ENQ1High = 0.0;
        ENQ2High = 0.0;

            Elist.clear();

            performNewInsert(clock); // Generate first arrival event
            Integer counter = 0;
            while (counter < maxArrivals)
            {
                clock = performNewInsert(clock);     //inserting an arrival for each clock tick
                counter++;
            }
        System.out.println("Counter : Clock :: " + counter+":"+clock);
            Double prev;
            while (Elist.head != null && (Ndep < maxDepartures))
            {

                CurrentEvent = Elist.get();               // Get next Event from list
                iter++;
                prev = clock;
                clock = CurrentEvent.time;                 // Update system clock

            //    System.out.println(" || Iter : " + iter);
                switch (CurrentEvent.type) {

                    case ARR:                                 // If arrival

                        if(CurrentEvent.queue == Q1)
                        {
                            ENQ1 += ((Q1Size)*(clock-prev));
                            Q1Prev = clock;
                            Q1Size++;

                           if(highPriorityEventCountForQueue1 > 0)//CurrentEvent.priority == HIGH)
                           {
                               //ARRH1
                               Elist.insert(clock+rv.exp_rv(mu1),DEP,HIGH,Q1);
                               highPriorityEventCountForQueue1--;

                               ENQ1High += highQ1*(clock-prev);
                               highQ1Total++; highQ1++;
                           }
                           else if (lowPriorityEventCountForQueue1 > 0)
                           {
                               //ARRL1
                               Elist.insert(clock+rv.exp_rv(mu1),DEP,LOW,Q1);
                               lowPriorityEventCountForQueue1--;

                               ENQ1Low += lowQ1*(clock-prev);
                               lowQ1Total++;lowQ1++;

                           }

                        }
                        else         //For Q2
                        {
                            ENQ2 += ((Q2Size)*(clock-prev));
                            Q2Prev = clock;
                            Q2Size++;

                         if(highPriorityEventCountForQueue2 > 0)//CurrentEvent.priority == HIGH)
                           {
                                  //ARRH2
                               Elist.insert(clock+rv.exp_rv(mu2),DEP,HIGH,Q2);
                                highPriorityEventCountForQueue2--;

                               ENQ2High += highQ2*(clock-prev);
                               highQ2Total++;  highQ2++;
                           }
                           else if (lowPriorityEventCountForQueue2 > 0)
                           {
                                  //ARRL2
                               Elist.insert(clock+rv.exp_rv(mu2),DEP,LOW,Q2);
                               lowPriorityEventCountForQueue2--;

                               ENQ2Low += lowQ2*(clock-prev);
                               lowQ2Total++;  lowQ2++;

                           }

                        }

                       // if(iter < maxArrivals) performNewInsert(clock);     //inserting an arrival for each clock tick

                        break;

                    case DEP:                                 // If departure

                          if(CurrentEvent.queue == Q1)
                           {
                               ENQ1 += ((Q1Size)*(clock-prev));
                               Q1Prev = clock;
                               Q1Size--;
                                //DEPQ1
                                if(CurrentEvent.priority == HIGH)
                                  {
                                        //create ARRH2
                                     Elist.insert(clock,ARR,CurrentEvent.priority,Q2);
                                     highPriorityEventCountForQueue2++;
                                      ENQ1High += highQ1*(clock-prev);
                                     highQ1--;

                                  }
                                  else
                                  {
                                         //create ARRL2
                                      Elist.insert(clock,ARR,CurrentEvent.priority,Q2);
                                      lowPriorityEventCountForQueue2++;
                                      ENQ1Low += lowQ1*(clock-prev);
                                      lowQ1--;

                                  }
                              // if(iter < maxArrivals) performNewInsert(clock);     //inserting an arrival for each clock tick

                           }
                           else    //Q2
                           {
                               ENQ2 += ((Q2Size)*(clock-prev));
                               Q2Prev = clock;
                               Q2Size--;

                                  //DEPQ2
                               if( rv.uni_rv() <= r21 )
                               {
                                   //insert(Double time, Integer type, Integer priority, Integer queue)
                                  Elist.insert(clock, ARR, CurrentEvent.priority, Q1);

                                   if(CurrentEvent.priority == HIGH)
                                   {
                                       highPriorityEventCountForQueue1++;
                                       ENQ1High += highQ2*(clock-prev);
                                       highQ2--;

                                   }
                                   else
                                   {
                                       lowPriorityEventCountForQueue1 ++;
                                       ENQ2Low += lowQ2*(clock-prev);
                                       lowQ2--;
                                   }

                               }
                               else
                               {
                                  // if(iter < maxArrivals)performNewInsert(clock);     //inserting an arrival for each clock tick
                                   Ndep++;
                                   //System.out.println(Ndep);
                               }
                           }

                        break;

            }     //switch case

           } //while loop
            System.out.println(" || NDep : " + Ndep);
            System.out.println(" || iter : " + iter);
            System.out.println(" || Clock : " + clock);

            System.out.println(" || ENQ1 : " + 4*ENQ1/clock);
            System.out.println(" || ENQ2 : " + 4*ENQ2/clock);

            System.out.println(" || HQ1 : " + 2*(highQ1Total/clock));
            System.out.println(" || HQ2 : " + 2*(highQ2Total/clock));
            System.out.println(" || LQ1 : " + 2*(lowQ1Total/clock));
            System.out.println(" || LQ2 : " + 2*(lowQ2Total/clock));

            System.out.println(" || ENQ1H : " + (ENQ1High/(500*clock)));
            System.out.println(" || ENQ2H : " + (ENQ1High/clock));
            System.out.println(" || ENQ1L : " + (ENQ1Low/clock));
            System.out.println(" || ENQ2L : " + (ENQ2Low/(500*clock)));




            System.out.println();


        }  //for loop for looping over lambdas
    }//main
}//class
