package fr.ensim.interop.introrest.model.telegram;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Person {
    @Id
    private Long id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;

    public Person(Long id, Long chat_id, String first_name, String last_name, String username) {
        this.id = id;
        this.chatId = chat_id;
        this.firstName = first_name;
        this.lastName = last_name;
        this.username = username;
    }

    public Person() {}
}
