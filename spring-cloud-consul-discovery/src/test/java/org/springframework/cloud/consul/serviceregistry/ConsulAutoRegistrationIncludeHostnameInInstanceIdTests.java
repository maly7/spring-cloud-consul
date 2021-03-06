/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.consul.serviceregistry;

import com.ecwid.consul.v1.agent.model.NewService;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.consul.ConsulAutoConfiguration;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.test.ConsulTestcontainers;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author varnson
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsulAutoRegistrationIncludeHostnameInInstanceIdTests.TestConfig.class,
		properties = { "spring.application.name=myTestService-IncludeHostnameInInstanceId",
				"spring.cloud.consul.discovery.include-hostname-in-instance-id=true",
				"spring.cloud.client.hostname=testhostname" },
		webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = ConsulTestcontainers.class)
public class ConsulAutoRegistrationIncludeHostnameInInstanceIdTests {

	@Autowired
	private ConsulAutoRegistration registration;

	@Autowired
	private ConsulDiscoveryProperties properties;

	@Test
	public void contextLoads() {
		NewService service = this.registration.getService();
		assertThat(service).as("service was null").isNotNull();

		NewService.Check check = service.getCheck();
		assertThat(service.getId()).as("id is null").isNotNull();
		assertThat(service.getId()).as("id no include hostname").contains("testhostname");
		assertThat(service.getId()).as("service id was wrong").isEqualTo(this.registration.getInstanceId());

	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	@ImportAutoConfiguration({ AutoServiceRegistrationConfiguration.class, ConsulAutoConfiguration.class,
			ConsulAutoServiceRegistrationAutoConfiguration.class })
	public static class TestConfig {

	}

}
