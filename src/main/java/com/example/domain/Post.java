package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Table(name = "\"post\"")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE \"post\" set deleted_at = now() where id=?")
public class Post extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "userAccount_id")
    private UserAccount user;

    public Post(Long id, String title, String body, UserAccount user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public static Post of(Long id, String title, String body, UserAccount userAccount){
        return new Post(id, title, body, userAccount);
    }

    public static Post of(String title, String body, UserAccount userAccount){
        return new Post(null, title, body, userAccount);
    }
}
