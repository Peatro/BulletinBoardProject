package com.peatroxd.bulletinboardproject.security.service;

import com.peatroxd.bulletinboardproject.security.annotation.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserArgumentResolverTest {

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CurrentUserArgumentResolver currentUserArgumentResolver;

    @Test
    void supportsParameterShouldAcceptCurrentUserAnnotation() throws NoSuchMethodException {
        Method method = TestController.class.getDeclaredMethod("endpoint", UUID.class, String.class);
        MethodParameter currentUserParameter = new MethodParameter(method, 0);
        MethodParameter regularParameter = new MethodParameter(method, 1);

        assertThat(currentUserArgumentResolver.supportsParameter(currentUserParameter)).isTrue();
        assertThat(currentUserArgumentResolver.supportsParameter(regularParameter)).isFalse();
    }

    @Test
    void resolveArgumentShouldDelegateToCurrentUserService() {
        UUID userId = UUID.randomUUID();
        when(currentUserService.getUserId()).thenReturn(userId);

        Object resolved = currentUserArgumentResolver.resolveArgument(null, null, null, null);

        assertThat(resolved).isEqualTo(userId);
        verify(currentUserService).getUserId();
    }

    private static class TestController {
        @SuppressWarnings("unused")
        private void endpoint(@CurrentUser UUID userId, String query) {
        }
    }
}
