package com.sibat.util.captcha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jimin on 15/11/22.
 */
public class  FileUtil {

    //读取
    public static String readFile(URL url) throws Exception {
        Logger log = LoggerFactory.getLogger(FileUtil.class);
        log.info("try to read file, {}", url.toString());
        BufferedReader br = null;
        try {
            File file = new File(url.toURI());
            br = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("read file succeed, {}", url.toString());
            return sb.toString();
        } catch (Exception e) {
            log.info("read file error, {}", url.toString(), e);
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
