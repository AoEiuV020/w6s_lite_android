package com.foreveross.atwork.modules.voip.activity.qsy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.CountryCode;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.modules.voip.support.qsy.Constants;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;


public class CallDialpadActivity extends Activity implements OnClickListener, OnLongClickListener, TextWatcher {

	private static final String TAG = "CallDialpadActivity";
	private static final int REQUESTCODE_SELECT_COUNTRY_CODE = 0x01;

	private static final char[] DIAL_CHARS = {'+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '#'};

	private boolean mKeyTonesEnabled;
    private ImageView mBackBtn;
	private TextView mTitleTV;
	private EditText mPhoneNumEdit;
	private TextView mCountryCodeTV, mHintTV;
	private ImageView mDelButton;
	private Button mCallButton;
    private CountryCode selectCountryCode;
	private ProgressDialog mProgressDialog;


	@Override
	public void onCreate(Bundle arg0){
		super.onCreate(arg0);
		this.setContentView(R.layout.tangsdk_call_dial_panel);
        // 初始化控件
		initView();
		// 初始化数据设置
		initData();
        ActivityManagerHelper.addActivity(this);
	}

	private void initView(){
		mPhoneNumEdit = (EditText) this.findViewById(R.id.uc_phone_call_number_edit);
		mPhoneNumEdit.setKeyListener(new RingOutKeyListener());
		mPhoneNumEdit.addTextChangedListener(this);

		mCallButton = (Button) this.findViewById(R.id.phone_call_dial_btn);
		mCallButton.setOnClickListener(this);
		mCallButton.setEnabled(false);

		mDelButton = (ImageView) this.findViewById(R.id.common_button_backspace);
		mDelButton.setOnLongClickListener(this);
		mDelButton.setOnClickListener(this);
		mDelButton.setVisibility(View.GONE);

		mBackBtn = (ImageView)findViewById(R.id.back_btn);
		mBackBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(this);

		mTitleTV = (TextView)this.findViewById(R.id.title_tv);
		mTitleTV.setOnClickListener(this);

		mCountryCodeTV = (TextView)this.findViewById(R.id.uc_phone_call_countrycode_edit);
		mCountryCodeTV.setVisibility(View.INVISIBLE);
		mHintTV = (TextView) findViewById(R.id.edit_hint_tv);
		mHintTV.setVisibility(View.VISIBLE);

		this.findViewById(R.id.common_button_number_0).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_1).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_2).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_3).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_4).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_5).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_6).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_7).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_8).setOnClickListener(this);
		this.findViewById(R.id.common_button_number_9).setOnClickListener(this);
		this.findViewById(R.id.common_button_star).setOnClickListener(this);
		this.findViewById(R.id.common_button_sharp).setOnClickListener(this);

		this.findViewById(R.id.common_button_number_0).setOnLongClickListener(this);
	}

	private void initData(){
		mTitleTV.setText(R.string.tangsdk_china);
		mCountryCodeTV.setText("+86");
		Drawable rightIcon = getResources().getDrawable(R.mipmap.tangsdk_more_icon);
		rightIcon.setBounds(0, 0, rightIcon.getMinimumWidth(), rightIcon.getMinimumHeight());
		mTitleTV.setCompoundDrawables(null, null, rightIcon, null);
	}

	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onPause(){
		super.onPause();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
        ActivityManagerHelper.removeActivity(this);
	}

	private class RingOutKeyListener extends DialerKeyListener {

		@Override
		public int getInputType() {
			return InputType.TYPE_NULL;
		}

		@Override
		protected char[] getAcceptedChars() {
			return DIAL_CHARS;
		}

		@Override
		public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_CALL:
				String countryCode = mCountryCodeTV.getText().toString();
				String number = mPhoneNumEdit.getText().toString();
				if(countryCode != null){
					number = countryCode + number;
				}
				startPhoneCall(number);
				mPhoneNumEdit.getEditableText().clear();
				return true;
			case KeyEvent.KEYCODE_DEL:
				Editable text = mPhoneNumEdit.getEditableText();
				int i;

				del_last_typed_char: for (i = text.length() - 1; i >= 0; i--) {
					for (char ch : DIAL_CHARS) {
						if (text.charAt(i) == ch) {
							text.delete(i, text.length());
							break del_last_typed_char;
						}
					}
				}

				if (i < 0) {
					text.clear();
					return true;
				}

				mPhoneNumEdit.setText(text.toString());
				mPhoneNumEdit.setSelection(mPhoneNumEdit.length());
				return true;
			case KeyEvent.KEYCODE_BACK:
				finish();
				return true;
			default:
				super.onKeyDown(view, content, keyCode, event);
				mPhoneNumEdit.setText(mPhoneNumEdit.getText().toString());
				mPhoneNumEdit.setSelection(mPhoneNumEdit.length()); // move cursor to the end
				return true;
			}
		}
	}

	private void injectKeyEvent(int keyCode) {
		mPhoneNumEdit.getKeyListener().onKeyDown(mPhoneNumEdit, mPhoneNumEdit.getEditableText(), keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
	}

    private void startPhoneCall(String phoneNum) {
        TangSDKInstance.getInstance().startPhone(phoneNum);
        Toast.makeText(this.getApplicationContext(), getString(R.string.tangsdk_wait_phone_call), Toast.LENGTH_SHORT).show();
        // 从堆栈中清除切换语音流程的页面
        ActivityManagerHelper.finishAll();
    }

	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.common_button_number_1) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_1);
			return;
		} else if (i == R.id.common_button_number_2) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_2);
			return;
		} else if (i == R.id.common_button_number_3) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_3);
			return;
		} else if (i == R.id.common_button_number_4) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_4);
			return;
		} else if (i == R.id.common_button_number_5) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_5);
			return;
		} else if (i == R.id.common_button_number_6) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_6);
			return;
		} else if (i == R.id.common_button_number_7) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_7);
			return;
		} else if (i == R.id.common_button_number_8) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_8);
			return;
		} else if (i == R.id.common_button_number_9) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_9);
			return;
		} else if (i == R.id.common_button_number_0) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_0);
			return;
		} else if (i == R.id.common_button_sharp) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_POUND);
			return;
		} else if (i == R.id.common_button_star) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_STAR);
			return;
		} else if (i == R.id.common_button_backspace) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_DEL);
			return;
		} else if (i == R.id.phone_call_dial_btn) {
			view.performHapticFeedback(0);
			injectKeyEvent(KeyEvent.KEYCODE_CALL);
		}  else if(i == R.id.back_btn){
			onBackPressed();
		}else if(i == R.id.title_tv){
			Intent intent = new Intent(this, CountryCodeActivity.class);
			if(selectCountryCode == null){
				intent.putExtra(Constants.EXTRA_COUNTRY_CODE, selectCountryCode);
			}else{
				String defaultCountryCode = mCountryCodeTV.getText().toString();
				intent.putExtra(Constants.EXTRA_COUNTRY_CODE, defaultCountryCode);
			}
			startActivityForResult(intent, REQUESTCODE_SELECT_COUNTRY_CODE);
		}

	}

	@Override
	public boolean onLongClick(View view) {
		int i = view.getId();
		if (i == R.id.common_button_backspace) {
			mPhoneNumEdit.getEditableText().clear();
			mDelButton.setPressed(false);
			return true;

		} else if (i == R.id.common_button_number_0) {
			injectKeyEvent(KeyEvent.KEYCODE_PLUS);
			return true;
		}
		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterTextChanged(Editable s) {
		String digits = s != null ? s.toString() : "";
		if(TextUtils.isEmpty(digits)){
			mDelButton.setVisibility(View.GONE);
			mCountryCodeTV.setVisibility(View.INVISIBLE);
			mHintTV.setVisibility(View.VISIBLE);
			mCallButton.setEnabled(false);
		} else {
			mDelButton.setVisibility(View.VISIBLE);
			mCountryCodeTV.setVisibility(View.VISIBLE);
			mHintTV.setVisibility(View.INVISIBLE);
			mCallButton.setEnabled(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUESTCODE_SELECT_COUNTRY_CODE) {
			if(resultCode == RESULT_OK){
				CountryCode countryCode = (CountryCode) data.getSerializableExtra(Constants.EXTRA_COUNTRY_CODE);
				if (countryCode != null) {
					Log.d(TAG, "onActivityResult-> return countryCode object");
					selectCountryCode = countryCode;
					mTitleTV.setText(selectCountryCode.countryCHName);
					mCountryCodeTV.setText(selectCountryCode.countryCode);
				} else{
					Log.w(TAG, "onActivityResult-> invalid countryCode null");
				}
			}else if(resultCode == RESULT_CANCELED){
				Log.i(TAG, "onActivityResult-> user cancel select countryCode");
			}
		}else{
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    public interface OnPhoneCalled{
        void onPhoneCalled();
    }
}
