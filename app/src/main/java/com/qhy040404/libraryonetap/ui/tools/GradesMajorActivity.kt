package com.qhy040404.libraryonetap.ui.tools

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.tools.Grade
import com.qhy040404.libraryonetap.data.tools.Semester
import com.qhy040404.libraryonetap.recyclerview.SimplePageActivity
import com.qhy040404.libraryonetap.recyclerview.simplepage.Card
import com.qhy040404.libraryonetap.recyclerview.simplepage.Category
import com.qhy040404.libraryonetap.recyclerview.simplepage.Clickable
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.tools.GradesUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.delay
import org.json.JSONObject

class GradesMajorActivity : SimplePageActivity() {
    private var currentVisible = true
    private val semesters = mutableListOf<Semester>()

    override fun initializeViewPref() {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }
    }

    override fun initializeView() {
        LibraryOneTapApp.instance?.addActivity(this)

        findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.VISIBLE

        if (!GlobalValues.md3) {
            toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }
    }

    override suspend fun setData() {
        if (!AppUtils.hasNetwork()) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@GradesMajorActivity)
                    .setMessage(R.string.glb_net_disconnected)
                    .setTitle(R.string.grade_major_title)
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(true)
                    .create().also {
                        if (this@GradesMajorActivity.currentVisible) {
                            it.show()
                        }
                    }
            }
            return
        }

        var majorStuId = 0
        val loginSuccess = Requests.initEdu()

        if (!loginSuccess.first) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@GradesMajorActivity)
                    .setTitle(R.string.exams_title)
                    .setMessage(
                        when (GlobalValues.netPrompt) {
                            Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected
                            Constants.NET_ERROR -> R.string.glb_net_error
                            Constants.NET_TIMEOUT -> R.string.glb_net_timeout
                            else -> R.string.glb_fail_to_login_three_times
                        }
                    )
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create().also {
                        if (this@GradesMajorActivity.currentVisible) {
                            it.show()
                        }
                    }
            }
        } else {
            if (GlobalValues.majorStuId == 0) {
                val initUrl = Requests.get(URLManager.EDU_GRADE_INIT_URL, null, true)
                val initData = Requests.get(URLManager.EDU_GRADE_INIT_URL)
                GlobalValues.majorStuId = if (initUrl.contains("semester-index")) {
                    initUrl.substringAfter("/").toInt()
                } else {
                    val initList =
                        initData.split("onclick=\"myFunction(this)\" value=\"")
                    if (initList.size == 3) {
                        if (!GlobalValues.toastShowed) {
                            showToast(R.string.tlp_minor_detected.getString())
                            GlobalValues.toastShowed = true
                        }
                        val aStuId = initList[1].substringBefore("\"").toInt()
                        val bStuId = initList[2].substringBefore("\"").toInt()
                        GlobalValues.minorStuId = aStuId.coerceAtLeast(bStuId)
                        majorStuId = aStuId.coerceAtMost(bStuId)
                    }
                    majorStuId
                }
            }

            if (!loginSuccess.second) {
                delay(2000L)
            }
            val gradesData =
                Requests.get(URLManager.getEduGradeUrl(GlobalValues.majorStuId))

            val gradesJsonObject = JSONObject(gradesData)
            val semesterArray = gradesJsonObject.optJSONArray("semesters")!!
            val grades = gradesJsonObject.optJSONObject("semesterId2studentGrades")!!
            for (i in 0 until semesterArray.length()) {
                val semesterId = semesterArray.optJSONObject(i).optInt("id")
                semesters.add(
                    Semester(
                        semesterId,
                        semesterArray.optJSONObject(i).optString("name"),
                        grades.optJSONArray(semesterId.toString())!!.let { currentSemester ->
                            val count = currentSemester.length()
                            buildList {
                                for (j in 0 until count) {
                                    val currentCourse = currentSemester.optJSONObject(j)!!
                                    add(
                                        Grade(
                                            currentCourse.optJSONObject("course")!!
                                                .optString("nameZh"),
                                            currentCourse.optJSONObject("course")!!
                                                .optString("code"),
                                            currentCourse.optJSONObject("course")!!
                                                .optDouble("credits"),
                                            currentCourse.optString("gaGrade"),
                                            currentCourse.optDouble("gp"),
                                            currentCourse.optJSONObject("studyType")!!
                                                .optString("text")
                                        )
                                    )
                                }
                            }
                        }
                    )
                )
            }
        }
        semesters.sortByDescending { it.id }
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            if (semesters.isEmpty()) {
                add(
                    Card(
                        R.string.gr_empty.getString()
                    )
                )
            } else {
                add(
                    Card(
                        String.format(
                            R.string.gr_stat.getString(),
                            semesters.first().courses.sumOf { it.credit },
                            GradesUtils.calculateWeightedAverage(
                                buildList {
                                    semesters.forEach {
                                        addAll(it.courses)
                                    }
                                }
                            ),
                            GradesUtils.calculateAverageGP(
                                this@GradesMajorActivity,
                                buildList {
                                    semesters.forEach {
                                        addAll(it.courses)
                                    }
                                }
                            )
                        )
                    ))
            }
            semesters.forEach { semester ->
                add(Category(semester.name))
                if (semester.courses.isEmpty()) {
                    add(Card(R.string.gr_eval_first.getString()))
                    return@forEach
                }
                semester.courses.forEach {
                    val head = "${it.name} : ${it.type}"
                    val desc = String.format(
                        R.string.gr_template.getString(),
                        it.code,
                        it.grade,
                        it.credit,
                        it.gp
                    )
                    add(
                        Clickable(
                            head,
                            desc
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        currentVisible = true
    }

    override fun onPause() {
        super.onPause()
        currentVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        LibraryOneTapApp.instance?.removeActivity(this)
    }
}
