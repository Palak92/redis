package com.example.redis.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class RedisApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisApplication.class);

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter1(), new PatternTopic("chat"));
		container.addMessageListener(listenerAdapter2(), new PatternTopic("chat"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter1() {
		return new MessageListenerAdapter(receiver1(), "receiveMessage");
	}

	@Bean
	MessageListenerAdapter listenerAdapter2() {
		return new MessageListenerAdapter(receiver2(), "receiveMessage");
	}

	@Bean
	Receiver receiver1() {
		return new Receiver();
	}

	@Bean
	Receiver receiver2() {
		return new Receiver();
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = SpringApplication.run(RedisApplication.class, args);

		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		// Receiver receiver = ctx.getBean(Receiver.class);

		LOGGER.info("Sending message...");
		template.convertAndSend("chat", "Hello from Redis!");
		Thread.sleep(500L);

		System.exit(0);
	}

}
