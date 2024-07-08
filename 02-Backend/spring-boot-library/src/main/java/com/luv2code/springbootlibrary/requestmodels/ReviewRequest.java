package com.luv2code.springbootlibrary.requestmodels;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    double rating;
    Long bookId;
    Optional<String> reviewDescription;
}
