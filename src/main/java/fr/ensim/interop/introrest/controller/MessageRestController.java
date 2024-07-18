package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.telegram.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class MessageRestController {
	private static final Logger logger = Logger.getLogger("MessageRestController");

	@Autowired
	PersonRepository personRepository;

	@Value("${telegram.api.url}")
	private String telegramApiUrl;
	@Value("${telegram.bot.id}")
	private String telegramBotID;

	// Opérations sur la ressource Message
	@PostMapping("/message")
	public ResponseEntity<ApiResponseTelegram> message(@RequestParam Long chat_id,
													   @RequestParam String text,
													   @RequestParam (required = false) Integer reply_to_message_id,
													   @RequestParam (required = false) String parse_mode) {
		logger.log(Level.INFO, "/message?chat_id=" + chat_id + "&text=" + text + "&reply_to_message_id=" + reply_to_message_id + "&parse_mode=" + parse_mode);
		if (!personRepository.findByChatId(chat_id).isPresent()) {
			logger.log(Level.INFO, "/message: No registered chat_id found: " + chat_id);
			return ResponseEntity.status(400).build();
		}
		if (parse_mode != null) {
			if (!parse_mode.equals("HTML")) {
				logger.log(Level.INFO, "/message: Unsupported parse_mode: " + parse_mode);
				return ResponseEntity.status(400).build();
			}
		}

		RestTemplate restTemplate = new RestTemplate();
		ApiResponseTelegram message;
		if (parse_mode == null) {
			message = restTemplate.getForObject(
					telegramApiUrl + telegramBotID +
							"/sendMessage?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}",
					ApiResponseTelegram.class, chat_id, text, reply_to_message_id);
		}
		else {
			message = restTemplate.getForObject(
					telegramApiUrl + telegramBotID +
							"/sendMessage?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode={parse_mode}",
					ApiResponseTelegram.class, chat_id, text, reply_to_message_id, parse_mode);
		}
		if (message != null) {
			logger.log(Level.INFO, "/message: OnResponse: " + message.getOk());

			return ResponseEntity.ok().body(message);
		}
		else { // Bad Gateway
			logger.log(Level.SEVERE, "/message: OnFailure");
			return ResponseEntity.status(502).build();
		}
	}


	@PostMapping("/messagev2")
		public ResponseEntity<ApiResponseTelegram> messagev2(@RequestBody MessageRequest messageRequest) {
		Long chat_id = messageRequest.getChat_id();
		String text = messageRequest.getText();
		Integer reply_to_message_id = messageRequest.getReply_to_message_id();
		String parse_mode = messageRequest.getParse_mode();

		logger.log(Level.INFO, "/messagev2?chat_id=" + chat_id + "&text=" + text + "&reply_to_message_id=" + reply_to_message_id + "&parse_mode=" + parse_mode);
		if (!personRepository.findByChatId(chat_id).isPresent()) {
			logger.log(Level.INFO, "/messagev2: No registered chat_id found: " + chat_id);
			return ResponseEntity.status(400).build();
		}
		if (parse_mode != null) {
			if (!parse_mode.equals("HTML")) {
				logger.log(Level.INFO, "/messagev2: Unsupported parse_mode: " + parse_mode);
				return ResponseEntity.status(400).build();
			}
		}

		RestTemplate restTemplate = new RestTemplate();
		ApiResponseTelegram message;
		if (parse_mode == null) {
			message = restTemplate.getForObject(
					telegramApiUrl + telegramBotID +
							"/sendMessage?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}",
					ApiResponseTelegram.class, chat_id, text, reply_to_message_id);
		}
		else {
			message = restTemplate.getForObject(
					telegramApiUrl + telegramBotID +
							"/sendMessage?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode={parse_mode}",
					ApiResponseTelegram.class, chat_id, text, reply_to_message_id, parse_mode);
		}
		if (message != null) {
			logger.log(Level.INFO, "/messagev2: OnResponse: " + message.getOk());

			return ResponseEntity.ok().body(message);
		}
		else { // Bad Gateway
			logger.log(Level.SEVERE, "/messagev2: OnFailure");
			return ResponseEntity.status(502).build();
		}
	}
}
