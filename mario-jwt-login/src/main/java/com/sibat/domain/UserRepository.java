package com.sibat.domain;

import com.sibat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tgw61 on 2017/4/21.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserNameAndPassword(String account, String password);

    User findByUserName(String account);

    User findByUserId(long userId);
}
