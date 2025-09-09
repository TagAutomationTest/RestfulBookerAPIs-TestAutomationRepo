import Factories.BookingFactory;
import Listners.TestNGListeners;
import Repositories.BookingApi;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestNGListeners.class})
public class createBookingTests {

    @Test(priority = 5)
    public void createValidBooking() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .validateBookingIsCreated();
    }

    @Test(priority = 1)
    public void createBookingWithoutBookingDates() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("missingDates"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }

    @Test(priority = 2)
    public void createBookingByInvalidPrice() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("missingPrice"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }


    @Test(priority = 3)
    public void createBookingByEmptyPayload() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("emptyPayload"))
                .validateBookingIsNotCreated(500, "Internal Server Error");
    }
}



