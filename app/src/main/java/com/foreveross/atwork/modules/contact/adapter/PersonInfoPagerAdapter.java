package com.foreveross.atwork.modules.contact.adapter;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.viewPager.AdjustHeightViewPager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.modules.contact.component.PersonInfoPagerView;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;

/**
 * Created by reyzhang22 on 16/5/19.
 */
public class PersonInfoPagerAdapter extends PagerAdapter {

    private Fragment mFragment;
    private List<Employee> mEmpList;

    public PersonInfoPagerAdapter(Fragment fragment, List<Employee> empList) {
        mFragment = fragment;
        mEmpList = empList;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PersonInfoPagerView itemView = new PersonInfoPagerView(mFragment.getActivity(), mFragment);
        Employee employee = mEmpList.get(position);

        itemView.refreshDataSchema(employee, employee.dataSchemaList);
        itemView.setTag(AdjustHeightViewPager.TAG + position);
        SkinMaster.getInstance().changeTheme(itemView);

        container.addView(itemView);

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
        return mEmpList.get(position).orgInfo.orgName;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public View getTabView(Context context, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab = layoutInflater.inflate(R.layout.custom_tab_info, null);
        TextView tv = tab.findViewById(R.id.tv_custom);
        tv.setText(getPageTitle(position));
        return tab;
    }

}
