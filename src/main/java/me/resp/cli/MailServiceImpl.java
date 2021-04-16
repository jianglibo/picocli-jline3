package me.resp.cli;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("MailService")
@Slf4j
public class MailServiceImpl {

  public void sendMessage(List<String> to, String subject, String text) {
    log.info("Mail to {} sent! Subject: {}, Body: {}", to, subject, text);
  }
}
