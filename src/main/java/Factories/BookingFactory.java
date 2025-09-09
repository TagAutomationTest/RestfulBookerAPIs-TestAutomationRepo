package Factories;

import Pojos.BookingDto;
import Utils.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BookingFactory {
    static ObjectMapper mapper;
    static InputStream input;
    private static JsonNode bookingData;
    private static BookingDto booking;
    private static Map<String, String> bookingDates;
    private static Map<String, Object> patchPayload;

    static {
        try {
            mapper = new ObjectMapper();
            input = BookingFactory.class
                    .getClassLoader()
                    .getResourceAsStream("testdata/bookings.json");
            bookingData = mapper.readTree(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bookings.json", e);
        }
    }

    public static BookingDto getCreatedBooking(String scenarioKey) {
        try {
            bookingDates = DateUtils.getBookingDates(0, 0);
            booking = mapper.treeToValue(bookingData.get(scenarioKey), BookingDto.class);
            if (scenarioKey.contains("valid")) {
                booking.setFirstname(booking.getFirstname() + "_" + UUID.randomUUID().toString().substring(0, 6));
                booking.setLastname(booking.getLastname() + "_" + System.currentTimeMillis());
                booking.setBookingdates(
                        new BookingDto.BookingDates(bookingDates.get("checkin"), bookingDates.get("checkout"))
                );
            }
            return booking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map booking for key: " + scenarioKey, e);
        }
    }

    public static BookingDto getUpdatedBooking(String scenarioKey) {
        bookingDates = DateUtils.getBookingDates(7, 14);
        booking = getCreatedBooking(scenarioKey);
        // Example: modify some fields to simulate an update
        booking.setFirstname("Updated_" + booking.getFirstname());
        booking.setLastname("Updated_" + booking.getLastname());
        booking.setTotalprice(
                booking.getTotalprice() != null ? booking.getTotalprice() + 500 : 500
        );
        // ✅ Update booking dates
        booking.setBookingdates(
                new BookingDto.BookingDates(bookingDates.get("checkin"), bookingDates.get("checkout"))
        );
        booking.setAdditionalneeds("Updated_" + System.currentTimeMillis());

        return booking;
    }

    public static Map<String, Object> getPatchedBooking(String scenarioKey) {
        patchPayload = new HashMap<>();

        if (scenarioKey.equalsIgnoreCase("updateName")) {
            patchPayload.put("firstname", "Patched_" + UUID.randomUUID().toString().substring(0, 4));
            patchPayload.put("lastname", "Patched_" + System.currentTimeMillis());
        }

        if (scenarioKey.equalsIgnoreCase("updatePrice")) {
            patchPayload.put("totalprice", 1500);
        }

        if (scenarioKey.equalsIgnoreCase("updateNeeds")) {
            patchPayload.put("additionalneeds", "Late Checkout");
        }
        if (scenarioKey.equalsIgnoreCase("updateDates")) {
            bookingDates=DateUtils.getBookingDates(2, 4);
            patchPayload.put("bookingdates", bookingDates);
        }
        return patchPayload;
    }
}


