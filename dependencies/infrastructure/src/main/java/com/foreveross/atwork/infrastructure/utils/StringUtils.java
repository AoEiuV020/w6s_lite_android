package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;

import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Created by lingen on 15/3/20.
 * Description:
 */
public class StringUtils {

    public static final String EMPTY = "";
    public static final String NULL = "NULL";
    public static final String ZERO = "0";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String FALSE = "FALSE";
    public static final String TRUE = "TRUE";

    /**
     * 查看一个字符是否为空(会trim 处理)
     *
     * @param s 需要检查的字条
     * @return 是否为空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static boolean isEmptyNoTrim(String s) {
        return s == null || s.equals("");
    }

    public static boolean isNull(String s) {
        return isEmpty(s) || NULL.equalsIgnoreCase(s);
    }

    public static String filterUnKnown(String sth) {
        if(UNKNOWN.equals(sth)) {
            return EMPTY;
        }

        return sth;

    }

    public static String filterTotalEmptySth(String sth) {
        if(ZERO.equals(sth) || NULL.equalsIgnoreCase(sth)) {
            return EMPTY;
        }

        return sth;
    }

    public static int getWordCount(String s) {

        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }


    public static String notNull(String s){
        if(s==null){
            return EMPTY;
        }
        return s;
    }
    /**
     * 生成中间省略的字符串
     * 这个方法是为了使得字符串过长的时候进行中间截取成 XXX..XXX.DOC 为什么不用系统的ellipse=middle，使用这个属性在某种命名的文件下会导致系统崩溃，这是Android系统的bug.
     * @param content 字符串长度
     * @param max 允许字符串最大值
     * @param allowChineseCount 允许中文最大值
     * @param start 省略开始于
     * @param end  省略结束于
     * @return
     */
    public static String middleEllipse(String content, int max, int allowChineseCount, int start, int end) {
        Integer index = 0;
        int chineseCount = 0;
        for (int i = 0; i < content.length(); i++) {
            String retContent = content.substring(i, i + 1);
            // 生成一个Pattern,同时编译一个正则表达式
            boolean isChina = retContent.matches("[\u4E00-\u9FA5]");
            if (isChina) {
                index = index + 2;
                chineseCount ++;
            } else {
                index = index + 1;
            }
        }

        if (index < max) {
            return content;
        }
        StringBuffer sb = new StringBuffer();
        if (chineseCount > allowChineseCount) {
            sb.append(content.substring(0, start + 2));
        } else {
            sb.append(content.substring(0, start + 5));
        }
        sb.append("...");
        sb.append(content.substring(content.length()- end, content.length()));
        return sb.toString();
    }

    public static boolean valueOf(String bo) {
        return TRUE.equalsIgnoreCase(bo);
    }


    /**
     * 通过 string 资源查找字符串
     * @param context
     * @param resStr
     * @return string
     * */
    public static String getStringFromResource(Context context, String resStr) {
        try {
            String packageName = context.getPackageName();
            int resId = context.getResources().getIdentifier(resStr, "string", packageName);
            return context.getString(resId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY;
    }


    public static String appendSeparatorStr(List<String> originalStrList, String separator) {
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < originalStrList.size(); i++) {
            strBuilder.append(originalStrList.get(i));

            if (i != originalStrList.size() - 1) {
                strBuilder.append(separator);
            }
        }

        return strBuilder.toString();
    }



    /**
     * 将驼峰式命名的字符串转换为下划线小写方式
     * 例如：HelloWorld->hello_world
     */
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (!isEmpty(name)) {
            // 将第一个字符处理成小写
            result.append(name.substring(0, 1).toLowerCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成小写
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 将下划线命名的字符串转换为驼峰式
     * 例如：HELLO_WORLD->helloWorld
     */
    public static String camelName(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (isEmpty(name)) {
            // 没必要转换
            return EMPTY;

        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母小写
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String camels[] = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String getStringDistinctive(final String text, List<String> textList) {
        String textHandling = text;
        int index = 1;
        while (true) {
            if(textList.contains(textHandling)) {
                textHandling = text + "(" + index + ")";
                index++;

            } else {
                break;
            }
        }
        return textHandling;
    }
}
