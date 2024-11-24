package com.lnyynet.expomodule.dysdk.douyinapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bytedance.sdk.open.aweme.CommonConstants
import com.bytedance.sdk.open.aweme.authorize.model.Authorization
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler
import com.bytedance.sdk.open.aweme.common.model.BaseReq
import com.bytedance.sdk.open.aweme.common.model.BaseResp
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi
import com.lnyynet.expomodule.dysdk.ExpoDouyinOpenSDKModule.Companion.onAuthResult
import com.lnyynet.expomodule.dysdk.common.AuthResult
import com.lnyynet.expomodule.dysdk.common.AuthType


class DouYinEntryActivity : Activity(), IApiEventHandler {

  private lateinit var douYinOpenApi: DouYinOpenApi

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    douYinOpenApi = DouYinOpenApiFactory.create(this)
    douYinOpenApi.handleIntent(intent, this)
  }

  /**
   * 什么也不做，只是为了实现接口
   */
  override fun onReq(req: BaseReq?) {  }

  override fun onResp(resp: BaseResp?) {
    Log.d("DouYinEntryActivity", "onResp: $resp")
    // 授权成功可以获得authCode
    if (resp != null) {
      if (resp.type == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
        val response = resp as Authorization.Response
        when {
          response.isSuccess -> {
            onAuthResult(
              AuthResult(
                AuthType.SUCCESS,
                response.authCode,
                response.grantedPermissions,
                response.errorCode,
                ""
              )
            )
          }
          response.isCancel -> {
            // 授权取消
            onAuthResult(
              AuthResult(
                AuthType.USER_CANCELED,
                "",
                "",
                response.errorCode,
                ""
              )
            )
          }
          else -> {
            // 授权失败
            onAuthResult(
              AuthResult(
                AuthType.FAILED,
                "",
                "",
                response.errorCode,
                response.errorMsg
              )
            )
          }
        }
        finish()
      }
    }
  }

  override fun onErrorIntent(p0: Intent?) {
    // 错误数据
    Toast.makeText(this, "操作异常，请重试", Toast.LENGTH_LONG).show()
    finish()
  }

}
