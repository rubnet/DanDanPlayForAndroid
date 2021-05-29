package com.xyoye.stream_component.ui.activities.web_dav_image

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.xyoye.common_component.adapter.BaseAdapter
import com.xyoye.common_component.adapter.addItem
import com.xyoye.common_component.adapter.buildAdapter
import com.xyoye.common_component.adapter.initData
import com.xyoye.common_component.base.BaseActivity
import com.xyoye.common_component.config.RouteTable
import com.xyoye.common_component.utils.DDLog
import com.xyoye.common_component.weight.ToastCenter
import com.xyoye.data_component.bean.ImageParams
import com.xyoye.stream_component.BR
import com.xyoye.stream_component.R
import com.xyoye.stream_component.databinding.ActivityWebDavImageBinding
import com.xyoye.stream_component.databinding.ItemWebDavImageBinding

@Route(path = RouteTable.Stream.WebDavImage)
class WebDavImageActivity : BaseActivity<WebDavImageViewModel, ActivityWebDavImageBinding>() {

    private lateinit var imageAdapter: BaseAdapter<String>

    private val hideNextRunnable = Runnable {
        dataBinding.nextIv.isGone = true
    }
    private val hidePreviousRunnable = Runnable {
        dataBinding.previousIv.isGone = true
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun initViewModel() =
        ViewModelInit(
            BR.viewModel,
            WebDavImageViewModel::class.java
        )

    override fun getLayoutId() = R.layout.activity_web_dav_image

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .fullScreen(true)
            .hideBar(BarHide.FLAG_HIDE_BAR)
            .init()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        ARouter.getInstance().inject(this)

        val imageParams = ImageDataHolder.imageParams

        if (checkParams(imageParams).not()) {
            ToastCenter.showError("参数错误")
            finish()
            return
        }

        viewModel.setImageParams(imageParams!!)

        imageAdapter = buildAdapter {
            initData(viewModel.getImageList())

            addItem<String, ItemWebDavImageBinding>(R.layout.item_web_dav_image) {
                initView { data, _, _ ->
                    Glide.with(this@WebDavImageActivity)
                        .load(viewModel.getGlideUrl(data))
                        .transition((DrawableTransitionOptions.withCrossFade()))
                        .into(itemBinding.imageView)
                }
            }
        }

        dataBinding.imageRv.apply {
            layoutManager =
                LinearLayoutManager(this@WebDavImageActivity, LinearLayoutManager.VERTICAL, false)

            adapter = imageAdapter
        }

        viewModel.updateImageLiveData.observe(this) {
            if (it.isEmpty()) {
                ToastCenter.showWarning("无更多数据")
                return@observe
            }
            imageAdapter.setData(it)
        }

        dataBinding.imageRv.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                downX = event?.x ?: 0f
                return@setOnTouchListener false
            } else if (event.action == MotionEvent.ACTION_UP){
                val upX = event?.x ?: 0f
                DDLog.e("TestS", "dx:$downX, ux:$upX")
                if (downX - upX > 200) {
                    handler.removeCallbacks(hideNextRunnable)
                    handler.removeCallbacks(hidePreviousRunnable)
                    dataBinding.nextIv.isGone = false
                    dataBinding.previousIv.isGone = true
                    handler.postDelayed(hideNextRunnable, 3000)
                    return@setOnTouchListener true
                } else if (upX - downX > 200) {
                    handler.removeCallbacks(hideNextRunnable)
                    handler.removeCallbacks(hidePreviousRunnable)
                    dataBinding.nextIv.isGone = true
                    dataBinding.previousIv.isGone = false
                    handler.postDelayed(hidePreviousRunnable, 3000)
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }
            return@setOnTouchListener false
        }
    }

    private var downX: Float = 0f

    private fun checkParams(imageParams: ImageParams?): Boolean {
        imageParams ?: return false

        val dirPosition = imageParams.dirPosition
        if (dirPosition >= imageParams.urls.size)
            return false

        return true
    }
}