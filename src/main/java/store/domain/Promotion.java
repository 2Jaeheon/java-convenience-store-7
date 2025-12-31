package store.domain;

import java.time.LocalDate;

public class Promotion {
    private String name;
    private int buy;
    private int get;
    private LocalDate endDate;
    private LocalDate startDate;

    public Promotion(String name, int buy, int get, String startDate, String endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    public String getName() {
        return name;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
