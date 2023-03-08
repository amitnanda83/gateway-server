package com.amit.gateway.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objects containing errors details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestError {

    /**
     * Code of the error raised
     */
    String errorCode;

    /**
     * Message for the error raised
     */
    String errorMessage;
}
