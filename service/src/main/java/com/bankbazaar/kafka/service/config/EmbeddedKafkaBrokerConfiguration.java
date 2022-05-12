/*
 * Copyright (c) 2021 BankBazaar.com, Chennai, TN, India. All rights reserved. This software is the
 * confidential and proprietary information of BankBazaar.com ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with BankBazaar.
 */

package com.bankbazaar.kafka.service.config;
import java.util.*;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
//@Profile({"dev", "test"})
public class EmbeddedKafkaBrokerConfiguration {

  private static final String TMP_EMBEDDED_KAFKA_LOGS =
          String.format("/tmp/embedded-kafka-logs-%1$s/", UUID.randomUUID());
  private static final String PORT = "port";
  private static final String LOG_DIRS = "log.dirs";
  private static final String LISTENERS = "listeners";
  private static final Integer KAFKA_PORT = 9092;
  private static final String LISTENERS_VALUE = "PLAINTEXT://localhost:" + KAFKA_PORT;
  private static final Integer ZOOKEEPER_PORT = 2181;

  private EmbeddedKafkaBroker embeddedKafkaBroker;

  /**
   * bean for the embeddedKafkaBroker.
   *
   * @return local embeddedKafkaBroker
   */
  @Bean
  public EmbeddedKafkaBroker embeddedKafkaBroker() {
    String[] topics = {"File_Processor", "Create_CSV"};
    Map<String, String> brokerProperties = new HashMap<>();
    brokerProperties.put(LISTENERS, LISTENERS_VALUE);
    brokerProperties.put(PORT, KAFKA_PORT.toString());
    brokerProperties.put(LOG_DIRS, TMP_EMBEDDED_KAFKA_LOGS);
    EmbeddedKafkaBroker broker = new EmbeddedKafkaBroker(1, true, topics)
            .kafkaPorts(KAFKA_PORT).zkPort(ZOOKEEPER_PORT);
    broker.brokerProperties(brokerProperties);
    this.embeddedKafkaBroker = broker;
    return broker;
  }

  /**
   * close the embeddedKafkaBroker on destroy.
   */
  @PreDestroy
  public void preDestroy() {
    if (embeddedKafkaBroker != null) {
      embeddedKafkaBroker.destroy();
    }
  }
}
