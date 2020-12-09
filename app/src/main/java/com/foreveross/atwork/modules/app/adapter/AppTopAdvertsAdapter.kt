package com.foreveross.atwork.modules.app.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.viewPager.RecyclingPagerAdapter
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.advertisement.manager.AdvertisementManager
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions


class AppTopAdvertsAdapter(private var context: Context, private var advertisementConfigList: List<AdvertisementConfig>) : RecyclingPagerAdapter() {
    var viewHolder: ViewHolder? = null

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view = convertView
        if (null == view) {
            viewHolder = ViewHolder()

            view = LayoutInflater.from(context).inflate(R.layout.item_pager_ad_in_app_top, null)
            val ivImage: ImageView = view.findViewById(R.id.iv_img)

            viewHolder!!.imageView = ivImage

            view.tag = viewHolder

        } else {
            viewHolder = view.tag as ViewHolder
        }


        refreshImageView(position, viewHolder!!)

        return view!!

    }

    override fun refreshView(position: Int, view: View) {

        refreshImageView(position, view.tag as ViewHolder)
    }

    private fun refreshImageView(position: Int, viewHolder: ViewHolder) {
        val realPosition = getRealPosition(position)


        if (!ListUtil.isEmpty(advertisementConfigList)) {
            val advertisementConfig = advertisementConfigList[realPosition]
            displayBanner(viewHolder.imageView, advertisementConfig)

            setOnClickListener(viewHolder, advertisementConfig)


        }
    }

    private fun setOnClickListener(viewHolder: ViewHolder, advertisementConfig: AdvertisementConfig) {
        viewHolder.imageView?.apply {
            if (TextUtils.isEmpty(advertisementConfig.mLinkUrl)) {
                setOnClickListener(null)

            } else {
                setOnClickListener { v ->

                    val webViewControlAction = WebViewControlAction.newAction()
                            .setUrl(advertisementConfig.mLinkUrl)

                    val intent = WebViewActivity.getIntent(context, webViewControlAction)
                    context.startActivity(intent)

                    val advertisementEvent = advertisementConfig.toAdvertisementEvent(AdvertisementOpsType.Click)
                    AdvertisementManager.updateAppTopBannerStatistics(advertisementConfig.mMediaId, advertisementEvent)
                }


            }
        }
    }


    /**
     * get really position
     *
     * @param position
     * @return
     */
    fun getRealPosition(position: Int): Int {
        if (advertisementConfigList.isEmpty()) {
            return position
        }

        return position % advertisementConfigList.size
    }


    override fun getCount(): Int {
        if (1 != getRealCount()) {
            return getRealCount() * 1000
        }

        return getRealCount()
    }


    fun getRealCount(): Int = advertisementConfigList.size


    fun changeBannerHeight(height: Int) {
        if (viewHolder != null && viewHolder!!.imageView != null) {
            val params = viewHolder!!.imageView!!.layoutParams as RelativeLayout.LayoutParams
            params.height = DensityUtil.dip2px(height.toFloat())
            viewHolder!!.imageView!!.layoutParams = params
        }
    }

    companion object {

        fun displayBanner(imageView: ImageView?, advertisementConfig: AdvertisementConfig) {
            val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
            val localBannerPath = advertisementConfig.getLocalBannerPath(AtworkApplicationLike.baseContext, currentOrgCode)
            if (FileUtil.isExist(localBannerPath)) {
                ImageCacheHelper.displayImage(localBannerPath, imageView, getDisplayImageOptions())
                return
            }

            if (!StringUtils.isEmpty(advertisementConfig.mMediaId)) {
                ImageCacheHelper.displayImageByMediaId(advertisementConfig.mMediaId, imageView, getDisplayImageOptions())

            }

            if (-1 != advertisementConfig.mDefaultHolderImg) {
                imageView?.setImageResource(advertisementConfig.mDefaultHolderImg)
            }
        }


        fun getDisplayImageOptions(): DisplayImageOptions {
            val builder = DisplayImageOptions.Builder()
            builder.cacheOnDisk(true)
            builder.cacheInMemory(true)
            builder.showImageForEmptyUri(R.mipmap.loading_app_top_advert_bg)
            builder.showImageOnLoading(R.mipmap.loading_app_top_advert_bg)
            builder.showImageOnFail(R.mipmap.loading_app_top_advert_bg)
            return builder.build()
        }

    }
}

class ViewHolder {

    var imageView: ImageView? = null

}