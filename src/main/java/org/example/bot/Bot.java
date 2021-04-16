package org.example.telegrambot.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Bot extends TelegramLongPollingCommandBot {
    public static final Logger log = Logger.getLogger(Bot.class);

    public final Queue<Object> receiveQueue = new ConcurrentLinkedDeque<>();

    private final String BOT_NAME;
    private final String BOT_TOKEN;


    public Bot(String BOT_NAME, String BOT_TOKEN) {
        this.BOT_NAME = BOT_NAME;
        this.BOT_TOKEN = BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        receiveQueue.add(update);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public synchronized void setButton(SendMessage sendMessage) {
        //create keyboard
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        //create list rows keyboard
        List<KeyboardRow> keyboard = new ArrayList<>();

        //first row
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Привет!"));

        //second row
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("/help"));

        //etc...
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("add new remind"));

        //add row keyboard in list
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void sendMessage(String text, String chatId) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId);
        try {
            this.execute(answer);
        } catch (TelegramApiException e) {
            System.out.println("Get error, when execute answer");
            e.printStackTrace();
        }
    }

    public synchronized void sendMessageWithKeyBoard(String textMessage, String chatId) {
        SendMessage answer = new SendMessage();
        answer.setText(textMessage);
        this.setButton(answer);
        answer.setChatId(chatId);
        try {
            this.execute(answer);
        } catch (TelegramApiException e) {
            log.error(String.format("Can't send answer with message: %s from chatId: %s", textMessage, chatId));
        }
    }

}
