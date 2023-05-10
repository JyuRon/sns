package com.example.config.filter;

import com.example.dto.UserDto;
import com.example.service.UserService;
import com.example.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    private final static List<String> TOKEN_IN_PARAM_URLS= List.of("/api/v1/users/alarm/subscribe");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final  String token;

        try{
            if(TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())){
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            }else{
                final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

                if(header == null || !header.startsWith("Bearer ")){
                    log.error("Error occurs while getting header. header is null or invalid {}", request.getRequestURL());
                    filterChain.doFilter(request, response);
                    return;
                }
                token = header.split(" ")[1].trim();
            }


            // Token is valid
            if(jwtTokenUtils.isExpired(token)){
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // get userName for Token
            String userName = jwtTokenUtils.getUserName(token);

            // userName Valid
            UserDto userDto = userService.loadUserByUserName(userName);


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDto, null, userDto.getAuthorities()
            );

            // ip, session 정보
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (RuntimeException e){
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
