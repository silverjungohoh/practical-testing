package com.study.cafekiosk.domain.history.mail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.study.cafekiosk.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailSendHistory extends BaseEntity {

	@Id
	@Column(name = "history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fromEmail;

	private String toEmail;

	private String title;

	private String content;

	@Builder
	private MailSendHistory(String fromEmail, String toEmail, String title, String content) {
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.title = title;
		this.content = content;
	}
}
