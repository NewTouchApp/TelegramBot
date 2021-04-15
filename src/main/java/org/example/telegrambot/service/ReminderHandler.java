package org.example.telegrambot.service;

import org.example.telegrambot.repositories.ReminderDAO;
import org.example.telegrambot.repositories.UserDAO;
import org.example.telegrambot.bot.Bot;
import org.example.telegrambot.entity.Reminder;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReminderHandler implements Runnable{
    public static final Logger log = Logger.getLogger(ReminderHandler.class);
    private static final int WAIT_FOR_NEW_MESSAGE_DELAY = 30000;

    private UserDAO userDAO;
    private ReminderDAO reminderDAO;
    private Bot bot;

    public void setter(Bot bot, UserDAO userDAO, ReminderDAO reminderDAO) {
        this.userDAO = userDAO;
        this.reminderDAO = reminderDAO;
        this.bot = bot;
    }

    public static Reminder parse(String message, String chatId) {
        if (message == null) throw new IllegalArgumentException("message is null");
        String[] data = message.split("_");

        if (data.length != 2) throw new IllegalArgumentException("bad format data");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime dateAction;
        System.out.println(data[0] + data[1]);
        try {
            dateAction = LocalDateTime.parse(data[0], formatter);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("bad format date");
        }

        Reminder reminder = new Reminder();
        reminder.setActionDate(dateAction);
        reminder.setDateCreate(LocalDateTime.now());
        reminder.setMessage(data[1]);
        reminder.setChatId(chatId);
        reminder.setActual(true);

        return reminder;
    }


    @Override
    public void run() {
        while (true) {
            List<Reminder> actualRemindFromTo = reminderDAO.findActualRemindFromTo(LocalDateTime.now(), LocalDateTime.now().plusMinutes(20));
            for (Reminder reminder: actualRemindFromTo) {
                bot.sendMessage(reminder.getMessage(), reminder.getChatId());
                reminder.setActual(false);
                reminderDAO.save(reminder);
            }

            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
            }
        }
    }
}
