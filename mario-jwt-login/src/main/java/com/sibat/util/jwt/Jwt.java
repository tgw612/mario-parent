package com.sibat.util.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.sibat.domain.UserRepository;
import com.sibat.domain.User;
import com.sibat.util.Response;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class Jwt {//extends OncePerRequestFilter {
    /**
     * 秘钥
     */
    private static final byte[] SECRET = "3d9230dskdlfj276917dfac04dsfg47df11sdflk26d".getBytes();

    /**
     * 初始化head部分的数据为
     * {
     * "alg":"HS256",
     * "type":"JWT"
     * }
     */
    private static final JWSHeader header = new JWSHeader(JWSAlgorithm.HS256, JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);

    /**
     * 生成token，该方法只在用户登录成功后调用
     *
     * @param ，map可以存储用户name，token生成时间，token过期时间等自定义字段
     * @return token字符串, 若失败则返回null
     */
    // private static Map<String, Object> tokenMap = new ConcurrentHashMap<>();
    public static String createToken(Map<String, Object> payload) {
        String tokenString = null;
        // 创建一个 JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(new JSONObject(payload)));
        try {
            // 将jwsObject 进行HMAC签名
            jwsObject.sign(new MACSigner(SECRET));
            tokenString = jwsObject.serialize();
        } catch (JOSEException e) {
            System.err.println("签名失败:" + e.getMessage());
            e.printStackTrace();
        }
        return tokenString;
    }

    /**
     * 校验token是否合法，返回Map集合,集合中主要包含    state状态码   data鉴权成功后从token中提取的数据
     * 该方法在过滤器中调用，每次请求API时都校验
     *
     * @return Map<String, Object>
     */
    public static Boolean validToken(String tokenString) {
        try {
            JWSObject jwsObject = JWSObject.parse(tokenString);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);
            if (jwsObject.verify(verifier)) {
                JSONObject jsonOBj = payload.toJSONObject();
                // token校验成功（此时没有校验是否过期）
                //resultMap.put("state", TokenState.VALID.toString());
                // 若payload包含ext字段，则校验是否过期
                if (jsonOBj.containsKey("end")) {
                    //结束时间
                    long extTime = Long.valueOf(jsonOBj.get("end").toString());
                    //开始时间
                    long curTime = new Date().getTime();
                    // 过期了
                    if (curTime > extTime) {
                        return false;
                    }
                }
                return true;
            } else {
                // 校验失败
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 单终端登录验证
     *
     * @param tokenString
     * @return
     */
    public static Response validTokenOne(String tokenString, UserRepository userRepository) {
        Response res = new Response();
        try {
            JWSObject jwsObject = JWSObject.parse(tokenString);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);
            if (jwsObject.verify(verifier)) {
                JSONObject jsonOBj = payload.toJSONObject();
                // token校验成功（此时没有校验是否过期）
                //resultMap.put("state", TokenState.VALID.toString());
//                校验token中begin与数据库登录时间是否一致 来判断是否有另外终端登录
                if (jsonOBj.containsKey("begin")) {
                    long beginTime = Long.valueOf(jsonOBj.get("begin").toString());
                    if (jsonOBj.containsKey("uName")) {
                        String userName = jsonOBj.get("uName").toString();
                        User user = userRepository.findByUserName(userName);
                        if (!String.valueOf(beginTime).equals(user.getLastLoginTime())) {
                            return res.failure("404");
                        }
                    }
                }
                // 若payload包含ext字段，则校验是否过期
                if (jsonOBj.containsKey("end")) {
                    //结束时间
                    long extTime = Long.valueOf(jsonOBj.get("end").toString());
                    //开始时间
                    long curTime = new Date().getTime();
                    // 过期了
                    if (curTime > extTime) {
                        res.setMessage("已过期");
                        return res.failure("404");
                    }
                }
                res.setData(jsonOBj.get("uName"));
                return res.success("200");
            } else {
                return res.failure("404");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return res.failure("404");
        }
    }

    public static Response validTokenMore(String tokenString) {
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
                return new Response("200", jsonOBj.get("uid"));
            } else {
                return new Response("403", "未通过验证");
            }
        } catch (Exception e) {
            return new Response("500", "error!");
        }
    }



    public static String getMD5(String str) throws NoSuchAlgorithmException {
        // 生成一个MD5加密计算摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 计算md5函数
        md.update(str.getBytes());
        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
        return new BigInteger(1, md.digest()).toString(16);
    }

//    @Autowired
//    UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        String token = httpServletRequest.getHeader("Authorization");
//        Response res = validToken2(token);
//        if (!res.getStatus().equals("200") && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(res.getData().toString());
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//    }

}


