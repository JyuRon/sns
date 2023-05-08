package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(
        name = "\"comment\"",
        indexes = {
                @Index(name = "post_id_index", columnList = "post_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE \"comment\" set deleted_at = now() where id=?")
@Where(clause = "deleted_at is null")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userAccount_id")
    private UserAccount userAccount;

    public static Comment of(Long id, String comment, Post post, UserAccount userAccount){
        return new Comment(id, comment, post, userAccount);
    }

    public static Comment of(String comment, Post post, UserAccount userAccount){
        return Comment.of(null, comment, post, userAccount);
    }





}
