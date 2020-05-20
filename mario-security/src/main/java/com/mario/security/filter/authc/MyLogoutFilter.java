package com.mario.security.filter.authc;

import com.mario.security.filter.AdviceFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyLogoutFilter extends AdviceFilter {

  @Override
  protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        /*Subject subject = getSubject(request, response);
//        String redirectUrl = getRedirectUrl(request, response, subject);
        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.info("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }*/
//        log.info("logout success");
//        issueRedirect(request, response, redirectUrl);

//        HttpUtil.writeDate(response, ResponseRender.renderSucc());
    return false;
  }
}