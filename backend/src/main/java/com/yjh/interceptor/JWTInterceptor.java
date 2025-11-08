package com.yjh.interceptor;

import com.yjh.entity.model.Result;
import com.yjh.entity.model.StatusCode;
import com.yjh.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Result result = null;

        //获取请求头中的令牌
        String token = request.getHeader("Authorization");
        try {
            //验证令牌
            JWTUtils.verifyToken(token);
            //验证通过，放行请求
            return true;
        } catch (Exception e){
            e.printStackTrace();
            result = new Result(StatusCode.TOKENERROR,false,"未登录或登录已过期,请重新登录！");
        }
        //采用jackson方式将map转化为json 字符串
        String json = new ObjectMapper().writeValueAsString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
