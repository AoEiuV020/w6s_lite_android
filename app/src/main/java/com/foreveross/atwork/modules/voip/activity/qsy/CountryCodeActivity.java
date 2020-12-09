/****************************************************************************************
 * Copyright (c) 2010~2013 All Rights Reserved by
 *  G-Net Integrated Service Co.,  Ltd.   
 * @file CountryCodeActivity.java
 * @author guanghua.xiao  
 * @date 2013-11-12 下午2:45:38
 * @version V1.0  
 ****************************************************************************************/ 
package com.foreveross.atwork.modules.voip.activity.qsy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.CountryCode;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.modules.voip.adapter.qsy.CountryCodeAdapter;
import com.foreveross.atwork.modules.voip.support.qsy.Constants;
import com.foreveross.atwork.modules.voip.support.qsy.DBHelper;

import java.util.List;


/**
 * @class CountryCodeActivity
 * @brief 国别码选择画面
 */
public class CountryCodeActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = CountryCodeActivity.class.getSimpleName();
    private Context mCtx;
	private ImageView backBtn;
	private Button completeButton;
	private ListView listView;
    private CountryCodeAdapter adapter;
    private TextView titleTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tangsdk_country_code_layout);
		ActivityManagerHelper.addActivity(this);
		mCtx = this;
		// 初始化控件
		backBtn = (ImageView)findViewById(R.id.back_btn);
		backBtn.setVisibility(View.VISIBLE);
		titleTV = (TextView)findViewById(R.id.title_tv);
		titleTV.setVisibility(View.VISIBLE);
		listView = (ListView)findViewById(R.id.init_country_code_list_view);
		// 初始化数据
        initData();
        
        // 初始化监听器
        listView.setOnItemClickListener(this);
        backBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		if(adapter != null){
			adapter.clear();
			adapter = null;
		}
		listView = null;
		backBtn = null;
		ActivityManagerHelper.removeActivity(this);
		super.onDestroy();
	}

	/**
	 * @brief 处理页面上按钮的单击事件
	 * @param v
	 * @see OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back_btn){
			setActivityResult();
			finish();
		}
	}
	
	/**
	 * @brief 列表某项被选择后的回调方法
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		CountryCode cCode = (CountryCode)adapter.getItem(position);
		adapter.setSelectedCountryCode(cCode);
		adapter.notifyDataSetChanged();
	}
	
	private void setActivityResult(){
		setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_COUNTRY_CODE,
				adapter.getSelectedCountryCode()));
	}
	
	/**
	 * @brief 初始化数据集  
	 */ 
	private void initData(){
        List<CountryCode> codeList = DBHelper.getInstance(mCtx).queryCountryCodeList();
        if(codeList != null){
        	adapter = new CountryCodeAdapter(this, codeList);
        	adapter.setSelectedCountryCode(getSelectCountryCode());
        	listView.setAdapter(adapter);
        }
	}
	
	/**
	 * @brief 获取默认选中的国别码
	 */ 
	private CountryCode getSelectCountryCode(){
    	// 从intent中获取上级页面传递过来的默认选中国别码实体
    	Object extraCountryCode = getIntent().getSerializableExtra(Constants.EXTRA_COUNTRY_CODE);
    	CountryCode selectCountryCode = null;
    	if(extraCountryCode instanceof CountryCode){  // CountryCode类型
    		selectCountryCode = (CountryCode)extraCountryCode;
    	}else if(extraCountryCode instanceof String){ // 从intent中获取上级页面传递过来的默认选中国别码字符串
    		String selectCodeString = getIntent().getStringExtra(Constants.EXTRA_COUNTRY_CODE);
    		selectCountryCode = adapter.getCountryCode(selectCodeString);
    	}
    	if(selectCountryCode == null){ // 如果为空，则取系统默认的国别码
			Log.i(TAG, "countrycode from extra is null, check default countrycode ");
    		selectCountryCode = adapter.getCountryCode(Constants.DEFAULT_COUNTRYCODE);
    	}
    	return selectCountryCode;
	}

}
