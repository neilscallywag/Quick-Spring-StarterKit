package com.starterkit.demo.clients.slack;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.scheduling.annotation.Async;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
public abstract class BaseSlackClient {

    public abstract String getUrl();

    public abstract String getChannel();

    @Async
    public void notify(final String message) {
        try {

            final WebhookResponse response =
                    Slack.getInstance().send(this.getUrl(), this.buildBodyMessage(message));

            if (response.getCode() != 200) {
                log.warn(
                        "[SLACK][ERROR] status[{}] body[{}]",
                        response.getCode(),
                        response.getBody());

            } else {
                log.info("[SLACK] error message send with success");
            }

        } catch (final Exception ex) {
            log.error("[SLACK][ERROR] An error has occurred when sending error message", ex);
        }
    }

    private String buildBodyMessage(final String message) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        final String timestamp =
                formatter.format(
                        OffsetDateTime.now().atZoneSameInstant(ZoneId.of("America/Sao_Paulo")));

        return format(
                "{\n" +
                "  \"channel\": \"#%s\",\n" +
                "  \"text\": \"*error*\",\n" +
                "  \"blocks\": [\n" +
                "    {\n" +
                "      \"type\": \"section\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"type\": \"mrkdwn\",\n" +
                "          \"text\": \"*Team:*\\n API\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"mrkdwn\",\n" +
                "          \"text\": \"*When:*\\n%s\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"mrkdwn\",\n" +
                "          \"text\": \"*Reason:*\\n%s.\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}",
                this.getChannel(), timestamp, StringEscapeUtils.escapeJson(message));
    }
}
