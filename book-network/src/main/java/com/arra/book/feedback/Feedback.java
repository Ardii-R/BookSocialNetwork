package com.arra.book.feedback;

import com.arra.book.book.Book;
import com.arra.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Feedback")
public class Feedback extends BaseEntity {

   private Double note;
   private String comment;

   @ManyToOne
   @JoinColumn(name = "book_id")
   private Book book;


    // id and auditing attributes inside BaseEntity

}
