package com.strange.sqs.service;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class StringMessageReceiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageReceiver.class);

	@SqsListener("${sqs.queue.name}")
	public void listen(final Message<String> message) {
		LOGGER.info(message.getPayload()+ " = received on listen method at {}", OffsetDateTime.now());
	}	
}
