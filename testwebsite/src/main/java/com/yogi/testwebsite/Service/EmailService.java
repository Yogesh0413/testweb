package com.yogi.testwebsite.Service;

import com.yogi.testwebsite.Entity.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${hotel.mail.from}")
    private String fromAddress;

    @Value("${hotel.mail.name}")
    private String fromName;

    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a booking confirmation email to the guest.
     * Marked @Async so it runs in a background thread and
     * does not slow down the booking response.
     */
    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(booking.getGuest().getEmail());
            helper.setSubject("Booking Confirmed — " + booking.getConfirmationCode()
                    + " | " + fromName);

            helper.setText(buildHtmlEmail(booking), true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            // Log but don't throw — a failed email should not
            // roll back a successful booking
            System.err.println("[EmailService] Failed to send confirmation email: "
                    + e.getMessage());
        }
    }

    /**
     * Sends a simple notification to the hotel admin
     * whenever a new booking is made.
     */
    @Async
    public void sendAdminNotification(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(fromAddress); // notify the hotel's own inbox
            helper.setSubject("New Booking: " + booking.getConfirmationCode()
                    + " — " + booking.getRoom().getName());

            helper.setText(buildAdminEmail(booking), true);

            mailSender.send(message);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("[EmailService] Failed to send admin notification: "
                    + e.getMessage());
        }
    }

    // ── Email templates ──────────────────────────────────────────────────

    private String buildHtmlEmail(Booking booking) {
        String gold   = "#c9a96e";
        String bg     = "#08090a";
        String card   = "#141618";
        String text   = "#f2ede6";
        String muted  = "#7a756c";
        String border = "#222";

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
            <title>Booking Confirmed</title>
        </head>
        <body style="margin:0;padding:0;background:%s;font-family:'Helvetica Neue',Arial,sans-serif;">

          <div style="max-width:600px;margin:0 auto;padding:40px 20px;">

            <!-- Header -->
            <div style="text-align:center;padding-bottom:32px;border-bottom:1px solid %s;">
              <p style="color:%s;font-size:11px;letter-spacing:3px;text-transform:uppercase;margin:0 0 8px;">
                ✦ Reservation Confirmed
              </p>
              <h1 style="color:%s;font-size:28px;font-weight:300;margin:0;font-style:italic;">
                %s
              </h1>
            </div>

            <!-- Greeting -->
            <div style="padding:32px 0 24px;">
              <p style="color:%s;font-size:16px;margin:0 0 12px;">
                Dear %s,
              </p>
              <p style="color:%s;font-size:14px;line-height:1.7;margin:0;">
                Your reservation has been confirmed. We look forward to welcoming you.
                Please keep your confirmation code safe — you will need it to manage your booking.
              </p>
            </div>

            <!-- Confirmation Code -->
            <div style="background:%s;border:1px solid %s;border-left:3px solid %s;
                        padding:20px 24px;margin-bottom:24px;text-align:center;">
              <p style="color:%s;font-size:10px;letter-spacing:2px;text-transform:uppercase;
                        margin:0 0 8px;">Confirmation Code</p>
              <p style="color:%s;font-size:32px;font-weight:300;letter-spacing:4px;margin:0;">
                %s
              </p>
            </div>

            <!-- Booking Details -->
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="border:1px solid %s;margin-bottom:24px;">
              <tr style="background:%s;">
                <td colspan="2" style="padding:14px 20px;border-bottom:1px solid %s;">
                  <p style="color:%s;font-size:10px;letter-spacing:2px;
                             text-transform:uppercase;margin:0;">Room</p>
                  <p style="color:%s;font-size:16px;margin:4px 0 0;">%s</p>
                  <p style="color:%s;font-size:12px;margin:2px 0 0;">%s View · %s m²</p>
                </td>
              </tr>
              %s
              %s
              %s
              %s
              %s
            </table>

            <!-- Special Requests -->
            %s

            <!-- Policies -->
            <div style="background:%s;border:1px solid %s;padding:20px 24px;margin-bottom:32px;">
              <p style="color:%s;font-size:10px;letter-spacing:2px;
                        text-transform:uppercase;margin:0 0 12px;">Good to Know</p>
              <p style="color:%s;font-size:13px;line-height:1.7;margin:0 0 6px;">
                ✦ Check-in from <strong style="color:%s;">3:00 PM</strong>
              </p>
              <p style="color:%s;font-size:13px;line-height:1.7;margin:0 0 6px;">
                ✦ Check-out by <strong style="color:%s;">12:00 PM</strong>
              </p>
              <p style="color:%s;font-size:13px;line-height:1.7;margin:0;">
                ✦ Free cancellation up to <strong style="color:%s;">48 hours</strong> before arrival
              </p>
            </div>

            <!-- Footer -->
            <div style="text-align:center;border-top:1px solid %s;padding-top:24px;">
              <p style="color:%s;font-size:12px;margin:0 0 6px;">%s</p>
              <p style="color:%s;font-size:12px;margin:0 0 6px;">
                Questions? Reply to this email or call
                <span style="color:%s;">+1 (800) 000-0000</span>
              </p>
              <p style="color:%s;font-size:11px;margin:16px 0 0;letter-spacing:1px;">
                ✦ &nbsp; THE GRAND HORIZON &nbsp; ✦
              </p>
            </div>

          </div>
        </body>
        </html>
        """.formatted(
            bg, border,                                         // body, header border
            gold, text, fromName,                               // eyebrow, h1
            text, booking.getGuest().getFirstName(),            // greeting name
            muted,                                              // greeting body
            card, border, gold,                                 // code box bg/border/left
            muted, gold,                                        // code label, code value
            booking.getConfirmationCode(),
            border,                                             // table border
            card, border,                                       // room row bg, border
            muted, text,                                        // room label, name
            booking.getRoom().getName(),
            muted,                                              // room meta
            booking.getRoom().getView(),
            (int) booking.getRoom().getSizeSqm(),
            detailRow("Check In",   booking.getCheckIn().format(DISPLAY),  border, muted, text),
            detailRow("Check Out",  booking.getCheckOut().format(DISPLAY), border, muted, text),
            detailRow("Nights",     booking.getNights() + (booking.getNights() == 1 ? " night" : " nights"), border, muted, text),
            detailRow("Guests",     booking.getGuestCount() + (booking.getGuestCount() == 1 ? " guest" : " guests"), border, muted, text),
            detailRow("Total",      "$" + String.format("%,.0f", booking.getTotalPrice()), border, gold, gold),
            booking.getSpecialRequests() != null && !booking.getSpecialRequests().isBlank()
                ? specialRow(booking.getSpecialRequests(), card, border, muted, text)
                : "",
            card, border,                                       // policies box
            gold,                                               // policies label
            muted, text, muted, text, muted, text,              // policy lines
            border,                                             // footer border
            muted, fromName,                                    // footer hotel name
            muted, gold,                                        // contact line
            gold                                                // footer mark
        );
    }

    private String detailRow(String label, String value,
                              String border, String labelColor, String valueColor) {
        return """
            <tr>
              <td style="padding:12px 20px;border-bottom:1px solid %s;width:40%%;">
                <p style="color:%s;font-size:10px;letter-spacing:1.5px;
                           text-transform:uppercase;margin:0;">%s</p>
              </td>
              <td style="padding:12px 20px;border-bottom:1px solid %s;">
                <p style="color:%s;font-size:14px;margin:0;font-weight:400;">%s</p>
              </td>
            </tr>
            """.formatted(border, labelColor, label, border, valueColor, value);
    }

    private String specialRow(String requests,
                               String bg, String border, String labelColor, String textColor) {
        return """
            <div style="background:%s;border:1px solid %s;
                        padding:16px 20px;margin-bottom:24px;">
              <p style="color:%s;font-size:10px;letter-spacing:2px;
                        text-transform:uppercase;margin:0 0 8px;">Special Requests</p>
              <p style="color:%s;font-size:13px;line-height:1.7;margin:0;">%s</p>
            </div>
            """.formatted(bg, border, labelColor, textColor, requests);
    }

    private String buildAdminEmail(Booking booking) {
        return """
            <html><body style="font-family:Arial,sans-serif;padding:20px;">
              <h2 style="color:#c9a96e;">New Booking Received</h2>
              <table cellpadding="8" cellspacing="0" style="border-collapse:collapse;width:100%%;">
                <tr><td style="border:1px solid #ddd;"><strong>Code</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Guest</strong></td>
                    <td style="border:1px solid #ddd;">%s — %s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Room</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Check In</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Check Out</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Nights</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Guests</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Total</strong></td>
                    <td style="border:1px solid #ddd;">$%,.0f</td></tr>
                <tr><td style="border:1px solid #ddd;"><strong>Special Requests</strong></td>
                    <td style="border:1px solid #ddd;">%s</td></tr>
              </table>
            </body></html>
            """.formatted(
                booking.getConfirmationCode(),
                booking.getGuest().getFullName(), booking.getGuest().getEmail(),
                booking.getRoom().getName(),
                booking.getCheckIn().format(DISPLAY),
                booking.getCheckOut().format(DISPLAY),
                booking.getNights(),
                booking.getGuestCount(),
                booking.getTotalPrice(),
                booking.getSpecialRequests() != null ? booking.getSpecialRequests() : "None"
        );
    }
}
