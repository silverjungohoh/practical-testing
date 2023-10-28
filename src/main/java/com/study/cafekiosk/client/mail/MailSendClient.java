package com.study.cafekiosk.client.mail;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailSendClient {

	public boolean sendEmail(String fromEmail, String toEmail, String title, String content) {
		log.info("send mail");
		return false;
	}
}
