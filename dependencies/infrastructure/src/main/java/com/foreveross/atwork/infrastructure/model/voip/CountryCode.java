/****************************************************************************************
 * Copyright (c) 2010~2013 All Rights Reserved by
 *  G-Net Integrated Service Co.,  Ltd.   
 * @file CountryCode.java
 * @author guanghua.xiao  
 * @date 2013-9-2 下午4:12:23
 * @version V1.0  
 ****************************************************************************************/
package com.foreveross.atwork.infrastructure.model.voip;

import java.io.Serializable;

/**
 * @class CountryCode
 * @brief 国别码
 */
public class CountryCode implements Serializable {
	private static final long serialVersionUID = 6174546517729742347L;
	/// 索引
	public int index;
	/// 英文国家名称
	public String countryENName;
	/// 中文国家名称
	public String countryCHName;
	/// 国别码
	public String countryCode;
	
}
