package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;


public class EmployeeSearchListItemView extends RelativeLayout {

    private View mVRoot;
    //头像
    private ImageView mAvatarView;
    private TextView mTitleView;
    private TextView mInfoView;
    private TextView mJobView;
    private ImageView mSelectView;

    private Employee mEmployee;
    private String mKey;

    private boolean selectedMode = false;

    public EmployeeSearchListItemView(Context context) {
        super(context);
        initView();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
    }

    public EmployeeSearchListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);

    }

    public void setSelectedMode(boolean selectedMode) {
        this.selectedMode = selectedMode;
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_contact_list_item, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.contact_list_item_avatar);
        mTitleView = view.findViewById(R.id.contact_list_item_title);
        mInfoView = view.findViewById(R.id.contact_list_item_info);
        mSelectView = view.findViewById(R.id.chat_list_select);
        mJobView = view.findViewById(R.id.contact_list_item_job);
        mSelectView.setVisibility(GONE);
    }


    public void refresh() {
        ContactInfoViewUtil.dealWithContactInitializedStatus(mAvatarView, mTitleView, mEmployee, true, true);

        if(AtworkConfig.EMPLOYEE_CONFIG.getShowPeerJobTitle() && !ListUtil.isEmpty(mEmployee.positions)) {
            Position showPosition = mEmployee.positions.get(0);
            String jobTitle = showPosition.jobTitle;
            if(1 < mEmployee.positions.size()) {
                jobTitle += getContext().getString(R.string.many_job_title);

            }

            String orgInfo = showPosition.orgName;

            mJobView.setText(jobTitle);
            mInfoView.setText(orgInfo);

            highlightKey(mTitleView);
            highlightKey(mJobView);
            highlightKey(mInfoView);

            mJobView.setVisibility(VISIBLE);
            mInfoView.setVisibility(VISIBLE);

        } else {
            mJobView.setVisibility(GONE);
            mInfoView.setVisibility(GONE);
        }
    }

    private void highlightKey(TextView textView) {

        int color = getContext().getResources().getColor(R.color.common_message_num_bg);

        String text = textView.getText().toString();
        if (!StringUtils.isEmpty(text)) {
            int start = text.indexOf(mKey);
            int end = -1;
            if (start != -1) {
                end = start + mKey.length();
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            }
        }
    }

    public void refreshView(Employee employee, String key) {
        mEmployee = employee;
        mKey = key;

        refresh();
    }

}
