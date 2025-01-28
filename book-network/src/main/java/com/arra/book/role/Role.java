package com.arra.book.role;

import com.arra.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Role")
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore     // Prevents circular references during JSON serialization by ignoring users
    private List<User> users;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate crateDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate lastModifiedDate;



}
