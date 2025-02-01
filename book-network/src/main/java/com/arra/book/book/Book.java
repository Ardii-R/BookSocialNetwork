package com.arra.book.book;

import com.arra.book.common.BaseEntity;
import com.arra.book.feedback.Feedback;
import com.arra.book.history.BookTransactionHistory;
import com.arra.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name="Book")
public class Book extends BaseEntity {

    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    // id and auditing attributes inside BaseEntity


    @Transient
    public double getRate() {
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        // calc the avg rate
        double rate = this.feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
        // round to 1 decimal place
        return Math.round(rate * 10.0) / 10.0;
    }

}
