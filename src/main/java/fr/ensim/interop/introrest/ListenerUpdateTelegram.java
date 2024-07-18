package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram implements CommandLineRunner {

	@Autowired
	private TelegramService telegramService;
	
	@Override
	public void run(String... args) throws Exception {
		Logger.getLogger("ListenerUpdateTelegram").log(Level.INFO, "Démarage du listener d'updates Telegram...");

		// Operation de pooling pour capter les evenements Telegram
		while (true) {
			telegramService.checkUpdatesAndHandleRequests();
			Thread.sleep(5000);
		}
	}
}
