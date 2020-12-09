package com.foreveross.atwork.infrastructure.model.email;

import java.io.Serializable;

public class K9Account implements Serializable {
	
	public Integer id;
	
	public int unread;
	
	public String latestemailid;

	//当前atwork帐号identifier
	public String identifier;
	
	//登录K9邮箱的uuid
	public String accountuuid;

}
