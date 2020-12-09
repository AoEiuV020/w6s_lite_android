package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/8.
 */
public class DropboxOrgGroupItemView extends RelativeLayout {

    private Context mContext;

    private View mVRoot;

    private TextView mOrgName;

    private TextView mOrgSpace;

    private ImageView mExpandedIcon;

    public DropboxOrgGroupItemView(Context context) {
        super(context);
        mContext = context;
        initView(context);

        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
    }

    public DropboxOrgGroupItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropboxOrgGroupItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dropbox_org_group, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mOrgName = view.findViewById(R.id.org_name);
        mOrgSpace = view.findViewById(R.id.org_space);
        mExpandedIcon = view.findViewById(R.id.expand_icon);
    }

    public void setOrganization(Organization org, boolean isExpand) {
        mOrgName.setText(StringUtils.middleEllipse(org.getNameI18n(BaseApplicationLike.baseContext), 40, 12, 9, 9));
        mExpandedIcon.setImageResource(isExpand ? R.mipmap.icon_dropbox_org_group_unexpanded : R.mipmap.icon_dropbox_org_group_expanded);
    }
}
