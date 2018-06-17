import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.Set;

public class TeleBot extends TelegramLongPollingBot {


    Set<Long> chatIds = new HashSet<Long>();

    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            chatIds.add(update.getMessage().getChatId());
            //System.out.println(update.getMessage().getChatId());
        }
    }

    public void sendAll(String msg){
        for(long l : chatIds){
            sendMsg(l , msg);
        }

    }


    public void sendMsg(long user , String msg){
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(user)
                .setText(msg);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        // Return bot username
        // If bot username is @TeleBot, it must return 'TeleBot'
        return "DroneIcarusBot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "458351657:AAGiP9BNRXtWT--lFtxenbaOx-l1KM9ZDIo";
    }
}
