package co.com.crediya.jpa.config;

import org.springframework.core.env.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class JpaConfigTest {


    @Mock
    DataSource dataSource;

    private DBSecret dbSecretUnderTest;
    private JpaConfig jpaConfigUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jpaConfigUnderTest = new JpaConfig();

        dbSecretUnderTest = DBSecret.builder()
                .password("sa")
                .username("sa")
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .build();

    }

    @Test
    void creaElSecretoDeBaseDeDatos() {
        Environment env = Mockito.mock(Environment.class);

        when(env.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        when(env.getProperty("spring.datasource.username")).thenReturn("sa");
        when(env.getProperty("spring.datasource.password")).thenReturn("sa");

        DBSecret secretResult = jpaConfigUnderTest.secretoBaseDatos(env);

        assertEquals(dbSecretUnderTest.getUrl(), secretResult.getUrl());
        assertEquals(dbSecretUnderTest.getUsername(), secretResult.getUsername());
        assertEquals(dbSecretUnderTest.getPassword(), secretResult.getPassword());
    }

    @Test
    void creaLaFuenteDeDatos() {
        final DataSource result = jpaConfigUnderTest.fuenteDatos(dbSecretUnderTest, "org.h2.Driver");

        assertNotNull(result);
    }

    @Test
    void creaLaFabricaDeEntityManager() {

        final LocalContainerEntityManagerFactoryBean result =
                jpaConfigUnderTest.entityManagerFactory(dataSource, "dialect");

        assertNotNull(result);
    }
}
