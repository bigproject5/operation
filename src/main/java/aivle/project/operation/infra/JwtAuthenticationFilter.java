package aivle.project.operation.infra;

import aivle.project.operation.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //토큰 파싱
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId =request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        //토큰이 없거나 형식이 맞지 않으면 다음 필터로 넘김
        if(authorizationHeader == null || userId == null || role == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if(!jwtUtil.isExpired(token)) {
            UserDetails userDetails = getUserDetails(Long.valueOf(userId), role);

            UsernamePasswordAuthenticationToken authentication = new
                    UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private UserDetails getUserDetails(Long userId, String role) {
        if(role.equals("ADMIN")){
            return loginService.loadUserByAdminId(userId);
        }
        else if(role.equals("WORKER")){
            return loginService.loadUserByWorkerId(userId);
        }
        throw new IllegalArgumentException("Invalid role");
    }

}
