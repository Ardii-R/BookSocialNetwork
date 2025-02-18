package com.arra.book.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    // create a specification to filter the books by the owner
    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
        //return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), ownerId);
    }
}
