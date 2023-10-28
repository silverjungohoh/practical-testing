package com.study.cafekiosk.api.service.mail;

import org.springframework.stereotype.Service;

import com.study.cafekiosk.client.mail.MailSendClient;
import com.study.cafekiosk.domain.history.mail.MailSendHistory;
import com.study.cafekiosk.domain.history.mail.MailSendHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final MailSendClient mailSendClient;
	private final MailSendHistoryRepository mailSendHistoryRepository;

	public boolean send(String fromEmail, String toEmail, String title, String content) {

		boolean result = mailSendClient.sendEmail(fromEmail, toEmail, title, content);

		if (result) {
			mailSendHistoryRepository.save(MailSendHistory.builder()
				.fromEmail(fromEmail)
				.toEmail(toEmail)
				.title(title)
				.content(content)
				.build()
			);
			return true;
		}
		return false;
	}
}
