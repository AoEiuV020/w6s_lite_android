package com.foreveross.atwork.modules.aboutme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.MyAccountEmployeePagerView;
import com.foreveross.atwork.component.viewPager.AdjustHeightViewPager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.modules.aboutme.fragment.AvatarPopupFragment;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;

/**
 * Created by shadow on 2016/4/19.
 */
public class MyAccountPagerAdapter extends PagerAdapter {

    private Context mContext;
    private AvatarPopupFragment.OnPhotographPathListener mListener;
    private List<Employee> mEmpList;
    private User mUser;

    public MyAccountPagerAdapter(AvatarPopupFragment.OnPhotographPathListener listener, @Nullable User user, List<Employee> employeeList) {
        this.mListener = listener;
        this.mContext = listener.getCurrentFragment().getActivity();
        this.mUser = user;
        this.mEmpList = employeeList;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Employee employee = mEmpList.get(position);
        MyAccountEmployeePagerView itemView = new MyAccountEmployeePagerView(mContext);
        itemView.refreshDataSchema(mUser, employee, mListener);
        itemView.setTag(AdjustHeightViewPager.TAG + position);
        container.addView(itemView);
        SkinMaster.getInstance().changeTheme(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mEmpList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < mEmpList.size() && null != mEmpList.get(position).orgInfo) {
            return mEmpList.get(position).orgInfo.orgName;

        }

        return "";
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public View getTabView(Context context, int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.custom_tab_info, null);
        TextView tv = tab.findViewById(R.id.tv_custom);
        tv.setText(getPageTitle(position));
        return tab;
    }
}
