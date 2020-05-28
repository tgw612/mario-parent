package com.sibat;

import com.avos.avoscloud.AVOSCloud;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeanCloudDemoApplication {

	public static void main(String[] args) {
		// 初始化参数依次为 AppId, AppKey, MasterKey
		AVOSCloud.initialize("aChovxBzVdgETxoVQLLs9qu2-gzGzoHsz","o8T1XzqpJfg6m04eLjTeNzRk","MW7uSJ4x6Am7uezhn1zsnVF1");
		AVOSCloud.setDebugLogEnabled(true);
		SpringApplication.run(LeanCloudDemoApplication.class, args);
	}
}
