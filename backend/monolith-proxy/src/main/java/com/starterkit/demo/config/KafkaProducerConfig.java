package com.starterkit.demo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.starterkit.demo.model.ClickstreamEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Value("${kafka.sasl.mechanism}")
	private String saslMechanism;

	@Value("${kafka.security.protocol}")
	private String securityProtocol;

	@Value("${kafka.sasl.jaas.config}")
	private String saslJaasConfig;

	// // Optional: If using Avro serialization and schema registry
	// @Value("${kafka.schema.registry.url:}")
	// private String schemaRegistryUrl;

	// @Value("${kafka.basic.auth.credentials.source:}")
	// private String basicAuthCredentialsSource;

	// @Value("${kafka.basic.auth.user.info:}")
	// private String basicAuthUserInfo;

	@Bean
	public ProducerFactory<String, ClickstreamEvent> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put("sasl.mechanism", saslMechanism);
		configProps.put("security.protocol", securityProtocol);
		configProps.put("sasl.jaas.config", saslJaasConfig);

		// Use JSON Serializer for value serialization
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

		// // Optional: If using Avro serialization
		// if (!schemaRegistryUrl.isEmpty()) {
		//     configProps.put("schema.registry.url", schemaRegistryUrl);
		//     configProps.put("basic.auth.credentials.source", basicAuthCredentialsSource);
		//     configProps.put("basic.auth.user.info", basicAuthUserInfo);
		// }

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, ClickstreamEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
