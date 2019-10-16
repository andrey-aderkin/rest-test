package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("totalprice")
    private int totalPrice;

    @JsonProperty("depositpaid")
    private boolean depositPaid;

    @JsonProperty("bookingdates")
    private BookingDates bookingDates;

    @JsonProperty("additionalneeds")
    private String additionalNeeds;

    private Booking(String firstName, String lastName, int totalPrice, boolean depositPaid, BookingDates bookingDates,
                    String additionalNeeds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalPrice = totalPrice;
        this.depositPaid = depositPaid;
        this.bookingDates = bookingDates;
        this.additionalNeeds = additionalNeeds;
    }

    public static class Builder {

        private String firstName;
        private String lastName;
        private int totalPrice;
        private boolean depositPaid;
        private BookingDates bookingDates;
        private String additionalNeeds;

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder setDepositPaid(boolean depositPaid) {
            this.depositPaid = depositPaid;
            return this;
        }

        public Builder setBookingDates(BookingDates bookingDates) {
            this.bookingDates = bookingDates;
            return this;
        }

        public Builder setAdditionalNeeds(String additionalNeeds) {
            this.additionalNeeds = additionalNeeds;
            return this;
        }

        public Booking build() {
            return new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, additionalNeeds);
        }
    }
}
