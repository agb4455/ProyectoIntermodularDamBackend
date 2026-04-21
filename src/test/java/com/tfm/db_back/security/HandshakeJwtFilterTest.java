package com.tfm.db_back.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tfm.db_back.domain.service.HandshakeService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandshakeJwtFilterTest {

    @Mock
    private HandshakeService handshakeService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private HandshakeJwtFilter filter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new HandshakeJwtFilter(handshakeService, objectMapper);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_givenHandshakePathAndPost_shouldReturnTrue() {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/internal/auth/handshake");

        // Act
        boolean result = filter.shouldNotFilter(request);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotFilter_givenOtherPath_shouldReturnFalse() {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        // No stubbing for URI as it is short-circuited

        // Act
        boolean result = filter.shouldNotFilter(request);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void doFilterInternal_givenValidToken_shouldSetAuthAndContinue() throws Exception {
        // Arrange
        String token = "valid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(handshakeService.validateToken(token)).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("middle-server");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void doFilterInternal_givenMissingToken_shouldReturn401() throws Exception {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        
        // Mock output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(outputStream.toString()).contains("MISSING_TOKEN");
    }

    @Test
    void doFilterInternal_givenInvalidToken_shouldReturn401() throws Exception {
        // Arrange
        String token = "invalid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(handshakeService.validateToken(token)).thenReturn(false);

        // Mock output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(outputStream.toString()).contains("INVALID_TOKEN");
    }

    // Clase auxiliar para capturar la salida del ServletOutputStream
    private static class DelegatingServletOutputStream extends jakarta.servlet.ServletOutputStream {
        private final java.io.OutputStream target;
        public DelegatingServletOutputStream(java.io.OutputStream target) { this.target = target; }
        @Override public void write(int b) throws java.io.IOException { target.write(b); }
        @Override public boolean isReady() { return true; }
        @Override public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
    }
}
