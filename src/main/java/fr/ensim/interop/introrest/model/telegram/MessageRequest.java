package fr.ensim.interop.introrest.model.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private Long chat_id;
    private String text;
    private Integer reply_to_message_id;
    private String parse_mode;


}

