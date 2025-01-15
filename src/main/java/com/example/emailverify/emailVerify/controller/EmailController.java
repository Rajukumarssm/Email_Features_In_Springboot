package com.example.emailverify.emailVerify.controller;


import com.example.emailverify.emailVerify.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    EmailService emailService;

    @PostMapping("/sendOTP")
    public ResponseEntity<String> sendOTP(@RequestParam String recipientEmail) {
        try {
            // Call the service to send OTP and return its response
            return emailService.sendEmailWithOTP(recipientEmail);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(500).body("Error occurred while processing OTP request: " + e.getMessage());
        }
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<String> verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        try {
            // Call the service to verify OTP and get the result
            boolean isVerified = emailService.verifyOTP(email, otp);

            if (isVerified) {
                return ResponseEntity.ok("OTP Verified Successfully!");
            } else {
                return ResponseEntity.status(400).body("Invalid OTP!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while verifying OTP: " + e.getMessage());
        }
    }

    @PostMapping("/sendInvoice")
    public ResponseEntity<String> sendInvoiceFile(
            @RequestParam String recipientEmail,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Call the service to send the email
            emailService.sendInvoiceEmail(recipientEmail, file);
            return ResponseEntity.ok("File sent successfully to " + recipientEmail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(500).body("Error occurred while sending the file: " + e.getMessage());
        }
    }
}