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
                .createBooking(BookingFactory.getBooking("validBooking"))
                .validateBookingIsCreated()
                .updateBooking(BookingFactory.getBooking("validBooking"))
                .validateBookingIsUpdated()
                .deleteBooking()
                .validateBookingIsDeleted();
    }
}
