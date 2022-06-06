package ru.kvanttelecom.tv.amprocessor.core.db.configurations;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.dreamworkerln.spring.db.repositories.RepositoryWithEntityManager;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;

@Configuration
@EnableJpaRepositories(
    basePackages = PROJECT_PACKAGE_NAME,
    repositoryBaseClass = RepositoryWithEntityManager.class,
    repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
@EntityScan(basePackages = PROJECT_PACKAGE_NAME)

public class RepositoriesConfiguration {}
