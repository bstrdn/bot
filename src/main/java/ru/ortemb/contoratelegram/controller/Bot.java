package ru.ortemb.contoratelegram.controller;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.Users;
import ru.ortemb.contoratelegram.data.mapper.UsersMapper;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

  private final UserRepository userRepository;
  private final UsersMapper usersMapper;
  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @Override
  public void onUpdateReceived(Update update) {
    if (Objects.nonNull(update.getMessage()) && update.getMessage().hasText()) {
      if (update.getMessage().getText().equals("/start")) {
        //todo check exists
        Users newUser = userRepository.save(usersMapper.telegramUserToEntity(update.getMessage().getFrom()));
        try {
          execute(new SendMessage(newUser.getId(), String.format("Hi %s", newUser.getFirstName())));
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }
    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
      log.info("USER ID {} BLOCK YOU", update.getMyChatMember().getChat().getId());
    }
  }

  @Override
  public String getBotUsername() {
    return BOT_USERNAME;
  }

  @Override
  public String getBotToken() {
    return TOKEN;
  }

  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//    @Scheduled(cron = "*/10 * * * * *")
  private void send() {
    userRepository.findAll().forEach(users -> {
      try {
        execute(new SendMessage(users.getId(), "Have a good day!"));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), users.getUserName(), users.getId());
      }
    });
  }

//  @Transactional
//  @Scheduled(cron = "*/10 * * * * *")
//  void testSend() {
////        userRepository.findAll().stream().map(Users::getId).forEach(System.out::println);
//    SendMessage message = new SendMessage("85248441", "Hi!");
//    try {
//      execute(message);
//    } catch (TelegramApiException e) {
//      //todo get username
//      log.info("{}. USER ID: {}", e.getMessage(), message.getChatId());
//    }
//  }
}
