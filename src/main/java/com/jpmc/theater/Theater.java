package com.jpmc.theater;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Theater {

    LocalDateProvider provider;
    private final List<Showing> schedule;

    public Theater(LocalDateProvider provider) {
        this.provider = provider;

        Movie spiderMan = new Movie("Spider-Man: No Way Home", Duration.ofMinutes(90), 12.5, 1);
        Movie turningRed = new Movie("Turning Red", Duration.ofMinutes(85), 11, 0);
        Movie theBatMan = new Movie("The Batman", Duration.ofMinutes(95), 9, 0);
        schedule = List.of(
            new Showing(turningRed, 1, LocalDateTime.of(provider.currentDate(), LocalTime.of(9, 0))),
            new Showing(spiderMan, 2, LocalDateTime.of(provider.currentDate(), LocalTime.of(11, 0))),
            new Showing(theBatMan, 3, LocalDateTime.of(provider.currentDate(), LocalTime.of(12, 50))),
            new Showing(turningRed, 4, LocalDateTime.of(provider.currentDate(), LocalTime.of(14, 30))),
            new Showing(spiderMan, 5, LocalDateTime.of(provider.currentDate(), LocalTime.of(16, 10))),
            new Showing(theBatMan, 6, LocalDateTime.of(provider.currentDate(), LocalTime.of(17, 50))),
            new Showing(turningRed, 7, LocalDateTime.of(provider.currentDate(), LocalTime.of(19, 30))),
            new Showing(spiderMan, 8, LocalDateTime.of(provider.currentDate(), LocalTime.of(21, 10))),
            new Showing(theBatMan, 9, LocalDateTime.of(provider.currentDate(), LocalTime.of(23, 0)))
        );
    }

    public Reservation reserve(Customer customer, int sequence, int howManyTickets) {
        Showing showing;
        try {
            showing = schedule.get(sequence - 1);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Not able to find any showing for given sequence " + sequence);
        }
        return new Reservation(customer, showing, howManyTickets);
    }

    /*
    * Print schedule in JSON format.
    * */
    public void printSchedule() {
        JSONObject jsonObj = new JSONObject();
        Map<String, Object> rows;
        JSONArray list = new JSONArray();
        for(Showing s : schedule) {
            rows = new HashMap<>();
            rows.put("sequence", s.getSequenceOfTheDay());
            rows.put("movie", s.getMovie().getTitle());
            rows.put("slot", s.getStartTime().getHour() + ":" + s.getStartTime().getMinute());
            rows.put("duration", humanReadableFormat(s.getMovie().getRunningTime()));
            rows.put("price", "$" + s.getMovieFee());
            list.put(rows);
        }
        jsonObj.put(provider.currentDate().toString(), list);
        System.out.println(jsonObj.toString(4));
    }

    public String humanReadableFormat(Duration duration) {
        long hour = duration.toHours();
        long remainingMin = duration.toMinutes() - TimeUnit.HOURS.toMinutes(duration.toHours());

        return String.format("%s hour%s %s minute%s", hour, handlePlural(hour), remainingMin, handlePlural(remainingMin));
    }

    // (s) postfix should be added to handle plural correctly
    private String handlePlural(long value) {
        if (value == 1) {
            return "";
        }
        else {
            return "s";
        }
    }

    public static void main(String[] args) {
        Theater theater = new Theater(LocalDateProvider.singleton());
        theater.printSchedule();
        Scanner input = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Sequence number you would like to reserve ticket/s for:");
        int seq = input.nextInt();
        System.out.println("Enter number of people:");
        int audience = input.nextInt();
        System.out.println("Enter Customer Name:");
        String name = input.next();
        int id = 1;
        Customer cus = new Customer(name, name+id);
        Reservation reserve = theater.reserve(cus, seq, audience);
        System.out.print("YOUR TOTAL COST: $" + reserve.totalFee());
    }
}
