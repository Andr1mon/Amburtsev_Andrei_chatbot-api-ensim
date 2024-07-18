package fr.ensim.interop.introrest.service;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.joke.JokeRepository;
import fr.ensim.interop.introrest.model.telegram.Person;
import fr.ensim.interop.introrest.model.telegram.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DatabaseService {
    private static final Logger logger = Logger.getLogger("DatabaseService");


    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JokeRepository jokeRepository;

    public void reconstructDatabase() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/data.sql"))) {
            for (Person person : personRepository.findAll()) {
                String sql = String.format("INSERT INTO person (id, chat_id, first_name, last_name, username) VALUES (%d, %d, '%s', '%s', '%s');",
                        person.getId(), person.getChatId(), person.getFirstName(), person.getLastName(), person.getUsername());
                writer.write(sql);
                writer.newLine();
            }
            for (Joke joke : jokeRepository.findAll()) {
                String sql = String.format("INSERT INTO joke (id, title, text, score) VALUES (%d, '%s', '%s', '%d');",
                        joke.getId(), joke.getTitle(), joke.getText(), joke.getScore());
                writer.write(sql);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Reconstruct Database failed: ", e);
        }
    }

    public void addNewPerson(Long id, Long chatId, String first_name, String last_name, String username) {
        if (!personRepository.existsById(id)) {
            Person newPerson = new Person(id, chatId, first_name, last_name, username);
            personRepository.save(newPerson);
            reconstructDatabase();
        }
    }

    public void addNewJoke(Long id, String title, String text, Integer score) {
        if (!jokeRepository.existsById(id) && !jokeRepository.existsByTitle(title)) {
            text = text.replace("'", "’");
            title = title.replace("'", "’");
            Joke newJoke = new Joke(id, title, text, score);
            jokeRepository.save(newJoke);
            reconstructDatabase();
        }
    }

    public void changeJokeNoteByTitle(String title, Integer score) {
        Optional<Joke> joke = jokeRepository.findByTitle(title);
        joke.ifPresent(value -> value.setScore(score));
        reconstructDatabase();
    }

    public List<Joke> findGoodJokes() {
        List<Joke> jokes = new ArrayList<>();
        for (Joke joke : jokeRepository.findAll()) {
            if (joke.getScore() > 5) {
                jokes.add(joke);
            }
        }
        return jokes;
    }

    public List<Joke> findBadJokes() {
        List<Joke> jokes = new ArrayList<>();
        for (Joke joke : jokeRepository.findAll()) {
            if (joke.getScore() <= 5) {
                jokes.add(joke);
            }
        }
        return jokes;
    }
}
