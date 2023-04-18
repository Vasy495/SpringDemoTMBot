package com.mytinkov.springdemotmbot.service;

import com.mytinkov.springdemotmbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component // Позволит автоматически создать экземпляр spring'у
@Slf4j
public class TelegramBot extends TelegramLongPollingBot /*WebHookBot - всегда знает что ему написали*/ {


    final BotConfig config;

    static final String HELP_TEXT = "Привет, меня зовут Ботман, и я уже умею:\n\n" +
            "Type /start - приветствие пользователя и начало общения с ботом\n\n" +
            "Type /mydata - посмотреть мою историю\n\n" +
            "Type /deletedata - очистить мою историю\n\n" +
            "Type /help - как пользоваться ботом";


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "приветствие пользователя и начало общения с ботом"));
        listOfCommands.add(new BotCommand("/mydata", "показать мою историю"));
        listOfCommands.add(new BotCommand("/deletedata", "очистить мою историю"));
        listOfCommands.add(new BotCommand("/help", "как пользоваться ботом"));
        listOfCommands.add(new BotCommand("/setting", "настройки бота"));
        try {
                this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId(); // идентифицирует кто пишет по id

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Sorry, command wasn't recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replies to user " + name);

        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId)); //Получаем Long, но когда отвечаем нужен String
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}


