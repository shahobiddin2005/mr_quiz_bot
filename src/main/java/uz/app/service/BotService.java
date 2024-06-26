package uz.app.service;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotService extends TelegramLongPollingBot {
    private static final BotLogicService logicService = BotLogicService.getInstance();


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery().getData());
            logicService.callbackHandler(update);
        } else if (update.hasMessage()) {
            System.out.println(update.getMessage().getText());
            logicService.messageHandler(update);
        }
    }


    @Override
    public String getBotUsername() {
        return "mr_quiz_test_bot";
    }

    @Override
    public String getBotToken() {

        return "6981963867:AAE7VOflk89bA1r6r0EVpnHLeDcWie9eN3Q";
    }


    public void executeMessages(SendMessage... messages) {
        for (SendMessage message : messages) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static BotService botService;

    public static BotService getInstance() {
        if (botService == null) {
            botService = new BotService();
        }
        return botService;
    }

    @SneakyThrows
    public void executeMessages(ForwardMessage forwardMessage) {
        execute(forwardMessage);
    }

    @SneakyThrows
    public void executeMessages(SendPhoto sendPhoto) {
        execute(sendPhoto);
    }

    @SneakyThrows
    public void executeMessages(DeleteMessage deleteMessage) {
        execute(deleteMessage);
    }

    @SneakyThrows
    public Message executeMessages(SendMessage deleteMessage) {
        return execute(deleteMessage);
    }

    @SneakyThrows
    public void executeMessages(EditMessageText editMessageText) {
        execute(editMessageText);
    }
}
