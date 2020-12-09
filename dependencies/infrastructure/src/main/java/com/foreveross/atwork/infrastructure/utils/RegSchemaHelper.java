package com.foreveross.atwork.infrastructure.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegSchemaHelper {

    /**
     * 电话号码匹配规则, 用于修改提交时做校验区别于{@link #phonePatternRegCommon}, 它支持3位短号
     * */
    private static String phonePatternRegEdit = "^\\d?(\\d-?){2,18}\\d$";

    private static String phonePatternRegCommon = "((?<=\\D)|^)(0\\d{9,13}|\\d{2,5}-\\d{7,10}|\\d{3}-\\d{4}-\\d{4}|1+[3578]+\\d{9}|\\d{7,10})((?=\\D)|$)";
    private static String mobilePatternReg = "^(0|86|17951)?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[678])[0-9]{8}$";
    //    private static String webUrlPatternReg = "(https://?|ftp://|file://|www.|http://)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//    private static String webUrlPatternReg = "(((file|gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)://)|(www\\.))?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})(:[0-9]{1,5})?|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})(:[0-9]{1,5})?)(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    private static String webUrlPatternReg = "\\s*(((file|gopher|news|nntp|telnet|(h|H)ttp|ftp|(h|H)ttps|ftps|sftp)://)|(www\\.))?(([a-zA-Z0-9\\.,_-]+\\.[a-zA-Z]{2,6})(:[0-9]{1,5})?|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})(:[0-9]{1,5})?)(/?[a-zA-Z0-9\\#&%_\\.,/-~-+()!]*)?\\s*";
    private static String workplusSchemaUrlPatternReg = "\\s*(((workplus|Workplus|rfchina|Rfchina)://))(([a-zA-Z0-9\\.,_-]+)(:[0-9]{1,5})?)(/?[a-zA-Z0-9\\#&%_\\.,/-~-+()!]*)?\\s*";
    private static String emailPatternReg = "[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
    private static String emojiPatternReg = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";



    public static boolean isUrlLink(String text) {
        return Pattern.matches(webUrlPatternReg, text.toLowerCase());
    }

    public static boolean isPhotoForCommon(String text) {
        return Pattern.matches(phonePatternRegCommon, text);
    }

    public static boolean isPhoneForEdit(String text) {
        return Pattern.matches(phonePatternRegEdit, text);
    }

    public static boolean isMobile(String text) {
        return Pattern.matches(mobilePatternReg, text);

    }

    public static boolean isEmail(String text) {
        return Pattern.matches(emailPatternReg, text);
    }

    public static String filterEmoji(String txt) {
        Pattern emoji = Pattern.compile(emojiPatternReg,
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(txt);
        return emojiMatcher.replaceAll("[表情]");
    }

    public static boolean hasEmoji(String txt) {
        Pattern emoji = Pattern.compile(emojiPatternReg,
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(txt);
        return emojiMatcher.find();
    }


}
