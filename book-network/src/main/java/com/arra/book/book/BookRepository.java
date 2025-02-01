package com.arra.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    /*
     * JpaRepository<Book, Integer>: provides standard methods like findAll, save, delete for Book entities.
     * JpaSpecificationExecutor<Book>: Allows the use of Specification objects.
     */
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.createdBy != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);



    // TODO: implement method instead of using specification
/*
    @Query("""
            """)
    Page<Book> findAllDisplayableBooksByOwner(Pageable pageable, Integer userId);
*/

}
