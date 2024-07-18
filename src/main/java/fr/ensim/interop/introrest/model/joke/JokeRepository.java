package fr.ensim.interop.introrest.model.joke;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JokeRepository extends CrudRepository<Joke, Long> {
    Optional<Joke> findByTitle(String title);
    boolean existsByTitle(String title);
}