package com.example.emergencyhandler

import android.content.Context
import android.widget.Toast
import cn.leancloud.AVException
import cn.leancloud.AVObject
import com.example.emergencyhandler.data.entity.Call
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Hints @Inject constructor(
    @ApplicationContext context: Context
) {
    // hints
    val inputHints = listOf(
        context.getString(R.string.info_add_real_name_hint),
        context.getString(R.string.info_add_sex_hint),
//            context.getString(R.string.info_relationship),
        context.getString(R.string.info_add_birth_hint),
        context.getString(R.string.info_add_phone_hint),
        context.getString(R.string.info_add_weight_hint),
        context.getString(R.string.info_add_blood_type_hint),
        context.getString(R.string.info_add_medical_conditions_hint),
        context.getString(R.string.info_add_medical_notes_hint),
        context.getString(R.string.info_add_allergy_hint),
        context.getString(R.string.info_add_medications_hint),
        context.getString(R.string.info_add_address_hint)
    )
}

fun convertAVObjectToCall(avObject: AVObject): Call {
    return Call(
        id = avObject.objectId,
        callerAccountId = avObject.getString("callerAccountId"),
        callerAccount = avObject.getString("callerAccount"),
        handler = avObject.getString("handler"),
        locationCoordinate = avObject.getString("locationCoordinate"),
        locationName = avObject.getString("locationName"),
        patientId = avObject.getString("patientId"),
        patientName = avObject.getString("patientName"),
        responseTime = avObject.getDate("responseTime"),
        status = avObject.getString("status"),
        createTime = avObject.createdAt
    )
}

fun getErrorMessage(e: Throwable): String {
    return when (val code = AVException(e.cause!!).code) {
        206 -> "似乎已退出登陆"
        210 -> "用户名和密码不匹配"
        211 -> "该用户尚未注册"
        219 -> "登录失败次数超过限制，请稍候再试，或尝试重制密码"
        603 -> "验证码无效"
        999 -> "网络断开"
        else -> "error: code $code"
    }
}

fun showMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}