/****************************************************************************************
 * Copyright (c) 2010~2013 All Rights Reserved by
 *  G-Net Integrated Service Co.,  Ltd.   
 * @file CountryCodeAdapter.java
 * @author guanghua.xiao  
 * @date 2013-9-2 下午5:07:24
 * @version V1.0  
 ****************************************************************************************/
package com.foreveross.atwork.modules.voip.adapter.qsy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.CountryCode;
import com.foreveross.atwork.modules.voip.support.qsy.Constants;

import java.util.List;
import java.util.Locale;

/**
 * @class CountryCodeAdapter
 * @brief 国别码选择的适配器
 */
public class CountryCodeAdapter extends BaseAdapter {
	static final String TAG = CountryCodeAdapter.class.getSimpleName();
	private LayoutInflater inflater;
	/// 国别码数据
	private List<CountryCode> list;
	/// 是否隐藏单选控件(默认不隐藏）
	private boolean checkBtnHidden = false;
	/// 语言类型（1表示中文，2表示其它即英文）
	private int languageType;
	/// 选择的国家
	private CountryCode selectedCountryCode;

	/**
	 * @brief 构造方法
	 */
	public CountryCodeAdapter(Context context, List<CountryCode> countryCodeList) {
		super();
		inflater = LayoutInflater.from(context);
		this.list = countryCodeList;
		this.checkBtnHidden = false;
		this.languageType = getOsLangugeType();
	}

	/**
	 * @brief 获取操作系统语言
	 * @return 1表示中文，2表示其他即英文
	 */
	public int getOsLangugeType() {
		String language = Locale.getDefault().getLanguage();
		if (language.startsWith("zh")) {
			return 1;
		} else {
			return 2;
		}
	}

	public void setCheckBtnHidden(boolean checkBtnHidden) {
		this.checkBtnHidden = checkBtnHidden;
	}

	public void setLanguageType(int languageType) {
		this.languageType = languageType;
	}
	
	/**
	 * @brief 数据变化后，通知更新UI
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	public CountryCode getSelectedCountryCode() {
		return selectedCountryCode;
	}

	/**
	 * @brief 设置处于选中状态的国别码
	 * @param selectedCountryCode     
	 */ 
	public void setSelectedCountryCode(CountryCode selectedCountryCode) {
		this.selectedCountryCode = selectedCountryCode;
	}

	/**
	 * @brief 获取默认选中的国别码位置
	 * @return 默认选中的国别码位置索引
	 */
	public CountryCode getCountryCode(String codeStr) {
		// 默认选中第一个
		int size = getCount();
		for (int i = 0; i < size; i++) {
			if (codeStr.equals(list.get(i).countryCode)) {
				return list.get(i);
			}
		}
		// 出现错误，默认国别码未找到，则返回第一个
		return size > 0 ? list.get(0) : null;
	}

	/**
	 * 获取显示个数
	 * 
	 * @return 显示个数
	 */
	@Override
	public int getCount() {
		return list.size();
	}

	/**
	 * @brief 获取指定位置的数据
	 * @param position
	 *            指定位置索引
	 * @return
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	/**
	 * @brief 获取指定位置ID
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @brief 返回要显示的列表项控件
	 * @param position
	 *            显示的位置
	 * @param convertView
	 *            显示项控件
	 * @param parent
	 * @return
	 * @see android.widget.Adapter#getView(int, View,
	 *      ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout itemView = (RelativeLayout) convertView;
		CountryCodeItem viewHolder = null;
		if (itemView == null) {
			viewHolder = new CountryCodeItem();
			itemView = (RelativeLayout) inflater.inflate(
					R.layout.tangsdk_countrycode_item, null);
			viewHolder.countryNameTV = (TextView) itemView
					.findViewById(R.id.settings_country_name_tv);
			viewHolder.countryCodeTV = (TextView) itemView
					.findViewById(R.id.settings_country_code_tv);
			viewHolder.checkBtn = (ImageView) itemView
					.findViewById(R.id.settings_country_check_btn);
			itemView.setTag(viewHolder);
		} else {
			viewHolder = (CountryCodeItem) itemView.getTag();
		}
		CountryCode countryCode = list.get(position);
		viewHolder.countryCodeTV.setText(countryCode.countryCode);
		if (languageType == Constants.LANGUAGE_TYPE_CHINESE) {
			viewHolder.countryNameTV.setText(countryCode.countryCHName);
		} else {
			viewHolder.countryNameTV.setText(countryCode.countryENName);
		}
		if (checkBtnHidden) {
			viewHolder.checkBtn.setVisibility(View.INVISIBLE);
		} else if(selectedCountryCode.index == countryCode.index){
			viewHolder.checkBtn.setVisibility(View.VISIBLE);
		}else{
			viewHolder.checkBtn.setVisibility(View.INVISIBLE);
		}
		return itemView;
	}

	/**
	 * @brief 清理占用的内存资源
	 */
	public void clear() {
		inflater = null;
		if (list != null) {
			list.clear();
			list = null;
		}
	}

	class CountryCodeItem {
		TextView countryNameTV;
		TextView countryCodeTV;
		ImageView checkBtn;
	}

}
