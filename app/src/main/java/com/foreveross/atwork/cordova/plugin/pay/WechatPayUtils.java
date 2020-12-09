package com.foreveross.atwork.cordova.plugin.pay;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
* WechatPayUtils.java Create on 2017年4月7日
* Create By wsj
* 用户微信支付签名
*/
public class WechatPayUtils {
	
	private final static String SIGN_KEY = "40124675301812987413401246753018";
	private final static String CHAR_SET = "UTF-8";
	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
	           "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	     
	//定义签名，微信根据参数字段的ASCII码值进行排序 加密签名,故使用SortMap进行参数排序
    public static String createSign(SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + SIGN_KEY);//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
        String sign = MD5Encode(sb.toString(),CHAR_SET).toUpperCase();
        return sign;
    }
	
    
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
           resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
     }
    
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
           n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
     }

     public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
           resultString = new String(origin);
           MessageDigest md = MessageDigest.getInstance("MD5");
           if (charsetname == null || "".equals(charsetname))
              resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
           else
              resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
     }



}
    
    

