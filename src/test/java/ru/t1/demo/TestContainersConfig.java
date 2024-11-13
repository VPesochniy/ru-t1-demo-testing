package ru.t1.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers // явное указание, что будут использоваться контейнеры
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

//    можнно объявить вот так или через @Bean
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("testDatabase")
                .withUsername("testUser")
                .withPassword("testPassword");
    }

//  можно прописать так или же использовать @ServiceConnection
//    @DynamicPropertySource
//    static void datasourceProperties(DynamicPropertyRegistry registry) {
//
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//    }

}
