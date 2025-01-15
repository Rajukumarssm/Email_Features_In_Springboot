package com.example.emailverify.emailVerify.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;


    // In-memory storage for OTPs
    private Map<String, String> otpStore = new HashMap<>();


    private  final String fromMail="rajukumar48035@gmail.com";

    // Method to generate a 4-digit OTP
    private String generateOTP() {
        try {
            Random random = new Random();
            int otp = 1000 + random.nextInt(9000);// Generate a 4-digit OTP
            return String.valueOf(otp);
        } catch (Exception e) {
            throw new RuntimeException("Error generating OTP: " + e.getMessage());
        }
    }

    public ResponseEntity<String>sendEmailWithOTP(String recipientEmail) {
//        try {
//            String otp = generateOTP();  // Generate OTP
//            System.out.println("otp is"+otp);
//            String subject = "Your OTP Verification Code";
//            String body = "Your OTP code for email verification is: " + otp;
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom(fromMail);
//            simpleMailMessage.setSubject(subject);
//            simpleMailMessage.setText(body);
//            simpleMailMessage.setTo(recipientEmail);
//            javaMailSender.send(simpleMailMessage);
//            // Store OTP without expiration
//            otpStore.put(recipientEmail, otp);
//            return ResponseEntity.ok("OTP sent successfully to " + recipientEmail);
//        } catch (Exception e) {
//            // Log the error and return a failed response
//            return ResponseEntity.status(500).body("Error occurred while sending OTP: " + e.getMessage());
//        }

        //second approach use mime---------------------->>>>>
        try {
            String otp = generateOTP();  // Generate OTP
            String subject = "DOC_AID ";
            String body = "Dear user,\n" +
                    "Your OTP is " + otp + ". This is valid for 10 minutes.\n" +
                    "Regards, DOC_AID";

            // Create a MimeMessage for better control
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set the From field to your email address
            helper.setFrom(fromMail);  // Just use the email without any display name

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body);
            otpStore.put(recipientEmail, otp);
            // Send the email
            javaMailSender.send(mimeMessage);

            return ResponseEntity.ok("OTP sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            // Log the error and return a failed response
            return ResponseEntity.status(500).body("Error occurred while sending OTP: " + e.getMessage());

        }
    }

    // Method to verify OTP and remove after successful verification
    public boolean verifyOTP(String recipientEmail, String otp) {
        // Retrieve the stored OTP
        String storedOtp = otpStore.get(recipientEmail);

        if (storedOtp == null) {
            throw new RuntimeException("OTP not found for this email.");
        }
        // Verify if the OTP matches
        if (storedOtp.equals(otp)) {
            otpStore.remove(recipientEmail); // OTP verified, remove it from store
            return true;
        } else {
            return false;
        }
    }
    public void sendInvoiceEmail(String recipientEmail, MultipartFile file) throws MessagingException, IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty. Please upload a valid file.");
        }

        // Handle null or empty filenames
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "attachment_" +LocalDate.now(); // Fallback filename
        }

        // Create a MIME message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Set email details
        helper.setFrom(fromMail);
        helper.setTo(recipientEmail);
        helper.setSubject("Subscription Invoice");
        helper.setText("Please find attached your invoice.");

        // Attach the uploaded file
        helper.addAttachment(originalFilename, file);

        // Send the email
        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MessagingException("Error occurred while sending the email: " + e.getMessage(), e);
        }
    }

}
