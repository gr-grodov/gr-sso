package gr.grodov.grsso.security.service;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class SessionService {

    public HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest().getSession();
    }

    @Nullable
    public HttpSession getSessionIfExists() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest().getSession(false);
    }

    public void setAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    @Nullable
    public <T> T getAttribute(String name, Class<T> type) {
        HttpSession session = getSessionIfExists();

        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(name);

        if (value == null) {
            return null;
        }

        return type.cast(value);
    }

    public void removeAttribute(String name) {
        HttpSession session = getSessionIfExists();

        if (session != null) {
            session.removeAttribute(name);
        }
    }

    public boolean hasAttribute(String name) {
        HttpSession session = getSessionIfExists();

        return session != null && session.getAttribute(name) != null;
    }

    public void invalidate() {
        HttpSession session = getSessionIfExists();

        if (session != null) {
            session.invalidate();
        }
    }

    public String getId() {
        return getSession().getId();
    }

    public static class Attributes {
        public static final String OAUTH_FLOW = "oauth_flow";
    }
}