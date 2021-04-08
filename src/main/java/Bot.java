import org.apache.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Bot extends TelegramLongPollingCommandBot {
    public static final Logger log = Logger.getLogger(Bot.class);

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String BOT_NAME, String BOT_TOKEN) {
        this.BOT_NAME = BOT_NAME;
        this.BOT_TOKEN = BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUsername(msg);

        log.info(String.format("Bot send message: %s", "Hello " + userName));

        SendMessage answer = new SendMessage();
        answer.setText("Hello " + userName);
        answer.setChatId(update.getMessage().getChatId().toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            System.out.println("Get error, when execute answer");
            e.printStackTrace();
        }
    }

    private String getUsername(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.toString(), user.getFirstName());
        //return user.getUserName();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}
