package com.baidu.ai.aip;

import com.baidu.ai.aip.auth.AuthService;
import com.baidu.ai.aip.auth.utils.Base64Util;
import com.baidu.ai.aip.auth.utils.HttpUtil;

import java.net.URLEncoder;

import static com.baidu.ai.aip.auth.utils.FileUtil.readFileByBytes;

/**
 * 通用文字识别
 */
public class imageOCR {
    static final String AKEY="maRUKpTrv185Y8hN2cxHhp1A";
    static final String SKEY="cdXTkZDhVMqKyx4BDYuepGPd521KpMyL";




    static String accessToken = AuthService.getAuth(AKEY, SKEY);
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    //E:\WORKSPACE\picture\button.png

    public static String imageOCR(String filePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            // 本地文件路径


            byte[] imgData = readFileByBytes(filePath);
            if (imgData == null) {
                return null;
            }
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。


            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





}