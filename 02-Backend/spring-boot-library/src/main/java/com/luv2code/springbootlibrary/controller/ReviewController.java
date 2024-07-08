package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.requestmodels.ReviewRequest;
import com.luv2code.springbootlibrary.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;


    @PostMapping(path = "/secure")
    public void postReview(@RequestBody ReviewRequest reviewRequest){
        String userEmail = "testuser@gmail.com";
        reviewService.postReview(userEmail, reviewRequest);
    }
    @GetMapping(path = "/secure/user/book")
    public Boolean reviewBookByUser(@RequestParam Long bookId){
        String userEmail = "testuser@gmail.com";
        return reviewService.userReviewListed(userEmail, bookId);
    }
}
