import Factories.BookingFactory;
import Repositories.BookingApi;
import org.testng.annotations.Test;

public class e2e {

    @Test(groups = {"smoke", "sanity", "regression"})
    public void testBookingLifecycle() {
        new BookingApi()
                .createBooking(BookingFactory.getCreatedBooking("validBooking"))
                .validateCreateBookingResponse()
                .validateBookingIsCreated()
                .updateBooking(BookingFactory.getUpdatedBooking("validBooking"), "valid")
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
