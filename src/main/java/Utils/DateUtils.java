package Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Generate booking dates with checkin and checkout dynamically
     * @param checkinOffset days from today for checkin
     * @param checkoutOffset days from today for checkout
     * @return Map<String, String> with "checkin" and "checkout"
     */
    public static Map<String, String> getBookingDates(int checkinOffset, int checkoutOffset) {
        LocalDate today = LocalDate.now();

        Map<String, String> bookingDates = new HashMap<>();
        bookingDates.put("checkin", today.plusDays(checkinOffset).format(FORMATTER));
        bookingDates.put("checkout", today.plusDays(checkoutOffset).format(FORMATTER));

        return bookingDates;
    }
}
