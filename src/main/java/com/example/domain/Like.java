package com.example.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "\"like\"")
@Getter
@Setter
@NoArgsConstructor
public class Like extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "userAccount_id")
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Like(Long id, UserAccount user, Post post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }

    public static Like of(Long id, UserAccount user, Post post){
        return new Like(id, user, post);
    }

    public static Like of(UserAccount user, Post post){
        return Like.of(null, user, post);
    }
}
