package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingDates {

    @JsonProperty("checkin")
    private String checkInDate;

    @JsonProperty("checkout")
    private String checkOutDate;

    public BookingDates(String checkInDate, String checkOutDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
    
    public BookingDates() {
    	
    }
}

