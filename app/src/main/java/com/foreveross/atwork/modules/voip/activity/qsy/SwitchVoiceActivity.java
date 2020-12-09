package com.foreveross.atwork.modules.voip.activity.qsy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.modules.voip.adapter.qsy.PhoneListAdapter;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by RocXu on 2015/12/24.
 */
public class SwitchVoiceActivity extends Activity implements View.OnClickListener,
        View.OnLongClickListener, TextWatcher, AdapterView.OnItemClickListener {
    private static final String TAG = "SwitchVoiceActivity";
    private Context context;
    private ImageView mBack;
    private PopupWindow dialpadWindow;
    private EditText et;
    private ListView phoneLV;
    private PhoneListAdapter adapter;
    private Button btn_showDialpad, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_asteriks, btn_sharp, btn_start, btn_backspace, btn_hiddendialpad;
    private String currentVoiceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tangsdk_switch_voice_layout);
        ActivityManagerHelper.addActivity(this);
        context = this;
        initView();
        initPopWindow();
        initFonts();
        initData();
        initListener();
    }

    private void initListener() {
        mBack.setOnClickListener(this);
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_asteriks.setOnClickListener(this);
        btn_sharp.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_backspace.setOnClickListener(this);
        btn_backspace.setOnLongClickListener(this);
        btn_showDialpad.setOnClickListener(this);
        btn_hiddendialpad.setOnClickListener(this);
        phoneLV.setOnItemClickListener(this);
        et.addTextChangedListener(this);
    }

    private void initPopWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.tangsdk_dialpad_layout, null);
        dialpadWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, (int) PromptUtil.convertDipToPx(context, 364));

        et = (EditText) view.findViewById(R.id.dialpad_number_et);
        hideSoftInputMethod(et);
        btn_0 = (Button) view.findViewById(R.id.dialpad_zero);
        btn_1 = (Button) view.findViewById(R.id.dialpad_one);
        btn_2 = (Button) view.findViewById(R.id.dialpad_two);
        btn_3 = (Button) view.findViewById(R.id.dialpad_three);
        btn_4 = (Button) view.findViewById(R.id.dialpad_four);
        btn_5 = (Button) view.findViewById(R.id.dialpad_five);
        btn_6 = (Button) view.findViewById(R.id.dialpad_six);
        btn_7 = (Button) view.findViewById(R.id.dialpad_seven);
        btn_8 = (Button) view.findViewById(R.id.dialpad_eight);
        btn_9 = (Button) view.findViewById(R.id.dialpad_nine);
        btn_asteriks = (Button) view.findViewById(R.id.dialpad_asterisk);
        btn_sharp = (Button) view.findViewById(R.id.dialpad_sharp);
        btn_start = (Button) view.findViewById(R.id.start_call_btn);
        btn_backspace = (Button) view.findViewById(R.id.back_space_btn);
        btn_hiddendialpad = (Button) view.findViewById(R.id.hidden_dialpad_btn);
    }

    private void initFonts(){
        setButtonFont(btn_0);
        setButtonFont(btn_1);
        setButtonFont(btn_2);
        setButtonFont(btn_3);
        setButtonFont(btn_4);
        setButtonFont(btn_5);
        setButtonFont(btn_6);
        setButtonFont(btn_7);
        setButtonFont(btn_8);
        setButtonFont(btn_9);
        setButtonFont(btn_sharp);
        setAsteriksFont();
    }

    private void setButtonFont(Button btn){
//        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
//        btn.setTypeface(tf);
    }

    private void setAsteriksFont(){
//        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Light.ttf");
//        btn_asteriks.setTypeface(tf);
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.back_btn);
        mBack.setVisibility(View.VISIBLE);
        phoneLV = (ListView) findViewById(R.id.phone_listview);
        btn_showDialpad = (Button) findViewById(R.id.show_dailpad_btn);
    }

    private void initData() {
        // 获取当前用户电话列表
        ArrayList<String> phoneList = TangSDKInstance.getInstance().getDelegate().getMyPhoneNumberList();
        // 获取当前语音类型对应的值，Voip对应的值"网络语音"；Tel对应的的值是电话号码
        currentVoiceValue = TangSDKInstance.getInstance().getPhoneCallNumber();
        if(TextUtils.isEmpty(currentVoiceValue)){
            currentVoiceValue = getString(R.string.tangsdk_voice_voip_type);
        }
        adapter = new PhoneListAdapter(context, phoneList, currentVoiceValue);
        phoneLV.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        ActivityManagerHelper.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Editable editable = et.getText();
        int start = et.getSelectionStart();
        if (id == R.id.back_btn) {
            onBackPressed();
            return;
        }
        if (id == R.id.dialpad_zero) {
            editable.insert(start, "0");
            return;
        }
        if (id == R.id.dialpad_one) {
            editable.insert(start, "1");
            return;
        }
        if (id == R.id.dialpad_two) {
            editable.insert(start, "2");
            return;
        }
        if (id == R.id.dialpad_three) {
            editable.insert(start, "3");
            return;
        }
        if (id == R.id.dialpad_four) {
            editable.insert(start, "4");
            return;
        }
        if (id == R.id.dialpad_five) {
            editable.insert(start, "5");
            return;
        }
        if (id == R.id.dialpad_six) {
            editable.insert(start, "6");
            return;
        }
        if (id == R.id.dialpad_seven) {
            editable.insert(start, "7");
            return;
        }
        if (id == R.id.dialpad_eight) {
            editable.insert(start, "8");
            return;
        }
        if (id == R.id.dialpad_nine) {
            editable.insert(start, "9");
            return;
        }
        if (id == R.id.dialpad_asterisk) {
            editable.insert(start, "*");
            return;
        }
        if (id == R.id.dialpad_sharp) {
            editable.insert(start, "#");
            return;
        }
        if (id == R.id.start_call_btn) {
            // start call by phone number
            startPhoneCall();
            return;
        }
        if (id == R.id.back_space_btn) {
            if (editable != null && editable.length() > 0 && start > 0) {
                editable.delete(start - 1, start);
            }
            return;
        }
        if (id == R.id.hidden_dialpad_btn) {
            if (dialpadWindow != null && dialpadWindow.isShowing())
                dialpadWindow.dismiss();
            return;
        }
        if (id == R.id.show_dailpad_btn) {
//            showDialpadWindow();
            startActivity(new Intent(context, CallDialpadActivity.class));
            return;
        }
    }

    private void startPhoneCall() {
        String phoneNumber = et.getText().toString().trim();
        TangSDKInstance.getInstance().startPhone(phoneNumber);
        Toast.makeText(this.getApplicationContext(), context.getString(R.string.tangsdk_wait_phone_call), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void showDialpadWindow() {
        dialpadWindow.setFocusable(true);
        dialpadWindow.setOutsideTouchable(false);
        dialpadWindow.setAnimationStyle(R.style.pop_win_style);
        dialpadWindow.setBackgroundDrawable(new BitmapDrawable());
        dialpadWindow.showAtLocation(btn_start, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public boolean onLongClick(View v) {
        et.setText("");
        return true;
    }


    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
    }

    /**
     * @param
     * @brief 文本变化的监听
     * @see
     */
    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        String keyword = String.valueOf(arg0);
        if (TextUtils.isEmpty(keyword)) {
            btn_start.setEnabled(false);
        } else {
            btn_start.setEnabled(true);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.tangsdk_push_bottom_out);
    }

    /**
     * @param
     * @brief 隐藏软件盘
     * @see
     */
    public void hideSoftInputMethod(EditText ed) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                // 有的机型是setSoftInputShowOnFocus
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String voiceValue = adapter.getItem(position);
        if(TextUtils.isEmpty(voiceValue)){
            return;
        }
        if (voiceValue.equals(context.getString(R.string.tangsdk_voice_voip_type))) {
            if (currentVoiceValue.equals(voiceValue)) {
                Toast.makeText(context, getString(R.string.tangsdk_current_same_as_select_voip), Toast.LENGTH_SHORT).show();
            } else {
                // switch to VOIP
                TangSDKInstance.getInstance().startVOIP();
                Toast.makeText(context, context.getString(R.string.tangsdk_wait_switch_to_voip), Toast.LENGTH_SHORT).show();
                this.finish();
            }
        } else {
            // switch to Phone call
            if (!TextUtils.isEmpty(currentVoiceValue)) {
                if (currentVoiceValue.equals(voiceValue)) {
                    Toast.makeText(context, String.format(getString(R.string.tangsdk_current_same_as_select_phone), voiceValue), Toast.LENGTH_SHORT).show();
                } else {
                    TangSDKInstance.getInstance().startPhone(voiceValue);
                    Toast.makeText(context, context.getString(R.string.tangsdk_wait_phone_call), Toast.LENGTH_SHORT).show();
                    this.finish();
                }
            }
        }
    }
}
