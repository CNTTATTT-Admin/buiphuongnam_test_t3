package vn.kurisu.mentormatch.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.repository.UserRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:5173/auth/google/callback}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            redirectWithError(response, "Google authentication payload is invalid");
            return;
        }

        String email = extractStringAttribute(oauth2User, "email");
        if (email == null || email.isBlank()) {
            redirectWithError(response, "Google account does not provide email");
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createOAuthUser(oauth2User, email));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            redirectWithError(response, "Your account has been blocked");
            return;
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUserName());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String jwt = tokenProvider.generateToken(authToken);

        String targetUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                .queryParam("token", jwt)
                .queryParam("userName", user.getUserName())
                .build(true)
                .toUriString();

        response.sendRedirect(targetUrl);
    }

    private User createOAuthUser(OAuth2User oauth2User, String email) {
        Role defaultRole = roleRepository.findByName("ROLE_MENTEE")
                .orElseThrow(() -> new IllegalStateException("ROLE_MENTEE not found"));

        String userName = generateUniqueUserName(email);
        String fullName = extractStringAttribute(oauth2User, "name");
        String avatarUrl = extractStringAttribute(oauth2User, "picture");

        User user = User.builder()
                .userName(userName)
                .email(email)
                .fullName((fullName == null || fullName.isBlank()) ? userName : fullName)
                .avatarUrl(avatarUrl)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .isActive(true)
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    private String generateUniqueUserName(String email) {
        String prefix = email.substring(0, email.indexOf('@'))
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");

        if (prefix.isBlank()) {
            prefix = "user";
        }

        String candidate = prefix;
        int suffix = 1;
        while (userRepository.existsByUserName(candidate)) {
            candidate = prefix + suffix;
            suffix++;
        }

        return candidate;
    }

    private String extractStringAttribute(OAuth2User oauth2User, String key) {
        Object value = oauth2User.getAttributes().get(key);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue.trim();
        }
        return null;
    }

    private void redirectWithError(HttpServletResponse response, String errorMessage) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                .queryParam("error", errorMessage)
                .build(true)
                .toUriString();
        response.sendRedirect(targetUrl);
    }
}
