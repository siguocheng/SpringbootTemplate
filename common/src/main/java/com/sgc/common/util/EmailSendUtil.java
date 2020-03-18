package com.sgc.common.util;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSendUtil {

	public static final String SMTP = "smtp.sina.com";

	public static final String MAIL_ACCOUNT = "gkingo@sina.com";

	public static final String EMAIL_PASSWORD = "a12084099";

	/**
	 * @param notificationHistory
	 *            消息体
	 * @param account
	 *            收件邮箱
	 * @throws GeneralSecurityException
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static void send(String content, String account) throws MessagingException {

		Properties prop = new Properties();
		prop.setProperty("mail.host", SMTP);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");

		// 使用JavaMail发送邮件的5个步骤
		// 1、创建session
		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(MAIL_ACCOUNT, "60891cdb4c48eb4f");
			}
		});

		// 2、通过session得到transport对象
		Transport ts = session.getTransport();
		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		ts.connect(SMTP, MAIL_ACCOUNT, EMAIL_PASSWORD);
		// 创建默认的 MimeMessage 对象
		MimeMessage message = new MimeMessage(session);
		// Set From: 头部头字段
		message.setFrom(new InternetAddress(MAIL_ACCOUNT));
		// Set To: 头部头字段
		// 群发————————————————————————————————————————————————————————————————————————————————————
		String[] split = account.split(",");
		Address[] add = new Address[split.length];
		for (int i = 0; i < add.length; i++) {
			add[i] = new InternetAddress(split[i]);
		}
		message.addRecipients(Message.RecipientType.TO, add);
		// 标题
		String title = "AOS数据监控告警";
		// Set Subject: 头部头字段
		// 换为标题//////////////
		message.setSubject(title);
		// 设置消息体
		// 换位消息体//////////////
		message.setText(content);
		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
	}

	public static void main(String[] args) throws MessagingException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("AOS-EB-DataParse 锅炉job数据更新异常\n");
		sb.append("AOS-SaveData-IOTJob 净水job数据更新异常\n");
		EmailSendUtil.send(sb.toString(), "pc.zhu@wuyuan-tec.com");
	}
}