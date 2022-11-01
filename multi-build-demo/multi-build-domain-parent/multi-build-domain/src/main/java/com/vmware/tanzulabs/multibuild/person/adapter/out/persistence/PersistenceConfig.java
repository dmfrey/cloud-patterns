package com.vmware.tanzulabs.multibuild.person.adapter.out.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories( basePackages = "com.vmware.tanzulabs.multibuild.person.adapter.out.persistence" )
class PersistenceConfig {
}
