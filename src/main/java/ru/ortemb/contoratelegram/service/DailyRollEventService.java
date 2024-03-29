package ru.ortemb.contoratelegram.service;


import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRollEventService {

  private final TelegramLongPollingBot bot;
  private final List<String> dice = List.of("🎲", "🎯", "🏀", "⚽", "🎰", "🎳");

  public void sendPhrases(List<Phrases> listPhrases, Random random, List<SystemUser> users) {
    try {
      sendMessage(users, String.format("4 - %s %s", listPhrases.get(0).getText(), getRandomEmoji(random.nextInt(3))));
      Thread.sleep(1000);
      sendMessage(users, String.format("3 - %s %s", listPhrases.get(1).getText(), getRandomEmoji(random.nextInt(3))));
      Thread.sleep(1000);
      sendMessage(users, String.format("2 - %s %s", listPhrases.get(2).getText(), getRandomEmoji(random.nextInt(3))));
      Thread.sleep(1000);
      sendMessage(users, String.format("1 - %s %s", listPhrases.get(3).getText(), getRandomEmoji(random.nextInt(3))));
      Thread.sleep(1000);
      sendMessage(users, "Испытываем фортуну " + getRandomEmoji(random.nextInt(5)));
      Thread.sleep(500);
      sendDice(users);
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(List<SystemUser> users, String text) {
    users.forEach(user -> {
      try {
        bot.execute(new SendMessage(user.getId(), text));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  public void sendDice(List<SystemUser> users) {
    users.forEach(user -> {
      try {
        bot.execute(SendDice.builder().chatId(user.getId()).emoji(dice.get(new Random().nextInt(dice.size()))).build());
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  public String getRandomEmoji(int amt) {
    int amount = amt > 0 ? amt : 1;
    ArrayList<Emoji> all = (ArrayList<Emoji>) EmojiManager.getAll();
    return Stream.generate(() -> (int) (Math.random() * all.size())).limit(amount).map(i -> all.get(i).getUnicode()).reduce((a, b) -> a + b).get();
  }
}
