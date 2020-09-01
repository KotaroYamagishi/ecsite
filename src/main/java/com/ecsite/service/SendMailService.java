package com.ecsite.service;

import java.nio.charset.StandardCharsets;


import javax.mail.internet.MimeMessage;

import com.ecsite.domain.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Service
public class SendMailService {
    @Autowired
    private JavaMailSender javaMailSender;

    
	/** 
	 * @param context
	 * @param order
	 */
	public void sendMail(Context context, Order order){

		// メールを送るためのメソッド
        javaMailSender.send(new MimeMessagePreparator() {

	        @Override
	        public void prepare(MimeMessage mimeMessage) throws Exception {
	            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
	                    StandardCharsets.UTF_8.name());
	            helper.setFrom("koutannn772@gmail.com");
	            helper.setTo(order.getDestinationEmail());
	            helper.setSubject("商品一覧");
	            helper.setText(getMailBody("user_who_bought_the_items", context), true);
	        }
	    });

    }

	
	/** 
	 * @param templateName
	 * @param context
	 * @return String
	 */
	// メールの内容
    private String getMailBody(String templateName, Context context) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(mailTemplateResolver());
		return templateEngine.process(templateName, context);
    }

	
	/** 
	 * @return ClassLoaderTemplateResolver
	 */
	// htmlに書いてあるメール内容をメールで送信できる書式に変更する
	private ClassLoaderTemplateResolver mailTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setPrefix("templates/mail/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(true);
		return templateResolver;
	}
}