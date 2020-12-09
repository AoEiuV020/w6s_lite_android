package com.foreveross.atwork.modules.image.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.file.VideoItem
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.chat.component.MoviePlayerView
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions

class ItemVideoPreviewView : FrameLayout {

    private lateinit var vMoviePlayView: MoviePlayerView
    private lateinit var ivVideoPlay: ImageView
    private lateinit var ivVideoThumbnail: ImageView

    private var videoItem: VideoItem? = null

    constructor(context: Context) : super(context) {
        initView(context)
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
        registerListener()

    }


    private fun initView(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_video_preview, this)

        vMoviePlayView = view.findViewById(R.id.v_movie_play_view)
        ivVideoPlay = view.findViewById(R.id.iv_video_play)
        ivVideoThumbnail = view.findViewById(R.id.iv_video_thumbnail)
    }

    fun registerListener() {
        ivVideoThumbnail.setOnClickListener {
            play()
        }


        vMoviePlayView.setOnClickListener {
            resumeOrPlay()
        }

        ivVideoPlay.setOnClickListener {
            resumeOrPlay()
        }
    }


    fun preview(mediaItem: VideoItem) {
        videoItem = mediaItem

        ivVideoPlay.visibility = VISIBLE
        ivVideoThumbnail.visibility = VISIBLE
        vMoviePlayView.visibility = INVISIBLE

        displayLocalImg(ivVideoThumbnail, mediaItem.filePath)

    }


    private fun resumeOrPlay() {
        if(vMoviePlayView.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if(null == videoItem) {
            return
        }

        ivVideoPlay.visibility = GONE
        ivVideoThumbnail.visibility = GONE
        vMoviePlayView.visibility = VISIBLE

        vMoviePlayView.play(videoItem!!.filePath, object: MoviePlayerView.OnMoviePlayListener{
            override fun onPlayCompletion() {

            }

            override fun onPrepareListener(duration: Int) {
            }

        })
    }

    fun pause() {
        vMoviePlayView.pause()

        ivVideoPlay.visibility = VISIBLE
    }

    fun release() {
        ivVideoPlay.visibility = VISIBLE
        ivVideoThumbnail.visibility = VISIBLE
        vMoviePlayView.visibility = View.INVISIBLE

        vMoviePlayView.release()
    }

    private fun displayLocalImg(cellView: ImageView, imgPath: String) {
        val builder = DisplayImageOptions.Builder()
        builder.cacheInMemory(true)
        builder.cacheOnDisk(true)
        builder.considerExifParams(true)
        builder.bitmapConfig(Bitmap.Config.RGB_565)

        val options = builder.build()

        // 设置图片
        ImageCacheHelper.displayImage(imgPath, cellView, options, object : ImageCacheHelper.ImageLoadedListener {
            override fun onImageLoadedComplete(bitmap: Bitmap) {
                refreshPlayVideoViewSize(bitmap)
            }

            override fun onImageLoadedFail() {
                cellView.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.loading_cover_size))
            }
        })
    }

    private fun refreshPlayVideoViewSize(bitmap: Bitmap?) {
        if (null != bitmap) {
            val thumbLayout = ivVideoThumbnail.layoutParams
            thumbLayout.width = ScreenUtils.getScreenWidth(BaseApplicationLike.baseContext)
            thumbLayout.height = (thumbLayout.width * (1f * bitmap.height / bitmap.width)).toInt()
            ivVideoThumbnail.layoutParams = thumbLayout


            val movieLayout = vMoviePlayView.layoutParams
            movieLayout.width = thumbLayout.width
            movieLayout.height = thumbLayout.height
            vMoviePlayView.layoutParams = movieLayout


        }
    }

}
