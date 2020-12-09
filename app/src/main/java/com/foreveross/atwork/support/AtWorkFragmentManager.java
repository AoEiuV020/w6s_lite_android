package com.foreveross.atwork.support;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.util.Stack;

/**
 * Created by ReyZhang on 2015/4/22.
 */
public class AtWorkFragmentManager {

    private static final String TAG = AtWorkFragmentManager.class.getSimpleName();
    private static final String ACTION_REPLACE = "replace";
    private static final String ACTION_ADD = "add";

    private AtWorkFragmentManager sAtWorkFragmentManager;
    private FragmentActivity mFragmentActivity;
    private Stack<String> mFragmentTagStack;
    private int mContainId;

    /**
     * Manager 初始化
     * @param fragmentActivity
     * @param containId
     */
    public AtWorkFragmentManager (FragmentActivity fragmentActivity, int containId) {
        if (fragmentActivity == null) {
            throw new IllegalArgumentException("invalid argument on " + TAG);
        }
        mFragmentActivity = fragmentActivity;
        mContainId = containId;
        mFragmentTagStack = new Stack<String>();
    }

    /**
     * 将Fragment添加到栈中
     * @param fragment
     * @param tag
     */
    public void addFragmentAndAdd2BackStack(Fragment fragment, String tag) {
        toFragmentAndAdd2BackStack(fragment, ACTION_ADD, tag);
    }

    /**
     * 取代栈中的fragment
     * @param fragment
     * @param tag
     */
    public void replaceFragmentAndAdd2BackStack(Fragment fragment, String tag) {
        toFragmentAndAdd2BackStack(fragment, ACTION_REPLACE, tag);
    }

    public void showFragment(Fragment showFragment, String tag) {
        FragmentTransaction ft = getFragmentTransaction();
        FragmentManager fragmentManager = mFragmentActivity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if(null != fragment) {
            ft.show(fragment);
            hideFragment(fragmentManager, fragment, ft);
        } else {
            ft.add(mContainId, showFragment, tag);
            hideFragment(fragmentManager, showFragment, ft);
        }

        ft.commit();
        fragmentManager.executePendingTransactions();
    }

    private void hideFragment(FragmentManager fragmentManager, Fragment hidedFragment, FragmentTransaction ft) {
        for(Fragment fragmentLoop : fragmentManager.getFragments()) {
            if (fragmentLoop == null) {
                continue;
            }
            if(hidedFragment != fragmentLoop) {
                ft.hide(fragmentLoop);
            }
        }
    }

    /**
     *
     * @param fragment
     * @param action
     * @param tag
     */
    private void toFragmentAndAdd2BackStack(Fragment fragment, String action, String tag) {
        FragmentTransaction ft = getFragmentTransaction();
        if (action.equalsIgnoreCase(ACTION_ADD)) {
            ft.add(mContainId, fragment, tag);
        } else {
            ft.replace(mContainId, fragment, tag);
//            mFragmentActivity.getSupportFragmentManager().executePendingTransactions();
//            return;
        }
        ft.addToBackStack(tag);
        checkTagStack(tag);
        mFragmentTagStack.push(tag);
        ft.commit();
        mFragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * 获取到fragment栈的总数
     * @return
     */
    public int getBackStackCount(){
        return mFragmentTagStack.size()-1;
    }

    /**
     * 通过tag检查栈
     * @param fragmentTag
     */
    private void checkTagStack(String fragmentTag){
        while (mFragmentTagStack.contains(fragmentTag) && mFragmentTagStack.size()>0) {
            if(mFragmentTagStack.lastElement().equalsIgnoreCase(fragmentTag)){
                popSingleBackStack();
                mFragmentTagStack.pop();
                break;
            }
            popSingleBackStack();
            mFragmentTagStack.pop();
        }
    }

    /**
     * fragment弹出栈
     * @return
     */
    public boolean popBackStack() {
        if (mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount() > 1) {
            popSingleBackStack();
            mFragmentTagStack.pop();
            return true;
        } else {
            return false;
        }

    }

    /**
     * 清除所有栈
     */
    public void clearStack() {
        int backStackCount = mFragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            popSingleBackStack();
        }
        mFragmentTagStack.clear();
    }

    public Fragment getCurrentFragment() {
        LogUtil.e(TAG, "--------------" + mFragmentTagStack.size());
        return  mFragmentActivity.getSupportFragmentManager().findFragmentByTag(mFragmentTagStack.lastElement());
    }

    /**
     * 推入栈
     * @param tag
     */
    public void pushToTagStack(String tag) {
        mFragmentTagStack.push(tag);
    }


    private FragmentTransaction getFragmentTransaction() {
        return mFragmentActivity.getSupportFragmentManager().beginTransaction();
    }

    private void popSingleBackStack() {
        mFragmentActivity.getSupportFragmentManager().popBackStack();
    }

}

