package com.sibat.domain;

import com.sibat.domain.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tgw61 on 2017/4/21.
 */
public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {
    VerifyCode findByCode(String s);
}
