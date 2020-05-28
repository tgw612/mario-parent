package com.sibat.config;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACVerifier;
import com.sibat.util.Response;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final byte[] SECRET = "3d9230dskdlfj276917dfac04dsfg47df11sdflk26d".getBytes();

    public Response validToken2(String tokenString) {
        try {
            JWSObject jwsObject = JWSObject.parse(tokenString);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);
            if (jwsObject.verify(verifier)) {
                JSONObject jsonOBj = payload.toJSONObject();
                if (jsonOBj.containsKey("end")) {
                    //结束时间
                    long extTime = Long.valueOf(jsonOBj.get("end").toString());
                    //开始时间
                    long curTime = new Date().getTime();
                    // 过期了
                    if (curTime > extTime) {
                        return new Response("403", "已过期");
                    }
                }
                return new Response("200", jsonOBj.get("uName"));
            } else {
                return new Response("403", "未通过验证");
            }
        } catch (Exception e) {
            return new Response("500", "error!");
        }
    }
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    AnyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        // authToken.startsWith("Bearer ")
        // String authToken = header.substring(7);
        String username = getUsernameFromToken(token);

        logger.info("checking authentication for user " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            if (validToken2(token).getStatus().equals("200")) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private String getUsernameFromToken(String token){
        try{
            JWSObject jwsObject = JWSObject.parse(token);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);
            if (jwsObject.verify(verifier)) {
                JSONObject jsonOBj = payload.toJSONObject();
                return jsonOBj.getAsString("uName");
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
}