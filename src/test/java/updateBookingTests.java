import Factories.BookingFactory;
import Listners.TestNGListeners;
import Repositories.BookingApi;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestNGListeners.class})
public class updateBookingTests {

    @Test(priority = 1)
    public void updateBookingWithoutAuthorization() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"), "none")
                .validateAuthResponse(403, "Forbidden");
    }

    @Test(priority = 2)
    public void updateBookingWithInvalidToken() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"), "invalid")
                .validateAuthResponse(403, "Forbidden");
    }

    // @Test(priority = 3)
    public void updateBookingWithExpiredToken() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"), "expired")
                .validateAuthResponse(403, "Forbidden");
    }

    @Test(priority = 4)
    public void updateValidBooking() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"), "valid")
                .validateUpdateBookingResponse()
                .validateBookingIsUpdated();
    }
}
