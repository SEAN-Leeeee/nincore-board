package me.sean.nlnnong.Domain;

import jakarta.persistence.*;

@Entity
public class Content{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    private String body;
    private String creator;
    private String createDatetime;
    private String modifyDatetime;
    private Boolean isDeleted;
    private String deleteDatetime;
}
