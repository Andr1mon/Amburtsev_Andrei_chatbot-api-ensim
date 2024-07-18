package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.joke.JokeRepository;
import fr.ensim.interop.introrest.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class JokeRestController {
	private static final Logger logger = Logger.getLogger("JokeRestController");

	@Autowired
	JokeRepository jokeRepository;
	@Autowired
	DatabaseService databaseService;

	@GetMapping("/joke")
	public ResponseEntity<Optional<Joke>> joke(@RequestParam (required = false) String rating) {
		if (rating == null) {
			long jokeCount = jokeRepository.count();
			if (jokeCount == 0) {
				logger.log(Level.INFO, "/joke OnFailure: No jokes found #1");
				return ResponseEntity.notFound().build();
			}

			Optional<Joke> joke = jokeRepository.findById(ThreadLocalRandom.current().nextLong(1, jokeCount + 1));
			if (joke.isPresent()) {
				logger.log(Level.INFO, "/joke OnResponse: joke id #" + joke.get().getId());

				return ResponseEntity.ok().body(joke);
			} else {
				logger.log(Level.INFO, "/joke OnFailure: No jokes found #2");
				return ResponseEntity.notFound().build();
			}
		} else {
			List<Joke> jokes;
			if (rating.equals("Good"))
				jokes = databaseService.findGoodJokes();
			else if (rating.equals("Bad"))
				jokes = databaseService.findBadJokes();
			else {
				logger.log(Level.INFO, "/joke OnFailure: incorrect rating - " + rating);
				return ResponseEntity.badRequest().build();
			}
			long jokeCount = jokes.size();
			if (jokeCount == 0) {
				logger.log(Level.INFO, "/joke OnFailure: No jokes found #1");
				return ResponseEntity.notFound().build();
			}

			Optional<Joke> joke = jokeRepository.findById(jokes.get((int) ThreadLocalRandom.current().nextLong(0, jokeCount)).getId());
			if (joke.isPresent()) {
				logger.log(Level.INFO, "/joke OnResponse: joke id #" + joke.get().getId());

				return ResponseEntity.ok().body(joke);
			} else {
				logger.log(Level.INFO, "/joke OnFailure: No jokes found #2");
				return ResponseEntity.notFound().build();
			}
		}
	}

}
