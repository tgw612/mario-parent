package com.sibat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tgw61 on 2016/11/16.
 */
@Entity
@NoArgsConstructor
@Table(name = "verifycode")
@Data
public class VerifyCode {
    @Id
    @GeneratedValue
    private int id;
    private String code;
    private Long createTime;

    public VerifyCode(String code, Long createTime) {
        this.code = code;
        this.createTime = createTime;
    }
}
