import bot.Bot;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import repositories.ReminderDAO;
import repositories.UserDAO;
import service.MessageHandler;
import service.ReminderHandler;
//import org.telegram.telegrambots.ApiContextInitializer;

//import java.util.logging.Logger;


public class ApiApplication {
    private static final Logger log = Logger.getLogger(ApiApplication.class);

    private static ClassPathXmlApplicationContext springContext;

    public static void main(String[] args) {
        springContext = new ClassPathXmlApplicationContext("spring/spring-app.xml","spring/spring-db.xml");
        springContext.refresh();
        UserDAO userDAO = springContext.getBean(UserDAO.class);
        ReminderDAO reminderDAO = springContext.getBean(ReminderDAO.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            //Reminder
            Bot bot = new Bot("my_first_reminder_bot",
                    "1738000937:AAGNgtu42ic9tixN1JTqQTbRXxTXpPmHLd0");
            botsApi.registerBot(bot);

            MessageHandler messageHandler = new MessageHandler();
            messageHandler.setBot(bot, userDAO, reminderDAO);
            Thread receiver = new Thread(messageHandler);
            receiver.setDaemon(true);
            //receiver.setName("MsgReceiver");
            receiver.setPriority(1);
            receiver.start();

            ReminderHandler reminderHandler = new ReminderHandler();
            reminderHandler.setter(bot, userDAO, reminderDAO);
            Thread reminder = new Thread(reminderHandler);
            reminder.setDaemon(true);
            reminder.setPriority(3);
            reminder.start();

            System.out.println("Bot starting...");

        } catch (TelegramApiException e) {
            log.error("Can't start bor" + e);
            e.printStackTrace();
        }
    }
}
