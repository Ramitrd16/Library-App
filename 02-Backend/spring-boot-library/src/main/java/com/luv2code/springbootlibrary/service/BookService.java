package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.BookRepository;
import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.entity.Payment;
import com.luv2code.springbootlibrary.exceptions.DemyBookException;
import com.luv2code.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final PaymentRepository paymentRepository;

    public Book checkoutBook(String userEmail, Long bookId) throws ParseException {
        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (!book.isPresent() || !ObjectUtils.isEmpty(validateCheckout) || book.get().getCopiesAvailable() < 1) {
            throw new DemyBookException("Book not found exception");
        }


        boolean bookNeedReturned = false;

        List<Checkout> currentBooksCheckout = checkoutRepository.findBooksByUserEmail((userEmail));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d2 = sdf.parse(LocalDate.now().toString());

        for(Checkout checkout : currentBooksCheckout ){
            Date d1 = sdf.parse(checkout.getReturnDate());

            TimeUnit time = TimeUnit.DAYS;
            double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
            if(differenceInTime<0){
                bookNeedReturned = true;
                break;
            }
        }
        Payment userPayment = paymentRepository.findByUserEmail(userEmail);
        if((userPayment!=null && userPayment.getAmount()>0)|| (userPayment!=null || bookNeedReturned)){
            throw new DemyBookException("outstanding balance");
        }

        if(userPayment==null){
            Payment payment = new Payment();
            payment.setAmount(00.00);
            payment.setUserEmail(userEmail);
            paymentRepository.save(payment);
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(userEmail,
                LocalDateTime.now().toString(),
                LocalDateTime.now().plusDays(7).toString(),
                book.get().getId());

        checkoutRepository.save(checkout);
        return book.get();
    }

    public boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout checkout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (checkout != null) {
            return true;
        }
        return false;
    }

    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansResponse> sHelfCurrentLoansResponses = new ArrayList<>();
        List<Checkout> checkoutLists = checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = new ArrayList<>();
        checkoutLists.forEach(checkout -> bookIdList.add(checkout.getBookId()));

        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutLists.stream()
                    .filter(x -> x.getBookId() == book.getId()).findFirst();

            if (checkout.isPresent()) {
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());

                TimeUnit timeUnit = TimeUnit.DAYS;
                long difference_in_Time = timeUnit.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                sHelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) difference_in_Time));
            }
        }
        return sHelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, Long bookId) throws ParseException {
        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (!book.isPresent() || validateCheckout == null) {
            throw new DemyBookException("Book doesnot exist");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        bookRepository.save(book.get());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(validateCheckout.getReturnDate());
        Date d2 = sdf.parse(LocalDate.now().toString());
        TimeUnit  timeUnit = TimeUnit.DAYS;
        double timeDifference = timeUnit.convert(d1.getTime()-d2.getTime(), TimeUnit.MILLISECONDS);
        if(timeDifference<0){
            Payment payment = paymentRepository.findByUserEmail(userEmail);
            payment.setAmount(payment.getAmount()+(timeDifference* -1));
            paymentRepository.save(payment);
        }
        checkoutRepository.deleteById(validateCheckout.getId());
    }

    public void renewLoan(String userEmail, Long bookId) throws ParseException {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if(validateCheckout == null){
            throw new DemyBookException("Book doesn't exist");
        }

        SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdfFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdfFormat.parse(LocalDate.now().toString());

        if(d1.compareTo(d2)>0 || d1.compareTo(d2)==0){
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }

}



























