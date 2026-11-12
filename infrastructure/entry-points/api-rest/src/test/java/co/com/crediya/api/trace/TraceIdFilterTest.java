package co.com.crediya.api.trace;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraceIdFilterTest {

    private final TraceIdFilter filter = new TraceIdFilter();

    @Test
    void generaTraceIdSiElClienteNoLoEnvia() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("POST", "/api/v1/solicitud"), response, new MockFilterChain());

        assertNotNull(response.getHeader(TraceIdFilter.TRACE_HEADER));
        assertNull(MDC.get(TraceIdFilter.TRACE_ID));
    }

    @Test
    void reutilizaElTraceIdValidoDelCliente() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/solicitud");
        request.addHeader(TraceIdFilter.TRACE_HEADER, "prueba-traza-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("prueba-traza-123", response.getHeader(TraceIdFilter.TRACE_HEADER));
    }
}
