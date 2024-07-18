package fr.ensim.interop.introrest.service;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.openWeather.CutWeather;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class TelegramService {
    private static final Logger logger = Logger.getLogger("TelegramService");
    private static Integer telegramOffset = 0;
    @Value("${telegram.api.url}")
    private String telegramApiUrl;
    @Value("${telegram.bot.id}")
    private String telegramBotID;
    @Value("${server.url}")
    private String serverUrl;
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    DatabaseService databaseService;

    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void checkUpdatesAndHandleRequests() {
        RestTemplate restTemplate = new RestTemplate();
        ApiResponseUpdateTelegram apiResponseUpdateTelegram = restTemplate.getForObject(telegramApiUrl + telegramBotID
                        + "/getUpdates?offset=" + telegramOffset, ApiResponseUpdateTelegram.class);
        logger.log(Level.INFO, "/checkUpdates Request: " + telegramApiUrl + telegramBotID + "/getUpdates?offset=" + telegramOffset);
        if (apiResponseUpdateTelegram != null) {
            if (apiResponseUpdateTelegram.getOk()) {
                List<Update> updates = apiResponseUpdateTelegram.getResult();
                Long chatId, userId;
                Integer messageId;
                String messageText;
                String text;
                String meteoTextTemplate = "Merci d'envoyer la requête selon un des templates suivants :\n<pre>meteo [ville]</pre>\nou\n<pre>meteo [ville] [nombre de jours]</pre>\n" +
                        "<b>Le nombre de jours maximum est 5.</b>\n\nPar exemple:\n<blockquote>meteo Le Mans</blockquote>" +
                        "ou <blockquote>meteo Le Mans 5</blockquote>";
                String jokeTextTemplate = "Merci d'envoyer la requête selon le templates suivant :\n\n<pre>blague_ajout Titre: [Titre de blague]\nText: [Text de blague]\nNote: [Note de blague]</pre>\n" +
                        "<b>Le note est compris entre 0 et 10 inclus.</b>\n\nPar exemple:\n<blockquote>Titre: Steak\nText: Qu'est ce qui n'est pas un steak ? Une pastèque.\nNote: 5</blockquote>";
                for (Update update : updates) {
                    telegramOffset = update.getUpdateId() + 1;
                    chatId = update.getMessage().getChatId();
                    messageText = update.getMessage().getText();
                    messageId = update.getMessage().getMessageId();
                    userId = update.getMessage().getFrom().getId();
                    logger.log(Level.INFO, "checkUpdates: new message: '" + messageText + "' (message_id #"
                            + messageId + ") in ChatID #" + chatId + " from user #" + userId);
                    // chatId handling
                    databaseService.addNewPerson(userId, chatId, update.getMessage().getFrom().getFirstName(),
                            update.getMessage().getFrom().getLastName(), update.getMessage().getFrom().getUserName());
                    // message handling
                    if (messageText.equalsIgnoreCase("/blague") || messageText.equalsIgnoreCase("blague")) {
                        Joke joke = restTemplate.getForObject(serverUrl + ":" + serverPort + "/joke", Joke.class);
                        text = "<b>" + joke.getTitle() + "</b>\n<blockquote>" + joke.getText() + "</blockquote>\nNote de la blague: <b>" + joke.getScore() + "/10</b>";
                        restTemplate.postForObject(
                                serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                null, Message.class, chatId, text, messageId);
                    }
                    else if (messageText.equalsIgnoreCase("/blague_nulle") || messageText.equalsIgnoreCase("blague nulle")) {
                        Joke joke = restTemplate.getForObject(serverUrl + ":" + serverPort + "/joke?rating=Bad", Joke.class);
                        text = "<b>" + joke.getTitle() + "</b>\n<blockquote>" + joke.getText() + "</blockquote>\nNote de la blague: <b>" + joke.getScore() + "/10</b>";
                        restTemplate.postForObject(
                                serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                null, Message.class, chatId, text, messageId);
                    }
                    else if (messageText.equalsIgnoreCase("/blague_bonne") || messageText.equalsIgnoreCase("blague bonne")) {
                        Joke joke = restTemplate.getForObject(serverUrl + ":" + serverPort + "/joke?rating=Good", Joke.class);
                        text = "<b>" + joke.getTitle() + "</b>\n<blockquote>" + joke.getText() + "</blockquote>\nNote de la blague: <b>" + joke.getScore() + "/10</b>";
                        restTemplate.postForObject(
                                serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                null, Message.class, chatId, text, messageId);
                    }
                    else if (messageText.equals("blague_ajout") || messageText.equals("/blague_ajout")) {
                        restTemplate.postForObject(
                                serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                null, Message.class, chatId, jokeTextTemplate, messageId);
                    }
                    else if (messageText.equalsIgnoreCase("/meteo") || messageText.equalsIgnoreCase("meteo")) {
                        restTemplate.postForObject(
                                serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                null, Message.class, chatId, meteoTextTemplate, messageId);
                    } else if (messageText.contains("meteo") || messageText.contains("/meteo")) {
                        String [] splitedMessage = messageText.split(" ");
                        if (splitedMessage[0].equals("meteo") || splitedMessage[0].equals("/meteo")) {
                            String city = "";
                            if (isInteger(splitedMessage[splitedMessage.length - 1])) {
                                if (Integer.parseInt(splitedMessage[splitedMessage.length - 1]) <= 5 &&
                                        Integer.parseInt(splitedMessage[splitedMessage.length - 1]) >= 1) {
                                    for (int i = 1; i < splitedMessage.length - 1; i++)
                                        city += splitedMessage[i] + " ";
                                    city = city.substring(0, city.length() - 1);

                                    // deserialization (cast) fix because
                                    // List<CutWeather> cutWeathers = restTemplate.getForObject(serverUrl + ":" + serverPort + "/weather?city={city}", List.class, city);
                                    // gives errors
                                    ParameterizedTypeReference<List<CutWeather>> responseType = new ParameterizedTypeReference<List<CutWeather>>() {
                                    };
                                    ResponseEntity<List<CutWeather>> responseEntity = restTemplate.exchange(
                                            serverUrl + ":" + serverPort + "/weather?city={city}&days={days}",
                                            HttpMethod.GET,
                                            null,
                                            responseType,
                                            city,
                                            splitedMessage[splitedMessage.length - 1]
                                    );
                                    List<CutWeather> cutWeathers = responseEntity.getBody();

                                    if (cutWeathers != null && !cutWeathers.isEmpty()) {
                                        text = "Meteo pour la ville <b>" + city + "</b>";
                                        for (CutWeather cutWeather : cutWeathers) {
                                            text += "\n\n<blockquote>Description : " + cutWeather.getMain() + " (" + cutWeather.getDescription() + ")\nTemperature : "
                                                    + cutWeather.getTemp() + "°C\nDate : " + cutWeather.getDt_txt() + "</blockquote>";
                                        }
                                    } else {
                                        text = "La ville n'a pas été trouvée.";
                                    }
                                }
                                else
                                    text = "Le nombre de jours est compris entre 1 et 5 inclus.";
                                restTemplate.postForObject(
                                        serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                        null, Message.class, chatId, text, messageId);


                            } else {
                                for (int i = 1; i < splitedMessage.length; i++)
                                    city += splitedMessage[i] + " ";
                                city = city.substring(0, city.length() - 1);

                                try {
                                    ParameterizedTypeReference<List<CutWeather>> responseType = new ParameterizedTypeReference<List<CutWeather>>() {
                                    };
                                    ResponseEntity<List<CutWeather>> responseEntity = restTemplate.exchange(
                                            serverUrl + ":" + serverPort + "/weather?city={city}",
                                            HttpMethod.GET,
                                            null,
                                            responseType,
                                            city,
                                            splitedMessage[splitedMessage.length - 1]
                                    );
                                    List<CutWeather> cutWeathers = responseEntity.getBody();

                                    if (cutWeathers != null && !cutWeathers.isEmpty()) {
                                        text = "Meteo pour la ville <b>" + city + "</b>";
                                        for (CutWeather cutWeather : cutWeathers) {
                                            text += "\n\n<blockquote>Description : " + cutWeather.getMain() + " (" + cutWeather.getDescription() + ")\nTemperature : "
                                                    + cutWeather.getTemp() + "°C\nDate : " + cutWeather.getDt_txt() + "</blockquote>";
                                        }
                                    }
                                    else {
                                        text = "La ville n'a pas été trouvée.";
                                    }
                                }
                                catch (Exception e) {
                                    text = "La ville n'a pas été trouvée.";
                                }


                                restTemplate.postForObject(
                                        serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                        null, Message.class, chatId, text, messageId);
                            }
                        }
                        else {
                            restTemplate.postForObject(
                                    serverUrl + ":" + serverPort + "/message?chat_id={chat_id}&text={text}&reply_to_message_id={reply_to_message_id}&parse_mode=HTML",
                                    null, Message.class, chatId, meteoTextTemplate, messageId);
                        }
                    }
                }
            }
        }
    }
}
