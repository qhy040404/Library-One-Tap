package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.core.content.edit
import androidx.core.text.toSpannable
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import rikka.material.app.DayNightDelegate
import java.util.*

object AppUtils {
    fun getNightMode(modeString: String): Int {
        return when (modeString) {
            "on" -> DayNightDelegate.MODE_NIGHT_YES
            "off" -> DayNightDelegate.MODE_NIGHT_NO
            else -> DayNightDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    var locale: Locale = Locale.getDefault()
        get() {
            val tag = GlobalValues.locale
            if (tag.isEmpty() || "SYSTEM" == tag) {
                return Locale.getDefault()
            }
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            field = value
            SPUtils.sp.edit { putString(Constants.PREF_LOCALE, value.toLanguageTag()) }
        }

    fun setTitle(ctx: Context): Spannable {
        val sb = SpannableStringBuilder(ctx.getString(R.string.app_name))
        return sb.toSpannable()
    }

    fun getThemeID(theme: String): Int {
        return when (theme) {
            "purple" -> R.style.Theme_Purple_NoActionBar
            "blue" -> R.style.Theme_Blue_NoActionBar
            "pink" -> R.style.Theme_Pink_NoActionBar
            "green" -> R.style.Theme_Green_NoActionBar
            "orange" -> R.style.Theme_Orange_NoActionBar
            "red" -> R.style.Theme_Red_NoActionBar
            "simple" -> R.style.Theme_Simple_NoActionBar
            else -> getThemeID(RandomDataUtils.randomTheme)
        }
    }
}