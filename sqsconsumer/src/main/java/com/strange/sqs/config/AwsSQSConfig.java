package com.strange.sqs.config;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementResultCallback;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class AwsSQSConfig
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AwsSQSConfig.class);

	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Bean
	SqsAsyncClient sqsAsyncClient() {
		final var awsCredentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
		return SqsAsyncClient.builder().region(Region.of(this.region))
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
				.build();
	}

	@Bean
	SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(final SqsAsyncClient sqsAsyncClient) {
		return SqsMessageListenerContainerFactory.builder()
			.configure(options ->
				options.acknowledgementMode(AcknowledgementMode.ALWAYS)
					.queueNotFoundStrategy(QueueNotFoundStrategy.FAIL)
					.acknowledgementInterval(Duration.ofSeconds(3)) // NOTE: With acknowledgementInterval 3 seconds,
					.acknowledgementThreshold(0))
			//.acknowledgementResultCallback(new AckResultCallback())
			.sqsAsyncClient(sqsAsyncClient).build();
	}

	/**
	 * Optional callback if we want to inspect the success/failure.
	 */
	static class AckResultCallback implements AcknowledgementResultCallback<Object>  {

		@Override
		public void onSuccess(final Collection<Message<Object>> messages) {
			LOGGER.info("Ack with success at {}", OffsetDateTime.now());
		}

		@Override
		public void onFailure(final Collection<Message<Object>> messages, final Throwable t) {
			LOGGER.error("Ack with fail", t);
		}
	}
}
