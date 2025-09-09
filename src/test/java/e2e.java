import Factories.BookingFactory;
import Listners.TestNGListeners;
import Repositories.BookingApi;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestNGListeners.class})
public class e2e {

    @Test
    public void testBookingLifecycle() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .validateBookingIsCreated()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"))
                .validateUpdateBookingResponse()
                .validateBookingIsUpdated()
                .partialUpdateBooking(BookingFactory.getPatchedBooking("updateName"))
                .validatePatchBookingResponse()
                .validateBookingIsPatched("updateName")
                .partialUpdateBooking(BookingFactory.getPatchedBooking("updateDates"))
                .validatePatchBookingResponse()
                .validateBookingIsPatched("updateDates")
                .deleteBooking()
                .validateBookingIsDeleted();
    }
}
