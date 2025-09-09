import Factories.BookingFactory;
import Repositories.BookingApi;
import org.testng.annotations.Test;

public class createBookingTests {

    @Test(priority = 5 ,groups = {"regression" ,"smoke","sanity"})
    public void createValidBooking() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .validateBookingIsCreated();
    }

    @Test(priority = 1,groups = {"regression"})
    public void createBookingWithoutBookingDates() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("missingDates"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }

    @Test(priority = 2 ,groups = {"regression"})
    public void createBookingByInvalidPrice() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("missingPrice"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }


    @Test(priority = 3, groups = {"regression"})
    public void createBookingByEmptyPayload() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("emptyPayload"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }
}



