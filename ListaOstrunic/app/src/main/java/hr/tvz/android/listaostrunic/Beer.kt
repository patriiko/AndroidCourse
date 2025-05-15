package hr.tvz.android.listaostrunic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Beer(
    val name: String,
    val style: String,
    val abv: Double,
    val imageResId: Int,
    val websiteUrl: String
) : Parcelable
