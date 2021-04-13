package service;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import repositories.UserDAO;
import org.springframework.stereotype.Service;

import bot.Bot;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
public class MessageReceiver implements Runnable {
    public static final Logger log = Logger.getLogger(Bot.class);
    private static final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;

    //answer constant
    private final String SORRY = "Sorry, I can't understand you!";

    private ClassPathXmlApplicationContext springContext;
    private  UserDAO userDAO;
    private Bot bot;

    private static int counter = 0;

    public MessageReceiver() {
        System.out.println("Create new MessageReceiver, counter " + ++counter);

    }

    public void setBot(Bot bot) {
        this.bot = bot;
        springContext = new ClassPathXmlApplicationContext("spring/spring-app.xml","spring/spring-db.xml");
        springContext.refresh();
        userDAO = springContext.getBean(UserDAO.class);
    }


    @Override
    public void run() {
        while (bot != null) {
            for (Object obj = bot.receiveQueue.poll(); obj != null; obj = bot.receiveQueue.poll()) {
                log.debug("Get new object for analyze in queue " + obj.toString());
                analyze(obj);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
                System.out.println("i'm sleeping....");
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
            }
        }
    }

    private void analyze(Object obj) {
        if (obj instanceof Update) {
            analyzeForUpdateType((Update) obj);
        }
        else {
            log.warn(String.format("Can't analyze this object %s", obj));
        }
    }

    private void analyzeForUpdateType(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();

        if (msg.hasText() && msg.getText().startsWith("add new remind")) {
            bot.sendMessage("please, enter new remind in next format: DD.MM.YYYY HH:MM Text", chatId);
            bot.sendMessage("i can to new remind soon!!!", chatId);
            //addNewReminder();
        }
        else if (msg.hasText() && msg.getText().equals("/help")) {
            bot.sendMessageWithKeyBoard("My command list::", chatId);
        }
        else if (msg.hasText() && (
                            msg.getText().equals("/start") ||
                            msg.getText().equals("Привет!") ||
                            msg.getText().toLowerCase(Locale.ROOT).equals("hi") ||
                            msg.getText().toLowerCase(Locale.ROOT).equals("hello") ||
                            msg.getText().toLowerCase(Locale.ROOT).equals("reminder")
                                    )){
            sayHello(msg.getFrom(), chatId);
        }
        else {
            bot.sendMessage(SORRY, chatId);
            bot.sendMessageWithKeyBoard("I can accept only this list:", chatId);
        }

    }

    private void sayHello(User userTelegram, String chatId) {
        Optional<entity.User> userDB = userDAO.findById((long) userTelegram.getId());
        if (userDB.isPresent()) {
            bot.sendMessage("Hello " + userDB.get().getUserName(), chatId);
        }
        else {
            entity.User userNew = new entity.User();
            userNew.setId(userTelegram.getId());
            if (userTelegram.getUserName() == null) {
                userNew.setUserName("Anonymous");
                bot.sendMessage("Hello Anonymous", chatId);
            }
            else {
                String telegramUserName = userTelegram.getUserName();
                userNew.setUserName(telegramUserName);
                bot.sendMessage(String.format("Hello %s",telegramUserName) , chatId);
            }
            userDAO.save(userNew);
        }
    }
}
