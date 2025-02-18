package com.arra.book.feedback;

import com.arra.book.book.Book;
import com.arra.book.book.BookRepository;
import com.arra.book.book.BookResponse;
import com.arra.book.common.PageResponse;
import com.arra.book.exception.OperationNotPermittedException;
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


@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // get the book
        Book book = bookRepository.findById(request.bookId).orElseThrow(()
                -> new EntityNotFoundException("Book not found with the id " + request.bookId));

        // checking if book is archived and shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not shareable book");
        }
        // check if user is not the same as the owner
        if(Objects.equals(book.getOwner().getUserId(), user.getUserId())){
            throw new OperationNotPermittedException("You cannot give a feedback to your own book!");
        }
        // map request to Feedback entity and save it and return feedback id
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(int page, int size, Integer bookId, Authentication connectedUser) {
        // get currently logged in users
        User user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // build the pageable object for pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));             // sort by createdDate attribute (BaseEntity)
        Page<Feedback> feedbacks = feedbackRepository.findALlByBookId(pageable, bookId);
        // convert to FeedbackResponse
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback, user.getUserId())).toList();
        // return PageResponse which contains feedbacks and metadata about pagination
        return new PageResponse<>(feedbackResponses, feedbacks.getNumber(), feedbacks.getSize(),
                feedbacks.getTotalElements(), feedbacks.getTotalPages(), feedbacks.isFirst(), feedbacks.isLast());
    }
}
