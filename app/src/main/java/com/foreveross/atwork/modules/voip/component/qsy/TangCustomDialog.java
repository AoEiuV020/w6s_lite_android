/****************************************************************************************
 * Copyright (c) 2010~2013 All Rights Reserved by
 *  G-Net Integrated Service Co.,  Ltd.   
 * @file CommonAlertDialog.java
 * @author peng.xu  
 * @date 2015-2-4 17:21:52
 * @version V1.0  
 ****************************************************************************************/
package com.foreveross.atwork.modules.voip.component.qsy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;

/**
 * @class TangCustomDialog
 * @brief 扩展的AlertDialog对话框
 * @author peng.xu
 * @date 2016-1-6 19:29:05
 */
public class TangCustomDialog extends Dialog {

	public TangCustomDialog(Context context) {
		super(context);
	}

	public TangCustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String confirm_btnText;
		private int confirm_btnColor;
		private View contentView;
		private boolean cancelable;
		private String[] otherBtns;// 可能会有多个按钮[除确定按钮意外]
		private int[] otherBtnColors;

		private OnClickListener confirm_btnClickListener;

		private OnClickListener[] others_btnClickListeners;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setOtherBtns(String[] btns,
				OnClickListener[] listeners) {
			this.otherBtns = btns;
			this.others_btnClickListeners = listeners;
			return this;
		}

		public Builder setOtherBtns(String[] btns, int[] btnColors,
									OnClickListener[] listeners) {
			this.otherBtns = btns;
			this.otherBtnColors = btnColors;
			this.others_btnClickListeners = listeners;
			return this;
		}

		/**
		 * @brief 设置Dialog承载的View
		 * @param v View
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int confirm_btnText,
				OnClickListener listener) {
			this.confirm_btnText = (String) context.getText(confirm_btnText);
			this.confirm_btnClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String confirm_btnText, int confirm_btnColor,
										 OnClickListener listener) {
			this.confirm_btnText = confirm_btnText;
			this.confirm_btnColor = confirm_btnColor;
			this.confirm_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int cancel_btnText,
				OnClickListener listener) {
			//			this.cancel_btnText = (String) context.getText(cancel_btnText);
			//			this.cancel_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String cancel_btnText,
				OnClickListener listener) {
			//			this.cancel_btnText = cancel_btnText;
			//			this.cancel_btnClickListener = listener;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public TangCustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final TangCustomDialog dialog = new TangCustomDialog(context,
					R.style.dialogViewStyle);
			dialog.setCancelable(cancelable);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title and message
			TextView titleTV = (TextView) layout.findViewById(R.id.title_tv);
			TextView messageTV = (TextView) layout
					.findViewById(R.id.message_tv);
			if (!TextUtils.isEmpty(title)) {
				titleTV.setText(title);
			} else {
				titleTV.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(message)) {
				messageTV.setText(message);
			}
			Button posBtn = new Button(context);
			posBtn.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, (int) PromptUtil.convertDipToPx(context, 44), 1.0f));
			
			posBtn.setTextSize(17);
			// 底部按钮区域
			LinearLayout buttonLayout = (LinearLayout) layout
					.findViewById(R.id.buttonLayout);

			if (null == otherBtns || otherBtns.length == 0) {// 只有确定按钮
				buttonLayout.setOrientation(LinearLayout.VERTICAL);
				posBtn.setBackgroundResource(R.drawable.tangsdk_alert_bottom_button);
			} else if (null != otherBtns && otherBtns.length == 1) {// 包含取消按钮
				buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
				posBtn.setBackgroundResource(R.drawable.tangsdk_alert_right_button);
			} else {// 多个按钮的场景
				buttonLayout.setOrientation(LinearLayout.VERTICAL);
				posBtn.setBackgroundResource(R.drawable.tangsdk_alert_middle_button);
			}

			posBtn.setText(confirm_btnText);
			if(confirm_btnColor > 0){
				posBtn.setTextColor(context.getResources().getColor(confirm_btnColor));
			}else {
				posBtn.setTextColor(Color.parseColor("#3B4F61"));
			}
			posBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (confirm_btnClickListener != null) {
						// @attention 必须要将which值带入，有些业务场景就是根据确定按钮还是取消按钮ID来作不同的处理
						confirm_btnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
					}
					dialog.dismiss();
				}
			});

            int len = otherBtns == null ? 0 : otherBtns.length;
			if (null != otherBtns && otherBtns.length > 0) {
				for (int i = 0; i < len; i++) {
					final int index = i;
					// @attention 必须要将which值带入，有些业务场景就是根据确定按钮还是取消按钮ID来作不同的处理
					final int which = i == len - 1 ? DialogInterface.BUTTON_NEGATIVE
							: (DialogInterface.BUTTON_NEUTRAL + (i - (len - 2)));
					Button otherButton = new Button(context);
					otherButton.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, (int)PromptUtil.convertDipToPx(context, 44),
							1.0f));
					otherButton.setText(otherBtns[i]);
					if(otherBtnColors != null && otherBtnColors.length > 0){
						otherButton.setTextColor(context.getResources().getColor(otherBtnColors[i]));
					}else {
						otherButton.setTextColor(Color.parseColor("#3B4F61"));
					}
					otherButton.setTextSize(17);
					if (1 == len) {
						otherButton
								.setBackgroundResource(R.drawable.tangsdk_alert_left_button);
					} else if (i < (otherBtns.length - 1)) {
						otherButton
								.setBackgroundResource(R.drawable.tangsdk_alert_middle_button);
					} else {
						otherButton
								.setBackgroundResource(R.drawable.tangsdk_alert_bottom_button);
					}
					otherButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (null != others_btnClickListeners
									&& null != others_btnClickListeners[index]) {
								others_btnClickListeners[index].onClick(dialog,
										which);
							}
							dialog.dismiss();
						}

					});
					buttonLayout.addView(otherButton);
				}
			}
			
			buttonLayout.addView(posBtn);

			final int viewWidth = (int)PromptUtil.convertDipToPx(context, 250f);
			layout.setMinimumWidth(viewWidth);
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
