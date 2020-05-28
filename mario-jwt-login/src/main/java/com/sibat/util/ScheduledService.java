package com.sibat.util;

import com.sibat.domain.VerifyCode;
import com.sibat.domain.VerifyCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by tgw on 2017/2/22.
 */
@Service
public class ScheduledService {
    @Autowired
    VerifyCodeRepository verifyCodeRepository;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void deleteVerifyCode() {
        long current = System.currentTimeMillis();
        List<VerifyCode> vcList = verifyCodeRepository.findAll();
        if (vcList != null && !vcList.isEmpty()) {
            for (VerifyCode vc : vcList) {
                if (current - vc.getCreateTime() > 1000 * 60 * 5) {
                    verifyCodeRepository.delete(vc);
                }
            }
        }
    }

}
