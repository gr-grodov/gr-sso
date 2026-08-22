package gr.grodov.grsso.authentication.service;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@NamedInterface("service")
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

        T result;
        try {
            result = type.cast(value);
        } catch (ClassCastException _) {
            return null;
        }
        return result;
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
}