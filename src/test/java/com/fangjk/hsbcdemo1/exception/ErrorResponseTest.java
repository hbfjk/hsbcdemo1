/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.fangjk.hsbcdemo1.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    private ErrorResponse errorResponse;
    private String message;
    private List<String> details;

    @BeforeEach
    public void setUp() {
        message = "Error occurred";
        details = Arrays.asList("Detail 1", "Detail 2", "Detail 3");
        errorResponse = new ErrorResponse(message, details);
    }

    @Test
    public void testConstructor() {
        // Verify the constructor properly initializes the message and details
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
    }

    @Test
    public void testGetMessage() {
        // Verify getter for message
        assertEquals(message, errorResponse.getMessage());
    }

    @Test
    public void testSetMessage() {
        // Verify setter for message
        String newMessage = "New error message";
        errorResponse.setMessage(newMessage);
        assertEquals(newMessage, errorResponse.getMessage());
    }

    @Test
    public void testGetDetails() {
        // Verify getter for details
        assertEquals(details, errorResponse.getDetails());
    }

    @Test
    public void testSetDetails() {
        // Verify setter for details
        List<String> newDetails = Arrays.asList("New Detail 1", "New Detail 2");
        errorResponse.setDetails(newDetails);
        assertEquals(newDetails, errorResponse.getDetails());
    }

    @Test
    public void testEmptyDetails() {
        // Verify the case where details are empty
        List<String> emptyDetails = Arrays.asList();
        errorResponse.setDetails(emptyDetails);
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    public void testNullDetails() {
        // Verify setting null details
        errorResponse.setDetails(null);
        assertNull(errorResponse.getDetails());
    }
}