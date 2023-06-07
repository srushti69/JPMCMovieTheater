package com.jpmc.theater;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Movie {
    private final static int MOVIE_CODE_SPECIAL = 1;

    private final String title;
    private String description;
    private final Duration runningTime;
    private final double ticketPrice;
    private final int specialCode;

    public Movie(String title, Duration runningTime, double ticketPrice, int specialCode) {
        this.title = title;
        this.runningTime = runningTime;
        this.ticketPrice = ticketPrice;
        this.specialCode = specialCode;
    }

    public String getTitle() {
        return title;
    }

    public Duration getRunningTime() {
        return runningTime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public double calculateTicketPrice(Showing showing) {
        return ticketPrice - getDiscount(showing.getSequenceOfTheDay(), showing.getStartTime());
    }

    private double getDiscount(int showSequence, LocalDateTime ldt) {
        double specialDiscount = 0;
        double specialTimeDiscount = 0;
        double sequenceDiscount = 0;
        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        if(hour>= 11 && (hour <= 15 || (hour == 16 && minute == 0))) {
            specialTimeDiscount = ticketPrice * 0.25;   // 25% discount for showtime starting between 11AM - 4PM
        }
        if (MOVIE_CODE_SPECIAL == specialCode) {
            specialDiscount = ticketPrice * 0.2;  // 20% discount for special movie
        }

        switch(showSequence) {
            case 1:
                sequenceDiscount = 3; // $3 discount for 1st show
                break;
            case 2:
                sequenceDiscount = 2; // $2 discount for 2nd show
                break;
            case 7:
                sequenceDiscount = 1; // $1 discount for 7th show
                break;
        }

        // biggest discount wins
        return Math.max(specialDiscount, Math.max(sequenceDiscount, specialTimeDiscount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Double.compare(movie.ticketPrice, ticketPrice) == 0
                && Objects.equals(title, movie.title)
                && Objects.equals(description, movie.description)
                && Objects.equals(runningTime, movie.runningTime)
                && Objects.equals(specialCode, movie.specialCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, runningTime, ticketPrice, specialCode);
    }
}