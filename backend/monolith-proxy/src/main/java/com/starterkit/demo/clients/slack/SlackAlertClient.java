/* (C)2024 */
package com.starterkit.demo.clients.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@Profile("!test")
public class SlackAlertClient extends BaseSlackClient {

	private final String url;
	private final String channel;

	public SlackAlertClient(
			@Value("${client.slack.api-alert.webhook}") final String url,
			@Value("${client.slack.api-alert.name}") final String channel) {
		this.url = url;
		this.channel = channel;
	}
}
