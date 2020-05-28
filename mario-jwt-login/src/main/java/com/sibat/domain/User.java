package com.sibat.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tgw61 on 2016/10/31.
 * id应该为hash
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User implements Serializable {
    private static final long serialVersionUID = 72477146660613254L;
    @Id
    @GeneratedValue
    private long userId;//用户id
    @NotNull
    private String userName;//用户名
    @NotNull
    private String password;//密码

    private String lastLoginTime;//最后登录时间

    private String role;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
