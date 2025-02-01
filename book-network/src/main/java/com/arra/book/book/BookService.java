package com.arra.book.book;

import com.arra.book.common.PageResponse;
import com.arra.book.exception.OperationNotPermittedException;
import com.arra.book.history.BookTransactionHistory;
import com.arra.book.history.BookTransactionHistoryRepository;
import com.arra.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService  {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Integer save(BookRequest request, Authentication connectedUser) {
        //User user = ((User) connectedUser.getPrincipal());
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBookById(Integer bookId) {
        // get the book and convert it to an BookResponse
        return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // build the pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));             // sort by createdDate attribute (BaseEntity)
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getUserId());
        // convert to BookResponse
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        // return PageResponse which contains books and metadata about pagination
        return new PageResponse<>(bookResponses, books.getNumber(), books.getSize(),
                books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    // functionality with using specification (BookSpecification) and JpaSpecificationExecutor interface instead of query
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // build the pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));             // sort by createdDate attribute (BaseEntity)
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getUserId()), pageable);
        // convert to BookResponse
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        // return PageResponse which contains books and metadata about pagination
        return new PageResponse<>(bookResponses, books.getNumber(), books.getSize(),
                books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }


    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // build the pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));             // sort by createdDate attribute (BaseEntity)
        Page<BookTransactionHistory> bookTransactionHistory = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getUserId());
        // convert to BorrowedBookResponse
        List<BorrowedBookResponse> bookTransactionHistoryList =  bookTransactionHistory.stream().map(bookMapper::toBorrowedBookResponse).toList();
        // return PageResponse which contains books and metadata about pagination
        return new PageResponse<>(bookTransactionHistoryList, bookTransactionHistory.getNumber(), bookTransactionHistory.getSize(),
                bookTransactionHistory.getTotalElements(), bookTransactionHistory.getTotalPages(), bookTransactionHistory.isFirst(),
                bookTransactionHistory.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // build the pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));             // sort by createdDate attribute (BaseEntity)
        Page<BookTransactionHistory> bookTransactionHistory = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getUserId());
        // convert to BorrowedBookResponse
        List<BorrowedBookResponse> bookTransactionHistoryList =  bookTransactionHistory.stream().map(bookMapper::toBorrowedBookResponse).toList();
        // return PageResponse which contains books and metadata about pagination
        return new PageResponse<>(bookTransactionHistoryList, bookTransactionHistory.getNumber(), bookTransactionHistory.getSize(),
                bookTransactionHistory.getTotalElements(), bookTransactionHistory.getTotalPages(), bookTransactionHistory.isFirst(),
                bookTransactionHistory.isLast());
    }


    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
        // checking
        // only the user of the book is allowed to update the book
        if(!Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You are not allowed to update the book shareable status!");
        }
        // inverse the value
        book.setShareable(!book.isShareable());
        // save new status
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
        // checking
        // only the user of the book is allowed to update the book
        if(!Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You are not allowed to update the book archived status!");
        }
        // inverse the value
        book.setArchived(!book.isArchived());
        // save new status
        bookRepository.save(book);
        return bookId;
    }


    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
        // checking if book is archived and shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The book cannot be borrowed!");
        }
        // check if user is not the same as the owner (borrow my own book)
        if(Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You cannot borrow your own book!");
        }
        // check if book is already borrowed
        final boolean isBookAlready = bookTransactionHistoryRepository.isBookAlreadyBorrowedByUser(bookId, user.getUserId());
        if(isBookAlready){
            throw new OperationNotPermittedException("You cannot borrow your own book!");
        }
        // create a new transaction
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        // save transaction and return transaction id
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
        // checking if book is archived and shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The book cannot be returned!");
        }
        // check if user is not the same as the owner (return own book)
        if(Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You cannot return your own book!");
        }
        // check if book is already borrowed by the user
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
                .findByBookIdAndUserId(book.getId(), user.getUserId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        // set the returned status and return the transaction id
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found with the id " + bookId));
        // checking if book is archived and shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The book cannot be returned!");
        }
        // check if user is not the same as the owner
        if(Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You cannot return your own book!");
        }

        // check if book is already borrowed by the user - only then return is possible
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
                .findByBookIdAndOwnerId(book.getId(), user.getUserId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. Return approval currently not possible!"));

        // set the approval status for the book and return the transaction history id
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
