package fr.ensim.interop.introrest.model.joke;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Getter
@Setter
@Entity
public class Joke {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String text;
    private Integer score;

    public Joke(Long id, String title, String text, Integer score) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.score = score;
    }

    public Joke() {}
}
