package resttest.test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import resttest.api.AuthResponse;
import resttest.api.Booking;
import resttest.api.BookingDates;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static resttest.test.BookerTestData.*;

public class BookerTest {
    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;
    private static Integer bookingId;
    private static Booking booking;

    public BookerTest() {
    }

    @BeforeClass
    public void configureLogger() throws FileNotFoundException {
        PrintStream printStream = new PrintStream(new FileOutputStream("log.txt", true));
        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(printStream));
    }

    @BeforeClass
    public void initRequestSpec() {
        requestSpec = new RequestSpecBuilder()
			.setContentType(ContentType.JSON)
			.setBaseUri(BASE_URL)
			.addFilter(new RequestLoggingFilter())
			.addFilter(new ResponseLoggingFilter())
			.build();
    }

    @BeforeClass
    public void initResponseSpec() {
        responseSpec = new ResponseSpecBuilder()
			.expectStatusCode(200)
			.expectContentType(ContentType.JSON)
			.build();
    }

    @Test
    public void testHealthCheckReturns201() {
        given().
		when().
			spec(requestSpec).
			get("/ping").
		then().
			log().all().
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
			log().all().
			assertThat().
			spec(responseSpec).
			body("isEmpty()", Matchers.is(false));
    }

    @Test(dataProvider = "bookingData")
    public void testPostBooking(String firstName, String lastName, int totalPrice, boolean isDepositPaid,
								String checkInDate, String checkOutDate, String additionalNeeds) {
        BookingDates bookingDates = new BookingDates(checkInDate, checkOutDate);

        booking = new Booking.Builder()
			.setFirstName(firstName)
			.setLastName(lastName)
			.setTotalPrice(totalPrice)
			.setDepositPaid(isDepositPaid)
			.setBookingDates(bookingDates)
			.setAdditionalNeeds(additionalNeeds)
			.build();

        Response bookingResponse =
			given().
				spec(requestSpec).
				body(booking).
				post("/booking");

        bookingResponse.
			then().
				log().all().
				assertThat().
				spec(responseSpec).
				body("isEmpty()", Matchers.is(false));
        bookingId = bookingResponse.getBody().jsonPath().get("bookingid");
    }

    @Test(dependsOnMethods = "testPostBooking")
    public void testPartialUpdateExistingBooking() {
        String token = AuthResponse.getToken(BASE_URL, ADM_USERNAME, ADM_PASSWORD);
        given().
			spec(requestSpec).
			header("Cookie", "token=" + token).
			body("{\"totalprice\":" + UPDATED_AMOUNT + "}").
			patch("/booking/" + bookingId).
		then().
			log().all().
			assertThat().
			spec(responseSpec).
			body("totalprice", Matchers.equalTo(UPDATED_AMOUNT));
    }

    @Test(dataProvider = "updatedBookingData", dependsOnMethods = "testPartialUpdateExistingBooking")
    public void testFullUpdateExistingBooking(String firstName, String lastName, int totalPrice, boolean isDepositPaid,
											  String checkInDate, String checkOutDate, String additionalNeeds) {
		String token = AuthResponse.getToken(BASE_URL, ADM_USERNAME, ADM_PASSWORD);
    	BookingDates bookingDates = new BookingDates(checkInDate, checkOutDate);

        booking = new Booking.Builder()
			.setFirstName(firstName)
			.setLastName(lastName)
			.setTotalPrice(totalPrice)
			.setDepositPaid(isDepositPaid)
			.setBookingDates(bookingDates)
			.setAdditionalNeeds(additionalNeeds)
			.build();

        Response bookingResponse =
			given().
				spec(requestSpec).
				header("Cookie", "token=" + token).
				body(booking).
				put("/booking/" + bookingId);

        bookingResponse.
			then().
				log().all().
				assertThat().
				spec(responseSpec).
				body("totalprice", Matchers.equalTo(UPDATED_AMOUNT)).
				body("bookingdates.checkin", Matchers.equalTo(checkInDate)).
				body("bookingdates.checkout", Matchers.equalTo(checkOutDate)).
				body("additionalneeds", Matchers.equalTo("Breakfast"));
    }

    @Test(dependsOnMethods = "testFullUpdateExistingBooking")
	public void testDeleteBooking() {
		String token = AuthResponse.getToken(BASE_URL, ADM_USERNAME, ADM_PASSWORD);
		given().
			spec(requestSpec).
			header("Cookie", "token=" + token).
			delete("/booking/" + bookingId).
		then().
			log().all().
			assertThat().
			statusCode(HttpStatus.SC_CREATED);
	}

	@Test(dependsOnMethods = "testDeleteBooking")
	public void testNegativeNoRecord() {
		given().
			spec(requestSpec).
			get("/booking/" + bookingId).
		then().
			log().all().
			assertThat().
			statusCode(HttpStatus.SC_NOT_FOUND);
	}

    @DataProvider(name = "bookingData")
    public Object[] bookingDataProvider() {
        return new Object[][]{
			{"Alice", "Brown", INITIAL_AMOUNT, true, LocalDate.now().plusDays(1).toString(),
				LocalDate.now().plusDays(5).toString(), ""}
        };
    }

    @DataProvider(name = "updatedBookingData")
    public Object[] updatedBookingDataProvider() {
        return new Object[][]{
			{"Alice", "Brown", UPDATED_AMOUNT, true, LocalDate.now().plusDays(2).toString(),
				LocalDate.now().plusDays(10).toString(), "Breakfast"}
        };
    }
}
