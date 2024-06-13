package uz.app.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.app.Db;
import uz.app.entity.Answer;
import uz.app.entity.Test;
import uz.app.entity.User;
import uz.app.payload.InlineString;

import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static uz.app.util.Utils.*;

public class BotLogicService {
    private SendMessage sendMessage = new SendMessage();
    Db db = new Db();

    {
        db.adds();
    }

    BotService botService = BotService.getInstance();
    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();

    private Set<User> users = new HashSet<>();

    private final Long adminId = 6870548934L;

    private User currentUser;

    private boolean isTestAdd = false;
    private boolean isTestDelete = false;
    private boolean isAnsAdd = true;
    private Test test = new Test();
    private List<Answer> answers = new ArrayList<>();
    private int variantCount = 3;

    public void messageHandler(Update update) {
        Long id = update.getMessage().getChatId();
        currentUser = getUserById(id);
        if (currentUser == null) {
            currentUser = new User(id.toString(), "main", 0, null, null);
            users.add(currentUser);
        }
        sendMessage.setReplyMarkup(null);
        sendMessage.setChatId(id);

        String text = update.getMessage().getText();

        if (isTestAdd && id.equals(adminId)) {
            switch (currentUser.getState()) {
                case "question" -> {
                    test.setQuestion(text);
                    sendMessage.setText("Question added ✅\nAdd variants:");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add answer ➕", "Add variant ➕"}}));
                    currentUser.setState("variant");
                    botService.executeMessages(sendMessage);
                }
                case "variant" -> {
                    switch (text) {
                        case "Add answer ➕" -> {
                            if (isAnsAdd) {
                                currentUser.setState("trueAnswer");
                                sendMessage.setText("Enter true answer: ");
                                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                                replyKeyboardRemove.setRemoveKeyboard(true);
                                replyKeyboardRemove.setSelective(true);
                                sendMessage.setReplyMarkup(replyKeyboardRemove);
                                botService.executeMessages(sendMessage);
                                isAnsAdd = false;
                            } else {
                                sendMessage.setText("True answer added ❗\uFE0F \nAdd variants:");
                                sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add variant ➕"}}));
                                currentUser.setState("variant");
                                botService.executeMessages(sendMessage);
                            }
                        }
                        case "Add variant ➕" -> {
                            if (variantCount > 0) {
                                currentUser.setState("falseAnswer");
                                sendMessage.setText("Enter other variant: ");
                                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                                replyKeyboardRemove.setRemoveKeyboard(true);
                                replyKeyboardRemove.setSelective(true);
                                sendMessage.setReplyMarkup(replyKeyboardRemove);
                                botService.executeMessages(sendMessage);
                                variantCount--;
                            } else if (isAnsAdd) {
                                sendMessage.setText("All other varian added ❗\uFE0F \nAdd true answer:");
                                sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add answer ➕"}}));
                                currentUser.setState("variant");
                                botService.executeMessages(sendMessage);
                            } else {
                                sendMessage.setText("Incorrect command ❗\uFE0F");
                                sendMessage.setReplyMarkup(replyService.keyboardMaker(adminMenu));
                                currentUser.setState("main");
                                botService.executeMessages(sendMessage);
                            }
                        }
                    }
                }
                case "trueAnswer" -> {
                    Answer answer = new Answer(text, true);
                    if (variantCount == 0 && !isAnsAdd) {
                        sendMessage.setText("Test added successfully ✅");
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(adminMenu));
                        test.setAnswers(answers);
                        db.tests.add(test);
                    } else {
                        sendMessage.setText("True answer added ✅\nAdd variants:");
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add variant ➕"}}));
                    }
                    answers.add(answer);
                    currentUser.setState("variant");
                    botService.executeMessages(sendMessage);
                }
                case "falseAnswer" -> {
                    Answer answer = new Answer(text, true);
                    sendMessage.setText("Other variant added ✅\nAdd variants:");
                    if (isAnsAdd && variantCount > 0 )
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add answer ➕", "Add variant ➕"}}));
                    else if (isAnsAdd)
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add answer ➕"}}));
                    else
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"Add variant ➕"}}));
                    currentUser.setState("variant");
                    answers.add(answer);
                    if (variantCount == 0 && !isAnsAdd) {
                        sendMessage.setText("Test added successfully ✅");
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(adminMenu));
                        test.setAnswers(answers);
                        db.tests.add(test);
                    }
                    botService.executeMessages(sendMessage);
                }
            }
        }

        switch (text) {
            case "/start" -> {
                if (id.equals(adminId)) {
                    sendMessage.setText("welcome to bot, Mr.Admin");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(adminMenu));
                    botService.executeMessages(sendMessage);
                    return;
                }
                sendMessage.setText("welcome to bot");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                botService.executeMessages(sendMessage);
            }
            case ADD_TEST -> {
                if (!id.equals(adminId)) return;
                isTestAdd = true;
                currentUser.setState("question");
                test = new Test();
                variantCount = 3;
                sendMessage.setText("Enter your question:");
                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                replyKeyboardRemove.setRemoveKeyboard(true);
                replyKeyboardRemove.setSelective(true);
                sendMessage.setReplyMarkup(replyKeyboardRemove);
                botService.executeMessages(sendMessage);
            } case DELETE_TEST -> {
                if (!id.equals(adminId)) return;
                isTestDelete = true;
                currentUser.setState("question");
                test = new Test();
                variantCount = 3;
                sendMessage.setText("Enter your question:");
                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                replyKeyboardRemove.setRemoveKeyboard(true);
                replyKeyboardRemove.setSelective(true);
                sendMessage.setReplyMarkup(replyKeyboardRemove);
                botService.executeMessages(sendMessage);
            }
            case START_TEST -> {
                if (id.equals(adminId)) return;
                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                replyKeyboardRemove.setRemoveKeyboard(true);
                replyKeyboardRemove.setSelective(true);
                sendMessage.setReplyMarkup(replyKeyboardRemove);
                sendMessage.setText("Test is starting ⏳");

                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(currentUser.getId());
                deleteMessage.setMessageId(botService.executeMessages(sendMessage).getMessageId());
                botService.executeMessages(deleteMessage);
                sendMessage.setText("Are you ready?");
                sendMessage.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][]{{new InlineString("I'm ready ✅", "ready")}, {new InlineString("cancel ❌", "cancel")}}));
                botService.executeMessages(sendMessage);
            }
            case SHOW_RESULT -> {
                if (id.equals(adminId)) return;
                if (currentUser.getCurrentTest() == null) {
                    sendMessage.setText("You haven't taken the test yet ❗\uFE0F");
                    sendMessage.setChatId(currentUser.getId());
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                    botService.executeMessages(sendMessage);
                    return;
                }
                int userResult = 0;
                for (Test test : currentUser.getCurrentTest()) {
                    if (test.getSelectedAns() != null) switch (test.getSelectedAns()) {
                        case "a" -> {
                            if (test.getAnswers().get(0).getHasCorrect()) userResult++;
                        }
                        case "b" -> {
                            if (test.getAnswers().get(1).getHasCorrect()) userResult++;
                        }
                        case "c" -> {
                            if (test.getAnswers().get(2).getHasCorrect()) userResult++;
                        }
                        case "d" -> {
                            if (test.getAnswers().get(3).getHasCorrect()) userResult++;
                        }
                    }
                }
                sendMessage.setText("Your result:  " + userResult + "/" + currentUser.getCurrentTest().size() + "\nIn percent:  " + Math.floor((float) userResult / (float) currentUser.getCurrentTest().size() * 100) + "%");
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                botService.executeMessages(sendMessage);
            }
            default -> {

            }
        }

    }

    private User getUserById(Long id) {
        for (User user : users) {
            if (user.getId().equals(id.toString())) {
                return user;
            }
        }
        return null;
    }


    @SneakyThrows
    public void callbackHandler(Update update) {
        if (currentUser == null) {
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
            sendMessage.setText("Click /start for begin");
            sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"/start"}}));
            botService.executeMessages(sendMessage);
            return;
        }
        Long id = update.getCallbackQuery().getMessage().getChatId();
        if (id.equals(adminId)) {
            sendMessage.setReplyMarkup(null);
            sendMessage.setChatId(id);
        } else {
            sendMessage.setReplyMarkup(null);
            sendMessage.setChatId(id);

            if (update.hasCallbackQuery() && !currentUser.getId().equals(adminId.toString())) {
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();//botService.executeMessages(message).getMessageId();
                switch (update.getCallbackQuery().getData()) {
                    case "ready" -> {
                        ArrayList<Test> tests = new ArrayList<>();
                        LinkedHashSet<Integer> taskcount = new LinkedHashSet<>();
                        Random random = new Random();
                        while (taskcount.size() < 4) {
                            taskcount.add(random.nextInt(db.tests.size()));
                        }
                        for (Integer i : taskcount) {
                            try {
                                tests.add(db.tests.get(i).clone());
                            } catch (CloneNotSupportedException e) {
                            }
                        }
                        currentUser.setCurrentTest(tests);
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(currentUser.getId());
                        editMessageText.setMessageId(messageId);
                        editMessageText.setText("3\uFE0F⃣");
                        botService.executeMessages(editMessageText);
                        Thread.sleep(1000);
                        editMessageText.setText("2\uFE0F⃣");
                        botService.executeMessages(editMessageText);
                        Thread.sleep(1000);
                        editMessageText.setText("1\uFE0F⃣");
                        botService.executeMessages(editMessageText);
                        Thread.sleep(1000);
                        editMessageText.setText("Start \uD83C\uDFC1");
                        botService.executeMessages(editMessageText);
                        Thread.sleep(500);
                        currentUser.setEndTime(LocalTime.now().plusMinutes(1));
                        testMaker(messageId, currentUser, currentUser.getCurrentTestNumber());

                    }
                    case "cancel" -> {
                        sendMessage.setText("Test canceled ✅");
                        sendMessage.setChatId(currentUser.getId());
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                        botService.executeMessages(sendMessage);
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(currentUser.getId());
                        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

                        botService.executeMessages(deleteMessage);
                    }
                    case "a" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).setSelectedAns("a");
                        testEditer(messageId, "a", currentUser.getCurrentTestNumber());
                    }
                    case "b" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).setSelectedAns("b");
                        testEditer(messageId, "b", currentUser.getCurrentTestNumber());
                    }
                    case "c" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).setSelectedAns("c");
                        testEditer(messageId, "c", currentUser.getCurrentTestNumber());
                    }
                    case "d" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).setSelectedAns("d");
                        testEditer(messageId, "d", currentUser.getCurrentTestNumber());
                    }
                    case "next" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        if (currentUser.getCurrentTestNumber() + 1 > currentUser.getCurrentTest().size() - 1) return;
                        currentUser.setCurrentTestNumber(currentUser.getCurrentTestNumber() + 1);
                        testMaker(messageId, currentUser, currentUser.getCurrentTestNumber());
                        testSelected(messageId, currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).getSelectedAns());
                    }
                    case "back" -> {
                        if (currentUser.getEndTime() != null && !LocalTime.now().isBefore(currentUser.getEndTime())) {
                            sendMessage.setChatId(currentUser.getId());
                            sendMessage.setText("Test time finished ⏳");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        if (currentUser.getCurrentTestNumber() - 1 < 0) return;
                        currentUser.setCurrentTestNumber(currentUser.getCurrentTestNumber() - 1);
                        testMaker(messageId, currentUser, currentUser.getCurrentTestNumber());
                        testSelected(messageId, currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).getSelectedAns());
                    }
                    case "end" -> {
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(currentUser.getId());
                        editMessageText.setMessageId(messageId);
                        editMessageText.setText("Confirm test completion?");
                        editMessageText.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][]{{new InlineString("Yes ✅", "yes"), new InlineString("No ❌", "no")}}));
                        botService.executeMessages(editMessageText);
                    }
                    case "no" -> {
                        testEditer(messageId, currentUser.getCurrentTest().get(currentUser.getCurrentTestNumber()).getSelectedAns(), currentUser.getCurrentTestNumber());
                    }
                    case "yes" -> {
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(currentUser.getId());
                        deleteMessage.setMessageId(messageId);
                        botService.executeMessages(deleteMessage);
                        sendMessage.setText("Test complated ✅");
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                        botService.executeMessages(sendMessage);
                    }
                }
            }
        }
    }

    private Test testMaker(Integer messageId, User currentUser, int testNumber) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(currentUser.getId());
        editMessageText.setMessageId(messageId);
        Test test = currentUser.getCurrentTest().get(testNumber);
        if (!LocalTime.now().isBefore(currentUser.getEndTime())) {
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setText("Test vaqti tugadi ⏳");
            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(currentUser.getId());
            deleteMessage.setMessageId(messageId);
            botService.executeMessages(sendMessage);
            botService.executeMessages(deleteMessage);
            return test;
        }
        List<Answer> answers = test.getAnswers();
        editMessageText.setText(testNumber + 1 + ") " + test.getQuestion() + "\n" + "A) " + answers.get(0).getVariant() + "\n" + "B) " + answers.get(1).getVariant() + "\n" + "C) " + answers.get(2).getVariant() + "\n" + "D) " + answers.get(3).getVariant());
        editMessageText.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][]{{new InlineString("A", "a"), new InlineString("B", "b")}, {new InlineString("C", "c"), new InlineString("D", "d")}, {new InlineString("⬅\uFE0F", "back"), new InlineString("\uD83C\uDFF3\uFE0F", "end"), new InlineString("➡\uFE0F", "next")},}));
        botService.executeMessages(editMessageText);
        return test;
    }//712311032


    private Test testEditer(Integer messageId, String seclect, int testNumber) {

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(currentUser.getId());
        editMessageText.setMessageId(messageId);
        Test test = currentUser.getCurrentTest().get(testNumber);
        if (!LocalTime.now().isBefore(currentUser.getEndTime())) {
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setText("Test vaqti tugadi ⏳");
            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(currentUser.getId());
            deleteMessage.setMessageId(messageId);
            botService.executeMessages(sendMessage);
            botService.executeMessages(deleteMessage);
            return test;
        }
        List<Answer> answers = test.getAnswers();
        editMessageText.setText(testNumber + 1 + ") " + test.getQuestion() + "\n" + "A) " + answers.get(0).getVariant() + "\n" + "B) " + answers.get(1).getVariant() + "\n" + "C) " + answers.get(2).getVariant() + "\n" + "D) " + answers.get(3).getVariant());
        InlineString[][] inlineStrings = {{new InlineString("A", "a"), new InlineString("B", "b")}, {new InlineString("C", "c"), new InlineString("D", "d")}, {new InlineString("⬅\uFE0F", "back"), new InlineString("\uD83C\uDFF3\uFE0F", "end"), new InlineString("➡\uFE0F", "next")},};
        if (seclect != null) {
            switch (seclect) {
                case "a" -> inlineStrings[0][0].setMessage("✅ " + inlineStrings[0][0].getMessage());
                case "b" -> inlineStrings[0][1].setMessage("✅ " + inlineStrings[0][1].getMessage());
                case "c" -> inlineStrings[1][0].setMessage("✅ " + inlineStrings[1][0].getMessage());
                case "d" -> inlineStrings[1][1].setMessage("✅ " + inlineStrings[1][1].getMessage());
            }
        }
        editMessageText.setReplyMarkup(inlineService.inlineMarkup(inlineStrings));
        botService.executeMessages(editMessageText);
        return test;
    }

    private void testSelected(Integer messageId, String variant) {
        if (variant == null) return;
        switch (variant) {
            case "a" -> {
                testEditer(messageId, "a", currentUser.getCurrentTestNumber());
            }
            case "b" -> {
                testEditer(messageId, "b", currentUser.getCurrentTestNumber());
            }
            case "c" -> {
                testEditer(messageId, "c", currentUser.getCurrentTestNumber());
            }
            case "d" -> {
                testEditer(messageId, "d", currentUser.getCurrentTestNumber());
            }
        }
    }

    private static BotLogicService botLogicService;

    public static BotLogicService getInstance() {
        if (botLogicService == null) {
            botLogicService = new BotLogicService();
        }
        return botLogicService;
    }

}
