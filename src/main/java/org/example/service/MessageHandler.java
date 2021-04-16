package org.example.telegrambot.service;

import org.example.telegrambot.repositories.ReminderDAO;
import org.example.telegrambot.repositories.UserDAO;
import org.example.telegrambot.bot.Bot;
import org.example.telegrambot.entity.Reminder;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class MessageHandler implements Runnable {
    public static final Logger log = Logger.getLogger(MessageHandler.class);
    private static final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;

    //answer constant
    private final String SORRY = "Sorry, I can't understand you!";

    private UserDAO userDAO;
    private ReminderDAO reminderDAO;
    private Bot bot;

    private static int counter = 0;

    public MessageHandler() {
        System.out.println("Create new MessageReceiver, counter " + ++counter);

    }

    public void setBot(Bot bot, UserDAO userDAO, ReminderDAO reminderDAO) {
        this.bot = bot;
        this.userDAO = userDAO;
        this.reminderDAO = reminderDAO;
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
                //System.out.println("i'm sleeping....");
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
            }
        }
    }

    private void analyze(Object obj) {
        if (obj instanceof Update) {
            Message msg = ((Update) obj).getMessage();
            if (msg == null) return;
            String chatId = msg.getChatId().toString();

            if (msg.hasText()) {
                analyzeForUpdateType(msg, chatId);
            }
            else {
                saySorry(chatId);
            }
        }
        else {
            log.warn(String.format("Can't analyze this object %s", obj));
        }
    }

    private void analyzeForUpdateType(Message msg, String chatId) {
        if (msg.getText().startsWith("add new remind")) {
            bot.sendMessage("please, enter new remind in next format: DD.MM.YYYY HH:MM Text", chatId);
        } else if (msg.getText().equals("/help")) {
            bot.sendMessageWithKeyBoard("My command list::", chatId);
        } else if (msg.getText().equals("/start") ||
                msg.getText().equals("Привет!") ||
                msg.getText().toLowerCase(Locale.ROOT).equals("hi") ||
                msg.getText().toLowerCase(Locale.ROOT).equals("hello") ||
                msg.getText().toLowerCase(Locale.ROOT).equals("reminder")) {
            sayHello(msg.getFrom(), chatId);
        } else {
            try {
                Reminder reminder = ReminderHandler.parse(msg.getText(), chatId);
                reminder.setUser(getCurrentOrCreateNewUser(msg.getFrom()));
                reminderDAO.save(reminder);
                bot.sendMessage("Ok, I remember, I will remind you of this at the specified time", chatId);
            }
            catch (Exception e) {
                bot.sendMessage(String.format("Sorry, I can't understand (%s)", e.getMessage()), chatId);
                bot.sendMessage("please, enter new remind in next format: DD.MM.YYYY HH:MM Text", chatId);
            }
        }
    }

    private void sayHello(User userTelegram, String chatId) {
        org.example.telegrambot.entity.User userDB = getCurrentOrCreateNewUser(userTelegram);
        bot.sendMessage(String.format("Hello %s",userDB.getUserName()) , chatId);
    }

    private void saySorry(String chatId) {
        bot.sendMessage(SORRY, chatId);
        bot.sendMessageWithKeyBoard("I can accept only this list:", chatId);
    }

    private org.example.telegrambot.entity.User getCurrentOrCreateNewUser(User userTelegram) {
        long userTelegramId = userTelegram.getId();
        Optional<org.example.telegrambot.entity.User> userDB = userDAO.findById(userTelegramId);
        if (userDB.isPresent()) {
            return userDB.get();
        }
        else {
            org.example.telegrambot.entity.User userNew = new org.example.telegrambot.entity.User();
            userNew.setId(userTelegramId);
            String userTelegramName = userTelegram.getUserName() == null ? "Anonymous" : userTelegram.getUserName();
            userNew.setUserName(userTelegramName);
            userDAO.save(userNew);
            return userNew;
        }
    }
}
