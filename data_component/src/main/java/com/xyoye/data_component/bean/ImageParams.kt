package com.xyoye.data_component.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by xyoye on 2021/5/23.
 */

@Parcelize
data class ImageParams(
    val credentials: String,
    val urls: MutableList<MutableList<String>>,
    val dirPosition: Int
): Parcelable
