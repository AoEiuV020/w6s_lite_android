package com.foreveross.atwork.infrastructure.model.discussion;

/**
 * Created by dasunsy on 2017/4/1.
 */

public class DiscussionType {

    /**
     * 部门群(旧版本的概念, 需要兼容这个字段, 实际代码行为 SYSTEM 跟 {@link #INTERNAL_ORG}都为内部群)
     * */
    public static String SYSTEM = "ORG";


    /**
     * 内部群
     * */
    public static String INTERNAL_ORG = "INTERNAL_ORG";

}
