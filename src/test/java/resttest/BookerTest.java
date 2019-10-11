package resttest;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.apache.http.HttpStatus;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static resttest.BookerTestData.*;
import static resttest.api.BookingDates.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import io.restassured.response.Response;
import io.restassured.specification.*;
import resttest.api.BookingDates;
import resttest.api.Booking;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookerTest {
	private static RequestSpecification requestSpec;
	private static ResponseSpecification responseSpec;
	private static Integer bookingId;
	private static Booking booking;
	
	private static final Logger LOG = LoggerFactory.getLogger(BookerTest.class);
	
	@BeforeClass
	public void initRequestSpec() {
		requestSpec = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
	            .setBaseUri(BASE_URL)
	            .addFilter(new ResponseLoggingFilter())
	            .addFilter(new RequestLoggingFilter())
	            .build();	
	}
	
	@BeforeClass
    public void initResponseSpec() {
 	    responseSpec = new ResponseSpecBuilder().
 	    		expectStatusCode(200).
 	    		expectContentType(ContentType.JSON).
 	    		build();
    }
	
    @Test
    public void testHealthCheckReturns201() {
        given().
        when().
        spec(requestSpec).
        	get("/ping").
        then().
        	assertThat().
        	statusCode(HttpStatus.SC_CREATED);
    }
	
	
	@Test
	public void testGetAllBookings() {
        given().
        when().
        spec(requestSpec).
            get("/booking").
        then().
            assertThat().
            spec(responseSpec).
            body("isEmpty()", Matchers.is(false));
        
        LOG.info("All bookings were successfully received");
	}
	
	@Test(dataProvider = "bookingData")
	public void testPostBooking(String firstName, String lastName, int totalPrice, boolean isDepositPaid, String checkInDate, String checkOutDate, String additionalNeeds) {
        BookingDates bookingDates = new BookingDates(checkInDate, checkOutDate);

        booking = new Booking.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setTotalPrice(totalPrice)
                .setDepositPaid(isDepositPaid)
                .setBookingDates(bookingDates)
                .setAdditionalNeeds(additionalNeeds)
                .build();

        Response bookingResponse = given()
        	.spec(requestSpec)
        	.body(booking)
        	.post("/booking");

        bookingId = bookingResponse.getBody().jsonPath().get("bookingid");
        LOG.info("New booking has been successfully created with ID = " + bookingId);       
	}
	
	@DataProvider(name="bookingData")
	public Object[] bookingDataProvider() {
		return new Object[][] {
            { "Alice", "Brown", 512, true, LocalDate.now().plusDays(1).toString(), LocalDate.now().plusDays(5).toString(), "" },
        };
	}
}
