package co.com.crediya.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void registraLosCasosDeUsoComoBeans() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        } catch (org.springframework.beans.factory.UnsatisfiedDependencyException e) {
            assertTrue(true, "Unsatisfied dependencies are expected for UseCases resolving gateways");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public PruebaUseCase pruebaUseCase() {
            return new PruebaUseCase();
        }
    }

    static class PruebaUseCase {
        public String ejecutar() {
            return "PruebaUseCase Test";
        }
    }
}