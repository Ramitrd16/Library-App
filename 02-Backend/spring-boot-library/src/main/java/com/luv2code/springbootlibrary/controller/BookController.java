package com.luv2code.springbootlibrary.controller;


import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import com.luv2code.springbootlibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    public static final String USER_EMAIL = "testuser@gmail.com";
    private final BookService bookService;

    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans() throws Exception {
        return bookService.currentLoans(USER_EMAIL);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(){
        return bookService.currentLoansCount(USER_EMAIL);
    }

    @GetMapping("/secure/ischeckout/byuser")
    public Boolean ifCheckoutBuUser(@RequestParam Long bookId){
        return bookService.checkoutBookByUser(USER_EMAIL, bookId);
    }
    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestParam Long bookId) throws ParseException {
        return bookService.checkoutBook(USER_EMAIL, bookId);
    }

    @PutMapping("/secure/return")
    public void returnBook(@RequestParam Long bookId) throws ParseException {
        bookService.returnBook(USER_EMAIL, bookId);
    }
    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestParam Long bookId) throws ParseException {
        bookService.renewLoan(USER_EMAIL, bookId);
    }


}
