package com.foreveross.atwork.modules.dropbox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.cache.DropboxCache;
import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.DropboxConfigRepository;
import com.foreverht.db.service.repository.WatermarkRepository;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxRWSettingActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

import java.util.List;

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
 * Created by reyzhang22 on 2016/10/18.
 */

public class DropboxRWSettingFragment extends BackHandledFragment {

    public static final String READONLY_SETTING_KEY = "readonly";

    public static final String WATERMARK_SETTING_KEY = "show_watermark";

    private ImageView mBack;
    private TextView mTitle;
    private TextView mTvDone;
    private WorkplusSwitchCompat mReadOnlySwitcher;
    private TextView mTvReadOnly;
    private String mSourceId;
    private String mDomainId;
    private Dropbox.SourceType mSourceType;
    private boolean mReadOnly = false;
    private boolean mWatermarkOpen = false;

    private View mWatermarkLayout;
    private WorkplusSwitchCompat mWatermarkSwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dropbox_rw_setting, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        registerListener();
    }

    @Override
    protected void findViews(View view) {
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mTvDone = view.findViewById(R.id.title_bar_common_right_text);

        mReadOnlySwitcher = view.findViewById(R.id.read_only_switcher);
        mTvReadOnly = view.findViewById(R.id.tv_read_only_setting_tip);

        mWatermarkLayout = view.findViewById(R.id.watermark_settings_layout);
        mWatermarkSwitcher = view.findViewById(R.id.watermark_switcher);
    }

    private void initData() {
        mTvDone.setVisibility(View.VISIBLE);
        mTvDone.setTextColor(getResources().getColor(R.color.common_item_black));
        mTvDone.setVisibility(View.GONE);
        mTitle.setText(getString(R.string.drop_rw_manager));

        Bundle bundle = getArguments();
        if (bundle == null ) {
            return;
        }
        mSourceId = bundle.getString(DropboxRWSettingActivity.KEY_INTENT_SOURCE_ID);
        mDomainId = bundle.getString(DropboxRWSettingActivity.KEY_INTENT_DOMAIN_ID);
        mReadOnly = bundle.getBoolean(DropboxRWSettingActivity.KEY_INTENT_READ_ONLY);
        mSourceType = (Dropbox.SourceType) bundle.getSerializable(DropboxRWSettingActivity.KEY_INTENT_SOURCE_TYPE);

        if(Dropbox.SourceType.Discussion == mSourceType) {
            mTvReadOnly.setText(R.string.read_only_discussion_setting_tip);
        } else {
            mTvReadOnly.setText(R.string.read_only_org_setting_tip);
        }

        mReadOnlySwitcher.setChecked(mReadOnly);
        if (!mReadOnly) {
            return;
        }
        mWatermarkOpen = WatermarkCache.getInstance().getWatermarkConfigCache(new Watermark(mSourceId, Watermark.Type.DROPBOX));
        handleWatermarkStatus();
    }

    private void registerListener() {
        mTvDone.setOnClickListener(view -> {
            onBackPressed();
        });

        mBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mReadOnlySwitcher.setOnClickNotPerformToggle(() -> {
            setDropboxSettingRequest(READONLY_SETTING_KEY, !mReadOnly, false);
        });

        mWatermarkSwitcher.setOnClickNotPerformToggle(() -> {
            setDropboxSettingRequest(WATERMARK_SETTING_KEY, mReadOnly, !mWatermarkOpen);
        });
    }

    private void handleWatermarkStatus() {
        mWatermarkLayout.setVisibility(mReadOnly ? View.VISIBLE : View.GONE);
        mWatermarkSwitcher.setChecked(mWatermarkOpen);
    }

    private void setDropboxSettingRequest(String settingKey, boolean readOnly, boolean watermarkOpen) {
        DropboxManager.getInstance().setDropboxRW(mActivity, mDomainId, mSourceType, mSourceId, readOnly, watermarkOpen, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                AtworkToast.showToast(getString(R.string.setting_success));
                if (READONLY_SETTING_KEY.equalsIgnoreCase(settingKey)) {
                    mReadOnly = !mReadOnly;
                    mWatermarkOpen = false;
                    handleReadOnlySettingResult();
                    return;
                }
                mWatermarkOpen = !mWatermarkOpen;
                handleWatermarkSettingResult();
            }

            @Override
            public void onDropboxOpsFail(int status) {
                AtworkToast.showToast(getString(R.string.dropbox_network_error));
            }
        });
    }

    private void handleReadOnlySettingResult() {
        handleWatermarkSettingResult();
        handleWatermarkStatus();
        mReadOnlySwitcher.setChecked(mReadOnly);
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mSourceId);
        dropboxConfig.mReadOnly = mReadOnly;
        DropboxConfigRepository.getInstance().insertOrUpdateDropboxConfig(dropboxConfig);
        DropboxCache.getInstance().setDropboxConfigCache(dropboxConfig);
    }

    private void handleWatermarkSettingResult() {
        mWatermarkSwitcher.setChecked(mWatermarkOpen);
        Watermark watermark = new Watermark();
        watermark.mType = Watermark.Type.DROPBOX;
        watermark.mSourceId = mSourceId;
        WatermarkCache.getInstance().setWatermarkConfigCache(watermark, mWatermarkOpen);
        if (mWatermarkOpen) {
            WatermarkRepository.getInstance().insertOrUpdateWatermark(watermark);
            return;
        }
        WatermarkRepository.getInstance().deleteWatermark(mSourceId, Watermark.Type.DROPBOX.toInt());
    }

    @Override
    protected boolean onBackPressed() {
        finish(true);
        return false;
    }

}
