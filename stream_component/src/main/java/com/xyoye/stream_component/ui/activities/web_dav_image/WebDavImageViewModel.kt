package com.xyoye.stream_component.ui.activities.web_dav_image

import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.xyoye.common_component.base.BaseViewModel
import com.xyoye.data_component.bean.ImageParams


class WebDavImageViewModel : BaseViewModel() {

    private lateinit var imageParams: ImageParams
    private var currentDirPosition: Int = 0

    val updateImageLiveData = MutableLiveData<MutableList<String>>()

    fun setImageParams(params: ImageParams) {
        imageParams = params
        currentDirPosition = params.dirPosition
    }

    fun getImageList(): MutableList<String> {
        if (currentDirPosition < 0){
            return mutableListOf()
        }
        if (imageParams.urls.size <= currentDirPosition){
            return mutableListOf()
        }
        return imageParams.urls[currentDirPosition]
    }

    fun nextImageList() {
        currentDirPosition++
        updateImageLiveData.postValue(getImageList())
    }

    fun previousImageList() {
        currentDirPosition--
        updateImageLiveData.postValue(getImageList())
    }

    fun getGlideUrl(url: String): GlideUrl {
        return GlideUrl(url, LazyHeaders.Builder()
                .addHeader("Authorization", imageParams.credentials)
                .build()
        )
    }
}