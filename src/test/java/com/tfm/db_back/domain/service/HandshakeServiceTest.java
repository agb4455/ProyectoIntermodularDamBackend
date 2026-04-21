package com.tfm.db_back.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para HandshakeService.
 * Sin contexto Spring — instanciación manual para máxima velocidad.
 */
class HandshakeServiceTest {

    private static final String VALID_SECRET = "test-secret-minimo-32-chars-ok!!";
    private static final String OTHER_SECRET = "otro-secret-minimo-32-chars-ok!!";

    private HandshakeService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new HandshakeService(VALID_SECRET, 24L);
    }

    @Test
    void generateToken_givenValidConfig_shouldReturnNonNullJwt() {
        // when
        String token = serviceUnderTest.generateToken();

        // then
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_givenGeneratedToken_shouldBeValidatableWithSameSecret() {
        // given
        String token = serviceUnderTest.generateToken();

        // when
        boolean valid = serviceUnderTest.validateToken(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    void validateToken_givenTokenSignedWithDifferentSecret_shouldReturnFalse() {
        // given — token firmado con un secret diferente
        HandshakeService otherService = new HandshakeService(OTHER_SECRET, 24L);
        String tokenFromOtherSecret = otherService.generateToken();

        // when — se valida con el secret correcto del servicio bajo prueba
        boolean valid = serviceUnderTest.validateToken(tokenFromOtherSecret);

        // then
        assertThat(valid).isFalse();
    }

    @Test
    void validateToken_givenMalformedToken_shouldReturnFalse() {
        // when
        boolean valid = serviceUnderTest.validateToken("esto.no.es.un.jwt");

        // then
        assertThat(valid).isFalse();
    }

    @Test
    void validateToken_givenEmptyString_shouldReturnFalse() {
        // when
        boolean valid = serviceUnderTest.validateToken("");

        // then
        assertThat(valid).isFalse();
    }
}
