package com.foreveross.atwork.modules.contact.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.OrganizationAsyncNetService;
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusBottomPopDialog;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.ContactProviderRepository;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.contact.activity.EmployeeTreeActivity;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.activity.SyncContactFailedActivity;
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter;
import com.foreveross.atwork.modules.contact.adapter.EmployeeTreeListAdapter;
import com.foreveross.atwork.modules.contact.component.ContactListItemView;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.component.SelectContactHead;
import com.foreveross.atwork.modules.group.inter.SyncActionListener;
import com.foreveross.atwork.modules.group.listener.DeptSelectedListener;
import com.foreveross.atwork.modules.group.listener.LoadMoreListener;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.organization.manager.DeptManager;
import com.foreveross.atwork.modules.search.component.SearchHeadView;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//==========同步联系人到手机功能==================

/**
 * 组织架构显示页面
 */
public class EmployeeTreeFragment extends BackHandledFragment implements SelectedChangedListener, DeptSelectedListener, SyncActionListener, LoadMoreListener {

    private static int MAX_USER = 500;

    private static final int MSG_SYNCING_CONTACT_TO_MOBILE = 0x0017;
    private static final int MSG_FINISH_CONTACT_TO_MOBILE = 0x0018;

    private static final String TAG = EmployeeTreeFragment.class.getSimpleName();


    private ListView mContactTreeView;

    private ListView mContactSearchListView;

    private EmployeeTreeListAdapter mEmployeeTreeListAdapter;

    private ContactListArrayListAdapter mSearchContactListAdapter;

    public List<ContactModel> mRootContactModels = new ArrayList<>();

    private ArrayList<ShowListItem> mUserListFailed = new ArrayList<>();


    private UserSelectControlAction mUserSelectControlAction;
    private UserSelectActivity.SelectMode mSelectMode = UserSelectActivity.SelectMode.NO_SELECT;

    private UserSelectActivity.SelectAction mSelectAction;

    private ProgressDialogHelper mProgressDialogHelper;

    private TextView mMoreView;

    private PopUpView mPopUpView;

    private boolean mSyncContactModel = false;

    private SelectContactHead mSelectUserHead;

    private TextView mTvSearchShowTitle;

    private TextView mTvTitle;

    private Button mBtnSyncContactOk;

    private SearchHeadView mSearchHeadView;

    private AtworkAlertDialog mAtworkAlertDialog;
    /**
     * 部门下的直接人员
     */
    private Map<OrganizationResult, List<Employee>> mSelectedActionDeptContacts = new HashMap<>();

    private List<SelectedChangedListener> selectedChangedListeners = new ArrayList<>();

    private List<String> notAllowedSelectedContacts = new ArrayList<>();

    private List<String> mSelectedContacts = new ArrayList<>();

    private List<? extends ShowListItem> mCallbackContactsSelected;

    private boolean mUserCancel = false;

    private boolean mIsSuggestiveHideMe;


    private boolean mIsSingleContact;

    private UiHandler uiHandler = new UiHandler(this);

    public Organization mOrg;

    private List<String> mCheckedList = new ArrayList<>();

    private Boolean mIsMandatoryFilterSenior;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mUserSelectControlAction = getArguments().getParcelable(UserSelectActivity.DATA_USER_SELECT_PARAMS);

            mOrg = getArguments().getParcelable(EmployeeTreeActivity.DATA_ORG);


            if(null != mUserSelectControlAction) {
                mSelectMode = mUserSelectControlAction.getSelectMode();
                mSelectAction = mUserSelectControlAction.getSelectAction();
                mIsSuggestiveHideMe = mUserSelectControlAction.isSuggestiveHideMe();
                mIsSingleContact = (1 == mUserSelectControlAction.getMax());
                mCallbackContactsSelected = mUserSelectControlAction.getCallbackContactsSelected();
                mIsMandatoryFilterSenior = mUserSelectControlAction.isMandatoryFilterSenior();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (UserSelectActivity.SelectMode.SELECT.equals(mSelectMode)) {
            view.findViewById(R.id.title_bar_contact_tree_layout).setVisibility(View.GONE);


            mContactTreeView.setAdapter(null);

            mEmployeeTreeListAdapter.setSingleContact(mIsSingleContact);
            mContactTreeView.setAdapter(mEmployeeTreeListAdapter);
        }

        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        handleMoreView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void findViews(View view) {
        mContactTreeView = view.findViewById(R.id.contact_tree_view);
        mContactSearchListView = view.findViewById(R.id.lw_contact_search);
        mTvSearchShowTitle = view.findViewById(R.id.tv_contact_title);
        mMoreView = view.findViewById(R.id.title_bar_contact_tree_search_more);
        mTvTitle = view.findViewById(R.id.title_bar_contact_tree_title);
        mTvTitle.setText(mOrg.getNameI18n(BaseApplicationLike.baseContext));
        mSelectUserHead = view.findViewById(R.id.select_user_contact_head);
        mSelectUserHead.setSyncActionListener(this);
        mBtnSyncContactOk = view.findViewById(R.id.async_contact_to_mobile_ok);
        mBtnSyncContactOk.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
        mSearchHeadView = new SearchHeadView(mActivity);
        mSearchHeadView.setHint(R.string.action_search);

        initSyncContactAlertDialog();
    }

    public void handleMoreView() {

        getPopArrayList(popItemList -> {
            if(mSyncContactModel || ListUtil.isEmpty(popItemList)) {
                mMoreView.setVisibility(View.GONE);

            } else {
                mMoreView.setVisibility(View.VISIBLE);

            }
        });


    }

    private void initSyncContactAlertDialog() {
        mAtworkAlertDialog = new AtworkAlertDialog(mActivity)
                .setType(AtworkAlertDialog.Type.PROGRESS)
                .hideTitle()
                .setProgressDesc(getResources().getString(R.string.syncing_contact))
                .hideBrightBtn()
                .setMax(100);


        mAtworkAlertDialog.setDismissListener(dialog -> mUserCancel = true);
    }

    private void registerListener() {


        mBtnSyncContactOk.setOnClickListener(v -> {
            final List<ShowListItem> contactList = mSelectUserHead.getSelectedContact();
            if (ListUtil.isEmpty(contactList)) {
                AtworkToast.showToast(getResources().getString(R.string.select_user_zero));
                return;
            } else {

                new AtworkAlertDialog(mActivity, AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.ask_to_sync_contact_to_mobile)
                        .setClickBrightColorListener(dialog -> startSyncContacts())
                        .show();

            }
        });

        mMoreView.setOnClickListener(v -> getPopArrayList(this::popBottomDialog));


        if (mSelectMode.equals(UserSelectActivity.SelectMode.SELECT)) {

            mContactTreeView.setOnTouchListener((v, event) -> {
                AtworkUtil.hideInput(mActivity);
                return false;
            });



            mContactTreeView.setOnItemClickListener((parent, view, position, id) -> {
                final ContactModel contactModel = (ContactModel) parent.getItemAtPosition(position);
                if (contactModel.type() == ContactModel.ContactType.Employee) {

                    EmployeeResult employeeResult = (EmployeeResult) contactModel;


                    if(UserSelectActivity.SelectAction.SCOPE == mSelectAction
                            && !ContactInfoViewUtil.canEmployeeTreeEmpSelect(employeeResult, mEmployeeTreeListAdapter.getSelectContacts())) {
                        return;
                    }

                    if (mActivity instanceof UserSelectActivity) {
                        UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
                        if (mIsSingleContact) {
                            contactModel.select();
                            Employee emp = employeeResult.toEmployee();
                            mSelectUserHead.selectContact(emp);
                            SelectedContactList.setContactList(mSelectUserHead.getSelectedContact());
                            Intent returnIntent = new Intent();
                            mActivity.setResult(Activity.RESULT_OK, returnIntent);
                            mActivity.finish();
                            return;
                        }

                        if(notAllowedSelectedContacts.contains(employeeResult.userId)) {
                            return;
                        }


                        if (!contactModel.selected && !userSelectActivity.isAllowed()) {

//                            ToastTipHelper.toastMaxTipInUserSelectActivity(userSelectActivity, mSelectAction);
                            toast(mUserSelectControlAction.getMaxTip());


                            return;
                        }

                        if (!contactModel.selected && userSelectActivity.isOneSelected()) {
                            return;
                        }

                        contactModel.select();
                        Employee emp = employeeResult.toEmployee();
                        userSelectActivity.action(emp);
                        refresh();
                    }
                } else {
                    loadMoreOrgContactModel(contactModel, false, new QureyOrganizationViewRange());
                }
            });
        }
        //非选择模式下的事件
        else if (mSelectMode.equals(UserSelectActivity.SelectMode.NO_SELECT)) {

            //FIXME 去掉长按功能
//            mContactTreeView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    final ContactModel contactModel = (ContactModel) parent.getItemAtPosition(position);
//                    //人员
//                    if (contactModel instanceof ContactTreeResponseJSON.Employee) {
//                        EmployeeTreeItemView employeeTreeItemView = (EmployeeTreeItemView) view;
//                        if (employeeTreeItemView.isContactShow()) {
//                            employeeTreeItemView.hiddenContact();
//                        } else {
//                            employeeTreeItemView.showContact();
//                        }
//                    }
//                    return true;
//                }
//            });

            //点击事件(非群组发起的时候)
            mContactTreeView.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        final ContactModel contactModel = (ContactModel) parent.getItemAtPosition(position);

                        if (contactModel.type() == ContactModel.ContactType.Employee) {

                            //非同步手机到通讯录模式下，跳转到个人详情页面
                            if (!mSyncContactModel) {
                                toContactInfo((EmployeeResult) contactModel);
                            }
                            //同步手机到通讯录模式下，选中当前人员
                            else {
                                if (!contactModel.selected && !isAllowed()) {
                                    AtworkToast.showToast(getResources().getString(R.string.sync_max_alert));
                                    return;
                                }

                                EmployeeResult employeeResult = (EmployeeResult) contactModel;
                                contactModel.select();
                                Employee emp = employeeResult.toEmployee();
                                syncToMobileAction(emp);
                                refresh();
                                updateSyncNum();
                            }
                            return;
                        }

                        loadMoreOrgContactModel(contactModel, false, new QureyOrganizationViewRange());
                    }
            );

            mSearchHeadView.getEditTextSearch().setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    return;
                }

                EmployeeSearchFragment employeeSearchFragment = new EmployeeSearchFragment();
                if (!employeeSearchFragment.isVisible()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EmployeeSearchFragment.DATA_ORG_CODE, mOrg.mOrgCode);
                    employeeSearchFragment.setArguments(bundle);
                    employeeSearchFragment.show(getFragmentManager(), "Contact_Search");
                    mSearchHeadView.clearFocus();
                }
            });

            mSelectUserHead.setSelectUserSearchListener(new SelectContactHead.SelectContactSearchListener() {
                @Override
                public void searchStart(String key) {

                }

                @Override
                public void searchSuccess(List<? extends ShowListItem> contactList, List<String> userIdNeedCheckOnlineStatusList, boolean isAllSearchNoResult) {
                    if (isAdded()) {
                        mContactTreeView.setVisibility(View.GONE);
                        mContactSearchListView.setVisibility(View.VISIBLE);

                        List<ShowListItem> selectedContactList = mSelectUserHead.getSelectedContact();
                        for (ShowListItem selected : selectedContactList) {
                            for (ShowListItem contact : contactList) {
                                if (selected.getId().equals(contact.getId())) {
                                    contact.select(true);
                                }
                            }
                        }

                        mSearchContactListAdapter.clear();
                        mSearchContactListAdapter.addAll(contactList);

                        checkOnlineStatus(userIdNeedCheckOnlineStatusList);
                    }

                }

                @Override
                public void searchClear() {
                    mContactTreeView.setVisibility(View.VISIBLE);
                    mContactSearchListView.setVisibility(View.GONE);
                }

                @Override
                public void refresh(ShowListItem contact) {
                    int position = mSearchContactListAdapter.getPosition(contact);
                    if (-1 != position) {
                        mSearchContactListAdapter.getItem(position).select(false);
                        mSearchContactListAdapter.notifyDataSetChanged();
                    }


                }
            });

            mContactSearchListView.setOnTouchListener((v, event) -> {
                AtworkUtil.hideInput(mActivity, mSelectUserHead.mSearchText);
                return false;
            });

            mContactSearchListView.setOnItemClickListener((parent, view, position, id) -> {

                final Employee emp = (Employee) parent.getItemAtPosition(position);

                if(UserSelectActivity.SelectAction.SCOPE == mSelectAction
                        && !ContactInfoViewUtil.canEmployeeTreeEmpSelect(emp, mEmployeeTreeListAdapter.getSelectContacts())) {
                    return;
                }

                if (!emp.mSelect && !isAllowed()) {
                    AtworkToast.showToast(getResources().getString(R.string.sync_max_alert));
                    return;
                }

                emp.select();
                ContactListItemView contactListItemView = (ContactListItemView) view;
                contactListItemView.refreshContactView(emp);
                syncToMobileAction(emp);

                mSelectUserHead.mSearchText.setText(StringUtils.EMPTY);
            });


            //左上角返回按钮
            getView().findViewById(R.id.title_bar_contact_tree_back).setOnClickListener(v -> {
                if (mSyncContactModel) {
                    WeakReference<Activity> weakReference = new WeakReference<>(mActivity);
                    AtworkUtil.hideInput(weakReference);
                    toCommonModel();
                } else {
                    mActivity.finish();
                    //界面回退动画
                    mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            });
        }


    }

    private void popBottomDialog(List<String> popItemList) {
        WorkplusBottomPopDialog dialog = new WorkplusBottomPopDialog();

        dialog.refreshData(popItemList.toArray(new String[0]));

        dialog.setItemOnListener(tag -> {

            dialog.dismiss();

            if (getResources().getString(R.string.sync_contact_to_mobile).equals(tag)) {

                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EmployeeTreeFragment.this, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EmployeeTreeFragment.this, new String[]{Manifest.permission.READ_CONTACTS}, new PermissionsResultAction() {
                            @Override
                            public void onGranted() {
                                toSyncContactModel();
                            }

                            @Override
                            public void onDenied(String permission) {
                                AtworkUtil.popAuthSettingAlert(getContext(), permission);
                            }
                        });

                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(getContext(), permission);
                    }
                });
            }

            if (getResources().getString(R.string.exit_organization).equals(tag)){

                if(LoginUserInfo.getInstance().getLoginUserId(getActivity()).equals(mOrg.mOwner)) {
                    new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                            .setContent(R.string.admin_not_allow_exit_org)
                            .hideDeadBtn()
                            .show();

                } else {
                    new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                            .setContent(R.string.ask_exit_organization)
                            .setClickBrightColorListener(dialog1 -> {
                                OrganizationManager.getInstance().removeOrganizationRemote(mActivity, mOrg.mOrgCode, new OrganizationManager.OnRemoveOrgListener() {
                                    @Override
                                    public void onSuccess() {
                                        finish();

                                    }

                                    @Override
                                    public void networkFail(int errorCode, String errorMsg) {
                                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                                    }
                                });
                            })
                            .show();
                }

            }

        });

        dialog.show(getChildFragmentManager(), "show_more_employee_tree");
    }

    public void checkOnlineStatus(List<String> userIdNeedCheckOnlineStatusList) {
        OnlineManager.getInstance().checkOnlineList(getActivity(), userIdNeedCheckOnlineStatusList, onlineList -> mSearchContactListAdapter.notifyDataSetChanged());
    }

    @SuppressLint("StaticFieldLeak")
    private void getPopArrayList(BaseQueryListener<List<String>> baseQueryListener) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                return getPopArrayListSync();
            }

            @Override
            protected void onPostExecute(List<String> popItemList) {
                baseQueryListener.onSuccess(popItemList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @NonNull
    private List<String> getPopArrayListSync() {
        List<String> popItemList = new ArrayList<>();
        popItemList.add(getString(R.string.exit_organization));

        if (shouldAddSyncContactItem()) {
            popItemList.add(getString(R.string.sync_contact_to_mobile));
        }
        //管理员能退出组织，组织创建者不能退出组织
        if(LoginUserInfo.getInstance().getLoginUserId(getActivity()).equals(mOrg.mOwner) || !DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            popItemList.remove(getString(R.string.exit_organization));
        }
        return popItemList;
    }

    private boolean shouldAddSyncContactItem() {
        if(DomainSettingsManager.getInstance().handleMobileContactSyncFeature() != DomainSettingsManager.SYNC_CONTACT_UNLIMIT) {
            return false;
        }

        if(!OrganizationSettingsManager.getInstance().isSeniorShowOpen(getActivity(), mOrg.mOrgCode)) {
            return true;
        }

        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(getActivity());
        Employee loginEmp = EmployeeManager.getInstance().queryEmpInSync(getActivity(), loginUserId, mOrg.mOrgCode);

        return null != loginEmp && loginEmp.senior;

    }

    private void updateSyncNum() {
        int size = mSelectUserHead.getSelectedContact().size();
        if (size > 0) {
            mBtnSyncContactOk.setTextColor(getResources().getColor(R.color.common_item_black));
            mBtnSyncContactOk.setText(getResources().getString(R.string.ok_with_num, size + ""));
        } else {
            //恢复为原样
            mBtnSyncContactOk.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
            mBtnSyncContactOk.setText(getResources().getString(R.string.ok));

        }
    }

    private void startSyncContacts() {
        mUserCancel = false;

        mAtworkAlertDialog.show();
        //刷新 progressbar
        mAtworkAlertDialog.setProgress(0);

        final ContactProviderRepository contactProviderRepository = new ContactProviderRepository(mActivity.getContentResolver());

        new Thread(() -> {
            try {
                //让同步操作不至于太快, 一闪而过
                Thread.currentThread().sleep(500);

                int progress = 0;
                mUserListFailed.clear();
                List<ShowListItem> contactList = mSelectUserHead.getSelectedContact();
//                int contactCount = mSelectUserHead.getSelectedContactHavingPhone().size();

                for (ShowListItem contact : contactList) {

                    if(mUserCancel){ // user cancel the user syncing
                        return;
                    }

                    boolean success = contactProviderRepository.syncUserToMobile(contact);
                    if (success) {
                        progress++;

                        Message message = Message.obtain();
                        message.what = MSG_SYNCING_CONTACT_TO_MOBILE;
                        message.arg1 = progress;
                        message.arg2 = contactList.size();

                        uiHandler.sendMessage(message);
                    } else {
                        mUserListFailed.add(contact);
                    }

                }

                Message message = Message.obtain();
                //成功的人数
                message.arg1 = progress;
                //失败的人数
                message.arg2 = mUserListFailed.size();
                message.what = MSG_FINISH_CONTACT_TO_MOBILE;

                uiHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }


    /**
     * 加载一个组织下的更多的人员
     *
     * @param contactModel
     */
    private void loadMoreOrgContactModel(final ContactModel contactModel, boolean loadMore, final QureyOrganizationViewRange range) {

        if (!contactModel.isLoaded()) {
            contactModel.loadingStatus = ContactModel.LOAD_STATUS_LOADING;
            refresh();
        }

        if (contactModel.expand && !loadMore) {
            contactModel.expand = false;
            refresh();

        } else {
            //已加载数据但是未有展开
            if (contactModel.isLoaded() && !loadMore) {
                contactModel.expand = true;
                refresh();
            } else {
                //还未有加载数据，先加载数据先
                String orgId = contactModel.id;
                int level = contactModel.level;
//                mProgressDialogHelper.show();

                ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                        .setSelectMode(UserSelectActivity.SelectMode.NO_SELECT != mSelectMode)
                        .setMandatoryFilterSenior(mIsMandatoryFilterSenior);

                DeptManager.INSTANCE.queryUserOrgAndEmployeeCompat(BaseApplicationLike.baseContext, mOrg.mOrgCode, orgId, level, range, expandEmpTreeAction, new OrganizationAsyncNetService.OnEmployeeTreeLoadListener() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        mProgressDialogHelper.dismiss();

                        if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                            AtworkToast.showResToast(R.string.network_not_good);

                        }

                    }

                    @Override
                    public void onSuccess(int loadedStatus, List<OrganizationResult> newOrganizationList) {
                        if (null != newOrganizationList) {
                            OrganizationResult orgTreeContactModelHandling = (OrganizationResult) contactModel;


                            clipModelChildrenOfChildren(newOrganizationList.get(0), orgTreeContactModelHandling);
                            clipModelChildren(orgTreeContactModelHandling, newOrganizationList.get(0), range);


                            for (OrganizationResult child : orgTreeContactModelHandling.children) {
                                child.selected = contactModel.selected;
                            }

                            for (EmployeeResult employeeResult : orgTreeContactModelHandling.employeeResults) {
                                employeeResult.selected = contactModel.selected;
                                employeeResult.parentModel = contactModel;
                            }

                            contactModel.expand = true;
                        }


                        contactModel.addLoadedStatus(loadedStatus);

                        refresh();
                        mProgressDialogHelper.dismiss();
                    }

                });
            }
        }
    }




    private void toContactInfo(EmployeeResult employeeResultModel) {
        if (!UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            return;
        }
        UserManager.getInstance().queryUserByUserId(mActivity, employeeResultModel.userId, employeeResultModel.domainId, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                if(null != getActivity()) {
                    startActivity(PersonalInfoActivity.getIntent(getActivity(), user));

                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }

        });

    }

    private void init() {
        boolean selectable = false;
        if(null == mSelectMode) {
            mSelectMode = UserSelectActivity.SelectMode.NO_SELECT;
        }

        if (UserSelectActivity.SelectMode.SELECT.equals(mSelectMode)) {
            selectable = true;

            if (mActivity instanceof UserSelectActivity) {
                UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
                notAllowedSelectedContacts = userSelectActivity.getNotAllowedSelectedStringList();
            }
        }

        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            mContactTreeView.addHeaderView(mSearchHeadView);
        }

        mEmployeeTreeListAdapter = new EmployeeTreeListAdapter(mActivity, selectable,  mSelectAction, mOrg, this, this);
        mEmployeeTreeListAdapter.setSuggestiveHideMe(mIsSuggestiveHideMe);
        if(mActivity instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
            mEmployeeTreeListAdapter.setSelectContacts(userSelectActivity.getSelectedList());
            mEmployeeTreeListAdapter.setNotAllowedSelectedContacts(userSelectActivity.getNotAllowedSelectedStringList());
        }

        mSearchContactListAdapter = new ContactListArrayListAdapter(mActivity, true);
        mSearchContactListAdapter.setSelectAction(mSelectAction);
        mSearchContactListAdapter.setSelectContacts(mEmployeeTreeListAdapter.getSelectContacts());
        mSearchContactListAdapter.setNotAllowedSelectedContacts(mEmployeeTreeListAdapter.getNotAllowedSelectedContacts());

        mContactTreeView.setAdapter(mEmployeeTreeListAdapter);

        mContactSearchListView.setAdapter(mSearchContactListAdapter);

        initRootContactModelUI();


//        mPopUpView = new PopUpView(mActivity);
//        mPopUpView.addPopItem(R.mipmap.icon_phone_single, R.string.sync_contact_to_mobile, 1);
        QureyOrganizationViewRange range = new QureyOrganizationViewRange();
        loadData(range);
        mEmployeeTreeListAdapter.setCurrentRange(range);
        if (!"none".equalsIgnoreCase(OrganizationSettingsManager.getInstance().getOrganizationWatermarkSettings(mOrg.mOrgCode))) {
            WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mContactTreeView, mOrg.mOrgCode);
        }

    }

    private void initRootContactModelUI() {
        mRootContactModels.clear();
        mRootContactModels.add(OrganizationResult.toOrganizationResult(mOrg));
        refresh();
    }



    public void loadData(QureyOrganizationViewRange range) {
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
//        mProgressDialogHelper.show();

        ContactModel topContactModel = mRootContactModels.get(0);
        if(null != topContactModel) {
            if (!topContactModel.isLoaded()) {
                topContactModel.loadingStatus = ContactModel.LOAD_STATUS_LOADING;
                refresh();
            }
        }

        ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                .setSelectMode(UserSelectActivity.SelectMode.NO_SELECT != mSelectMode)
                .setMandatoryFilterSenior(mIsMandatoryFilterSenior);

        DeptManager.INSTANCE.queryUserOrgAndEmployeeCompat(BaseApplicationLike.baseContext, mOrg.mOrgCode, mOrg.mId, OrganizationAsyncNetService.TOP_LEVEL, range, expandEmpTreeAction, new OrganizationAsyncNetService.OnEmployeeTreeLoadListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();

                if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.network_not_good);

                }
            }

            @Override
            public void onSuccess(int loadedStatus, List<OrganizationResult> newOrganizationList) {
                if (null != newOrganizationList) {
                    mEmployeeTreeListAdapter.setCurrentRange(range);

                    for (OrganizationResult orgResult : newOrganizationList) {
                        for (ContactModel oldContactMode : mRootContactModels) {
                            if (oldContactMode.id.equals(orgResult.id)) {

                                OrganizationResult oldOrganizationTreeContactModel = (OrganizationResult) oldContactMode;

                                clipModelChildrenOfChildren(orgResult, oldOrganizationTreeContactModel);
                                clipModelChildren(oldOrganizationTreeContactModel, orgResult, range);

//                                oldOrganizationTreeContactModel.children.subList(range.getOrgSkip() , oldOrganizationTreeContactModel.children.size()).clear();
//                                oldOrganizationTreeContactModel.children.addAll(orgResult.children);
//
//                                oldOrganizationTreeContactModel.employeeResults.subList(range.getEmployeeSkip() , oldOrganizationTreeContactModel.employeeResults.size()).clear();
//                                oldOrganizationTreeContactModel.employeeResults.addAll(orgResult.employeeResults);
                            }
                        }
                    }


                    if (null != topContactModel) {
                        topContactModel.expand = true;
                    }

                }

                if (null != topContactModel) {
                    topContactModel.addLoadedStatus(loadedStatus);
                }

                refresh();
                mProgressDialogHelper.dismiss();
            }


        });

    }

    private void clipModelChildrenOfChildren(OrganizationResult targetOrgResult, OrganizationResult fromOrgResult) {
        for(OrganizationResult orgResultChild : targetOrgResult.children) {
            for(OrganizationResult oldOrgTreeContactModelChild: fromOrgResult.children) {
                if(oldOrgTreeContactModelChild.id.equals(orgResultChild.id)) {
                    orgResultChild.children.clear();
                    orgResultChild.children.addAll(oldOrgTreeContactModelChild.children);
                    orgResultChild.employeeResults.clear();
                    orgResultChild.employeeResults.addAll(oldOrgTreeContactModelChild.employeeResults);
                }
            }
        }
    }

    private void clipModelChildren(OrganizationResult targetOrgResult, OrganizationResult fromOrgResult, QureyOrganizationViewRange range) {
        targetOrgResult.children.subList(range.getOrgSkip() , targetOrgResult.children.size()).clear();

        if (!targetOrgResult.children.isEmpty()) {
            targetOrgResult.children.get(targetOrgResult.children.size() - 1).isLoadCompleted = true;
        }

        targetOrgResult.children.addAll(fromOrgResult.children);


        targetOrgResult.employeeResults.subList(range.getEmployeeSkip(), targetOrgResult.employeeResults.size()).clear();

        if (!targetOrgResult.employeeResults.isEmpty()) {
            targetOrgResult.employeeResults.get(targetOrgResult.employeeResults.size() - 1).isLoadCompleted = true;
        }

        targetOrgResult.employeeResults.addAll(fromOrgResult.employeeResults);
    }

    /**
     * 清空数据, 刷新视图
     * */
    public void refreshToEmptyView() {
        mEmployeeTreeListAdapter.clear();
    }

    public void refreshOrg(Organization org) {
        //refresh code
        boolean orgChanged = false;
        if(!org.equals(mOrg)) {
            orgChanged = true;
        }
        mOrg = org;



        //内存回收时能够再次拿到正常的 orgCode
        if(null != getArguments()) {
            getArguments().putParcelable(EmployeeTreeActivity.DATA_ORG, mOrg);
        }
        if (mEmployeeTreeListAdapter != null) {
            mEmployeeTreeListAdapter.refreshOrg(mOrg);
        }

        if(orgChanged) {
            initRootContactModelUI();
        }
    }

    public void refresh() {
        if (mActivity instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
            mSelectedContacts = userSelectActivity.getSelectedContactIdList();

        } else if (mActivity instanceof EmployeeTreeActivity) {
            mSelectedContacts = getSelectedContact();
        }
        if (null != mEmployeeTreeListAdapter) {
            mEmployeeTreeListAdapter.clear();
        }
        for (ContactModel contactModel : mRootContactModels) {
            contactModel.top = true;
            refreshContactModel(contactModel, true);
        }
    }



    public List<String> getSelectedContact() {
        List<String> userList = new ArrayList<>();
        for (ShowListItem contact : mSelectUserHead.getSelectedContact()) {
            userList.add(contact.getId());
        }
        return userList;
    }

    /**
     * 刷新organization与employee的勾选状态
     */
    public void refreshSelected() {
        List<ShowListItem> contactList = mSelectUserHead.getSelectedContact();
        for (OrganizationResult organization : mSelectedActionDeptContacts.keySet()) {
            selectOrganization(organization, false);
        }

        syncToMobileAction(contactList, false);
    }

    private void refreshContactModel(ContactModel contactModel, boolean isTop) {
        if (!contactModel.selected) {
            contactModel.selected = notAllowedSelectedContacts.contains(contactModel.getId()) || mSelectedContacts.contains(contactModel.getId());
        }

        if (contactModel instanceof EmployeeResult) {
            EmployeeResult employeeResult = (EmployeeResult) contactModel;


            if(!ListUtil.isEmpty(mCallbackContactsSelected)) {
                for(ShowListItem contact : mCallbackContactsSelected) {
                    if(contact.getId().equals(employeeResult.userId)) {
                        contactModel.selected = true;
                        break;
                    }

                }
            }


            //不过滤自己
            if (!mIsSuggestiveHideMe) {
                mEmployeeTreeListAdapter.add(contactModel);

            } else {
                if(!employeeResult.userId.equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                    mEmployeeTreeListAdapter.add(contactModel);

                }
            }


        } else {

            mEmployeeTreeListAdapter.add(contactModel);

        }





        if (contactModel.expand) {
            List<String> checkList = new ArrayList<>();
            for (ContactModel subContactModel : contactModel.subContactModel()) {
                subContactModel.top = isTop;
                refreshContactModel(subContactModel, false);
                if (subContactModel instanceof EmployeeResult) {
                    EmployeeResult employeeResult = (EmployeeResult)subContactModel;
                    if (!mCheckedList.contains(employeeResult.userId)) {
                        checkList.add(employeeResult.userId);
                    }
                }
            }
            mCheckedList.addAll(0, checkList);
            OnlineManager.getInstance().checkOnlineList(mActivity, checkList, onlineList -> {

                if (null != mEmployeeTreeListAdapter) {
                    mEmployeeTreeListAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void selectContact(ShowListItem contact) {
        for (ContactModel contactModel : mRootContactModels) {
            OrganizationResult organization = (OrganizationResult) contactModel;
            refreshSelected(organization, contact, true);
        }
        refresh();
    }


    @Override
    public void unSelectedContact(ShowListItem contact) {

        if(!ListUtil.isEmpty(mCallbackContactsSelected)) {
            mCallbackContactsSelected.remove(contact);
        }

        for (ContactModel contactModel : mRootContactModels) {
            OrganizationResult organization = (OrganizationResult) contactModel;
            refreshSelected(organization, contact, false);
        }
        refresh();
    }

    @Override
    public void selectContactList(List<? extends ShowListItem> contactList) {
        for (ContactModel contactModel : mRootContactModels) {
            OrganizationResult organization = (OrganizationResult) contactModel;
            refreshSelected(organization, contactList, true);
        }
        refresh();
    }

    @Override
    public void unSelectedContactList(List<? extends ShowListItem> contactList) {
        for (ContactModel contactModel : mRootContactModels) {
            OrganizationResult organization = (OrganizationResult) contactModel;
            refreshSelected(organization, contactList, false);
        }
        refresh();
    }

    private void refreshSelected(final OrganizationResult organization, final List<? extends ShowListItem> contactList, final boolean selected) {
        for (EmployeeResult employeeResult : organization.employeeResults) {
            for (ShowListItem contact : contactList) {
                if (employeeResult.userId.equals(contact.getId())) {
                    employeeResult.selected = selected;
                }
            }
        }

        for (OrganizationResult subOrganization : organization.children) {
            for (ShowListItem contact : contactList) {
                refreshSelected(subOrganization, contact, selected);
            }
        }
    }

    private void refreshSelected(final OrganizationResult organization, final ShowListItem contact, final boolean selected) {
        for (EmployeeResult employeeResult : organization.employeeResults) {
            if (contact.getId().equals(employeeResult.userId)) {
                employeeResult.selected = selected;
            }
        }

        for (OrganizationResult subOrganization : organization.children) {
            refreshSelected(subOrganization, contact, selected);
        }
    }

    /**
     * 部门选择行为
     *
     * @param organization
     */
    @Override
    public void select(final OrganizationResult organization, final QureyOrganizationViewRange range) {

        if(UserSelectActivity.SelectAction.SCOPE == mSelectAction) {
            if(!ContactInfoViewUtil.canEmployeeTreeOrgSelect(organization, mEmployeeTreeListAdapter.getSelectContacts())) {
                return;
            }


            organization.select();
            if(mActivity instanceof UserSelectActivity) {
                UserSelectActivity userselectActivity = (UserSelectActivity) mActivity;
                userselectActivity.action(organization);
            }

            return;
        }


        if (mSelectedActionDeptContacts.containsKey(organization)) {
            List<Employee> employeeList = mSelectedActionDeptContacts.get(organization);
            selectEmpList(organization, employeeList, !organization.isSelected(BaseApplicationLike.baseContext, mIsSuggestiveHideMe));
        } else {
            if (mActivity != null) {
                mProgressDialogHelper.show();
            }

            //如果是选人的模式下, 在拉取数据之前, 先从 organization 中的人数去判断是否超过阀值
            if (!organization.isSelected(BaseApplicationLike.baseContext, mIsSuggestiveHideMe)) {
                if (mActivity instanceof UserSelectActivity && isToThresholdInSelectMode(organization)) {
//                    ToastTipHelper.toastMaxTipInUserSelectActivity((UserSelectActivity) mActivity, mSelectAction);
                    toast(mUserSelectControlAction.getMaxTip());

                    mProgressDialogHelper.dismiss();

                    return;

                } else if (mActivity instanceof EmployeeTreeActivity && mSyncContactModel && isToThresholdInSyncNum(organization)) {
                    AtworkToast.showToast(getResources().getString(R.string.sync_max_alert));
                    mProgressDialogHelper.dismiss();

                    return;
                }
            }

            if (mActivity instanceof UserSelectActivity
                    && ((UserSelectActivity) mActivity).isOneSelectedMode()
                    && !organization.isSelected(BaseApplicationLike.baseContext, mIsSuggestiveHideMe)) {

                if (((UserSelectActivity) mActivity).isOneSelected() || 1 < organization.allEmployeeCount) {

                    mProgressDialogHelper.dismiss();
                    return;
                }
            }

            ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                    .setSelectMode(UserSelectActivity.SelectMode.NO_SELECT != mSelectMode)
                    .setMandatoryFilterSenior(mIsMandatoryFilterSenior);

            OrganizationManager.getInstance().queryRecursionEmployeeByOrgId(mActivity, mOrg.mOrgCode, organization.id, range, expandEmpTreeAction, new OrganizationAsyncNetService.OnQueryEmployeeListener() {

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    mProgressDialogHelper.dismiss();

                    if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                        AtworkToast.showResToast(R.string.network_not_good);
                    }
                }

                @Override
                public void onSuccess(List<Employee> employeeList) {
                    selectEmpList(organization, employeeList, !organization.isSelected(BaseApplicationLike.baseContext, mIsSuggestiveHideMe));
                    mSelectedActionDeptContacts.put(organization, employeeList);
                    mProgressDialogHelper.dismiss();
                }
            });

        }
    }


    void selectEmpList(final OrganizationResult organization, List<Employee> employeeList, boolean selected) {
        //filter self, if need
        filterMeIfNeed(employeeList);

        if (mActivity instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
            if (selected && !userSelectActivity.isAllowed(employeeList)) {
//                ToastTipHelper.toastMaxTipInUserSelectActivity(userSelectActivity, mSelectAction);
                toast(mUserSelectControlAction.getMaxTip());

                return;
            }
            selectOrganization(organization, selected);
            userSelectActivity.action(employeeList, selected);
        }

        //同步联系人模式下
        if (mActivity instanceof EmployeeTreeActivity && mSyncContactModel) {
            if (selected && !isAllowed(employeeList)) {
                AtworkToast.showToast(mActivity.getResources().getString(R.string.sync_max_alert));
                return;
            }
            selectOrganization(organization, selected);
            syncToMobileAction(employeeList, selected);
            updateSyncNum();
        }
    }

    private void filterMeIfNeed(List<Employee> employeeList) {
        if(mIsSuggestiveHideMe) {
            Employee removedEmp = null;
            for(Employee employee : employeeList) {
                if(User.isYou(BaseApplicationLike.baseContext, employee.userId)) {
                    removedEmp = employee;
                    break;
                }
            }

            if(null != removedEmp) {
                employeeList.remove(removedEmp);
            }
        }
    }

    /**
     * 选择模式下 群组, voip 会议 人数上限阀值
     */
    private boolean isToThresholdInSelectMode(OrganizationResult organization) {
        int countMax = mUserSelectControlAction.getMax();

        return -1 != countMax && countMax < organization.allEmployeeCount;
    }

    /**
     * 选择模式下同步号码 人数上线阀值
     * */
    private boolean isToThresholdInSyncNum(OrganizationResult organization) {
        return AtworkConfig.SYNC_NUMS_COUNT_MAX < organization.allEmployeeCount;
    }

    private void selectOrganization(OrganizationResult organization, boolean selected) {
        organization.selected = selected;
        for (OrganizationResult subOrganization : organization.children) {
            selectOrganization(subOrganization, selected);
        }
    }


    //========同步联系人到手机功能=================


    public boolean isAllowed() {
        return mSelectUserHead.getSelectedNum() + 1 <= MAX_USER;
    }

    public boolean isAllowed(List<Employee> employeeList) {
        boolean isAllow = true;

        int addNum = 0;
        for (Employee emp : employeeList) {
            if (!mSelectUserHead.contains(emp)) {
                addNum++;

                if (mSelectUserHead.getSelectedNum() + addNum > MAX_USER) {
                    isAllow = false;
                    break;
                }
            }
        }

        return isAllow;
    }

    public void toSyncContactModel() {
        this.mSyncContactModel = true;
        mEmployeeTreeListAdapter.setSelectedMode(true);

//        mSearchHeadView.setVisibility(View.GONE);
        mContactTreeView.removeHeaderView(mSearchHeadView);

        mTvTitle.setText(getResources().getText(R.string.sync_contact_to_mobile));
        //这里 add 的顺序不能调过来, 为了保证最后的 listener 能刷新listview
        selectedChangedListeners.add(mSelectUserHead);
        selectedChangedListeners.add(this);
        mSelectUserHead.setVisibility(View.VISIBLE);
        mTvSearchShowTitle.setVisibility(View.VISIBLE);
        mBtnSyncContactOk.setVisibility(View.VISIBLE);
        mMoreView.setVisibility(View.GONE);

        updateSyncNum();

        mSelectUserHead.setSearchModeAndOrgCodes(ListUtil.makeSingleList(mOrg.mOrgCode), ListUtil.makeSingleList(SelectContactHead.SearchMode.EMPLOYEE));

    }

    public void toCommonModel() {
        this.mSyncContactModel = false;
        mEmployeeTreeListAdapter.setSelectedMode(false);
        mTvTitle.setText(mActivity.getResources().getText(R.string.contact_tree_title));

//        mSearchHeadView.setVisibility(View.VISIBLE);
        mContactTreeView.addHeaderView(mSearchHeadView);


        selectedChangedListeners.clear();
        mSelectUserHead.setVisibility(View.GONE);
        mTvSearchShowTitle.setVisibility(View.GONE);
        mBtnSyncContactOk.setVisibility(View.GONE);
        if (DomainSettingsManager.getInstance().handleMobileContactSyncFeature() == DomainSettingsManager.SYNC_CONTACT_UNLIMIT) {
            mMoreView.setVisibility(View.VISIBLE);
        }

    }

    public Map<OrganizationResult, List<Employee>> getSelectedActionDeptContacts() {
        return mSelectedActionDeptContacts;
    }

    @Override
    public void syncToMobileAction(List<? extends ShowListItem> contactList, boolean selected) {
        for (SelectedChangedListener selectedChangedListener : selectedChangedListeners) {
            if (selected) {
                selectedChangedListener.selectContactList(contactList);
            } else {
                selectedChangedListener.unSelectedContactList(contactList);
            }
        }
    }

    @Override
    public void syncToMobileAction(ShowListItem contact) {
        for (SelectedChangedListener selectedChangedListener : selectedChangedListeners) {
            if (contact.isSelect()) {
                selectedChangedListener.selectContact(contact);
            } else {
                selectedChangedListener.unSelectedContact(contact);
            }
        }

        updateSyncNum();
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

    @Override
    public void onLoadMore(ContactModel model, QureyOrganizationViewRange range) {
        if (model.level - 1 == OrganizationAsyncNetService.TOP_LEVEL) {
            loadData(range);
            return;
        }
        if (model instanceof EmployeeResult) {
            EmployeeResult employeeResult = (EmployeeResult) model;
            if (employeeResult.parentModel != null) {
                loadMoreOrgContactModel(employeeResult.parentModel, true, range);
            }
        }

    }


    public static class UiHandler extends Handler {
        private WeakReference<EmployeeTreeFragment> mWeakRef;

        public UiHandler(EmployeeTreeFragment fragment) {
            mWeakRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final EmployeeTreeFragment fragment = mWeakRef.get();

            if (null != fragment) {
                switch (msg.what) {
                    case MSG_SYNCING_CONTACT_TO_MOBILE:
                        fragment.mAtworkAlertDialog.setProgress(msg.arg1 * 100 / msg.arg2);
                        break;

                    case MSG_FINISH_CONTACT_TO_MOBILE:
                        fragment.refreshSelected();

                        fragment.toCommonModel();
                        fragment.mAtworkAlertDialog.dismiss();

                        AtworkAlertDialog dialog = new AtworkAlertDialog(fragment.mActivity, AtworkAlertDialog.Type.SIMPLE);
                        dialog.setContent(fragment.getResources().getString(R.string.sync_finish, msg.arg1 + "", msg.arg2 + ""))
                                .setDeadBtnText(fragment.getResources().getString(R.string.ok));

                        if (0 == msg.arg2) {
                            dialog.hideDeadBtn();

                        } else {
                            dialog.setBrightBtnText(fragment.getResources().getString(R.string.sync_check_fail_record))
                                    .setClickBrightColorListener(dialog1 -> fragment.startActivity(SyncContactFailedActivity.getIntent(fragment.mActivity, fragment.mUserListFailed)));
                        }

                        dialog.show();

                        break;
                }

            }
        }

    }
}


