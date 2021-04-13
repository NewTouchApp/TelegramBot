import bot.Bot;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.MessageReceiver;
//import org.telegram.telegrambots.ApiContextInitializer;

//import java.util.logging.Logger;


public class ApiApplication {
    private static final Logger log = Logger.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            //Reminder
            Bot reminder = new Bot("my_first_reminder_bot",
                    "1738000937:AAGNgtu42ic9tixN1JTqQTbRXxTXpPmHLd0");
            botsApi.registerBot(reminder);

            MessageReceiver messageReceiver = new MessageReceiver();
            messageReceiver.setBot(reminder);
            Thread receiver = new Thread(messageReceiver);
            receiver.setDaemon(true);
            //receiver.setName("MsgReceiver");
            //receiver.setPriority(3);
            receiver.start();

        } catch (TelegramApiException e) {
            log.error("Can't start bor" + e);
            e.printStackTrace();
        }

        System.out.println("Bot starting...");

    }
}
