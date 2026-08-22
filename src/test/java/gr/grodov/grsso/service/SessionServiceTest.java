package gr.grodov.grsso.service;

import gr.grodov.grsso.authentication.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();

        RequestContextHolder.setRequestAttributes(
            new ServletRequestAttributes(request)
        );
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getSession_returnSession() {
        when(request.getSession()).thenReturn(session);

        var result = sessionService.getSession();

        assertThat(result).isSameAs(session);
        verify(request).getSession();
    }

    @Test
    void getSessionIfExists_withSession_returnSession() {
        when(request.getSession(false)).thenReturn(session);

        var result = sessionService.getSessionIfExists();

        assertThat(result).isSameAs(session);
        verify(request).getSession(false);
    }

    @Test
    void getSessionIfExists_withoutSession_returnNull() {
        when(request.getSession(false)).thenReturn(null);

        var result = sessionService.getSessionIfExists();

        assertThat(result).isNull();
        verify(request).getSession(false);
    }

    @Test
    void setAttribute_withAttributeAndValue_saveInSession() {
        when(request.getSession()).thenReturn(session);

        sessionService.setAttribute("attribute", "value");

        verify(session).setAttribute("attribute", "value");
    }

    @Test
    void getAttribute_withNoSession_returnNull() {
        when(request.getSession(false)).thenReturn(null);

        var result = sessionService.getAttribute("attribute", String.class);

        assertThat(result).isNull();
        verify(request).getSession(false);
    }

    @Test
    void getAttribute_withNoAttribute_returnNull() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("attribute")).thenReturn(null);

        var result = sessionService.getAttribute("attribute", String.class);

        assertThat(result).isNull();
        verify(request).getSession(false);
        verify(session).getAttribute("attribute");
    }

    @Test
    void getAttribute_withIncorrectType_returnNull() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("attribute")).thenReturn(List.of());

        var result = sessionService.getAttribute("attribute", String.class);

        assertThat(result).isNull();
        verify(request).getSession(false);
        verify(session).getAttribute("attribute");
    }

    @Test
    void getAttribute_withCorrectAttribute_returnValue() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("attribute")).thenReturn("value");

        var result = sessionService.getAttribute("attribute", String.class);

        assertThat(result).isEqualTo("value");
        verify(request).getSession(false);
        verify(session).getAttribute("attribute");
    }

    @Test
    void removeAttribute_withNoSession_noDelete() {
        when(request.getSession(false)).thenReturn(null);

        sessionService.removeAttribute("attribute");

        verify(request).getSession(false);
        verify(session, never()).removeAttribute(anyString());
    }

    @Test
    void removeAttribute_withSession_delete() {
        when(request.getSession(false)).thenReturn(session);

        sessionService.removeAttribute("attribute");

        verify(request).getSession(false);
        verify(session).removeAttribute("attribute");
    }

    @Test
    void hasAttribute_withNoSession_returnFalse() {
        when(request.getSession(false)).thenReturn(null);

        var result = sessionService.hasAttribute("attribute");

        assertThat(result).isFalse();
        verify(request).getSession(false);
    }

    @Test
    void hasAttribute_withSessionAndWithAttribute_returnTrue() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("attribute")).thenReturn("value");

        var result = sessionService.hasAttribute("attribute");

        assertThat(result).isTrue();
        verify(request).getSession(false);
        verify(session).getAttribute(anyString());
    }

    @Test
    void invalidate_withNoSession_noInvalidate() {
        when(request.getSession(false)).thenReturn(null);

        sessionService.invalidate();

        verify(request).getSession(false);
        verify(session, never()).invalidate();
    }

    @Test
    void invalidate_withSession_invalidate() {
        when(request.getSession(false)).thenReturn(session);

        sessionService.invalidate();

        verify(request).getSession(false);
        verify(session).invalidate();
    }

    @Test
    void getId_any_returnId() {
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn("id");

        var result = sessionService.getId();

        assertThat(result).isEqualTo("id");
        verify(request).getSession();
        verify(session).getId();
    }
}