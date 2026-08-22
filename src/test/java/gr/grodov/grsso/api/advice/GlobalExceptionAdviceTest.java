package gr.grodov.grsso.api.advice;

import gr.grodov.grsso.authentication.api.ApiAuthController;
import gr.grodov.grsso.common.exception.GlobalExceptionAdvice;
import gr.grodov.grsso.authentication.service.AuthenticationService;
import gr.grodov.grsso.user.service.UserInfoService;
import gr.grodov.grsso.user.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ApiAuthController.class)
@Import(GlobalExceptionAdvice.class)
class GlobalExceptionAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserInfoService userInfoService;
    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    @WithMockUser
    void handle_MethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content("""
            {
                "email": "",
                "password": ""
            }
            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("validation_error"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists())
            .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
    }

    @Test
    @WithMockUser
    void handle_BadCredentialsException() throws Exception {
        when(userInfoService.createNewUser(any(), any(), any())).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content("""
            {
                "email": "user@example.com",
                "password": "Password123!"
            }
            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("auth_bad_credentials"));
    }

    @Test
    @WithMockUser
    void handle_BaseErrorFieldException() throws Exception {
        when(userInfoService.createNewUser(any(), any(), any())).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content("""
            {
                "email": "user@example.com",
                "password": "Password123!"
            }
            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("email_invalid"))
            .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());
    }

    @Test
    @WithMockUser
    void handle_Exception() throws Exception {
        when(userInfoService.createNewUser(any(), any(), any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content("""
            {
                "email": "user@example.com",
                "password": "Password123!"
            }
            """))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("unknown"));
    }
}