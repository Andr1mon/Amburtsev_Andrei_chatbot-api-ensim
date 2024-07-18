package fr.ensim.interop.introrest.model.telegram;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByChatId(Long chatId);
}