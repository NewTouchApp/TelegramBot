import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//import org.telegram.telegrambots.ApiContextInitializer;

//import java.util.logging.Logger;


public class ApiApplication {
    private static final Logger log = Logger.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        log.info("test");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            //Reminder
            botsApi.registerBot(new Bot("my_first_reminder_bot",
                    "1738000937:AAGNgtu42ic9tixN1JTqQTbRXxTXpPmHLd0"));
        } catch (TelegramApiException e) {
            log.error("error!!!!");
            e.printStackTrace();
        }
        System.out.println("hello word");

    }
}
