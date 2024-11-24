package com.lnyynet.expomodule.dysdk

import android.util.Log
import com.bytedance.sdk.open.aweme.authorize.model.Authorization
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory
import com.bytedance.sdk.open.douyin.DouYinOpenConfig
import com.lnyynet.expomodule.dysdk.common.AuthResult
import com.lnyynet.expomodule.dysdk.common.toWritableMap
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoDouyinOpenSDKModule : Module() {
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ExpoDouyinOpenSDK')` in JavaScript.
    Name("ExpoDouyinOpenSDK")

    // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
    Function("initSDK") { clientKey: String ->
      return@Function DouYinOpenApiFactory.init(DouYinOpenConfig(clientKey)).also {
        isInitialized = it
      }
    }

    // Defines a JavaScript function that always returns a Promise and whose native code
    // is by default dispatched on the different thread than the JavaScript runtime runs on.
    AsyncFunction("authorize") { acceptWhiteList: Boolean, promise: Promise ->
      // 判断是否已经初始化 SDK
        if (!isInitialized) {
            promise.reject("SDK_NOT_INITIALIZED", "SDK is not initialized, please call initSDK first", null)
            return@AsyncFunction
        }
      authorizePromise = promise
      // 调用 SDK 发起授权请求，这里会跳转到外部应用
      val douyinOpenApi = DouYinOpenApiFactory.create(appContext.currentActivity)

      val request = Authorization.Request()
      request.scope = if (acceptWhiteList) "user_info,trial.whitelist" else "user_info" // 用户授权时必选权限
      request.callerLocalEntry = "com.lnyynet.expomodule.dysdk.douyinapi.DouYinEntryActivity" // 第三方指定自定义的回调类 Activity
      // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用web页授权
      request.state = "ww" // 用于保持请求和回调的状态，授权请求后原样带回给第三方
      Log.d("ExpoDouyinOpenSDK", "pending authorize: $request")
      douyinOpenApi.authorize(request)
    }

    Function("isDouyinInstalled") {
      val douyinOpenApi = DouYinOpenApiFactory.create(appContext.currentActivity)
      return@Function douyinOpenApi.isAppInstalled
    }

  }

  companion object {
    private var isInitialized = false // 用于保存 SDK 初始化状态

    private var authorizePromise: Promise? = null // 用于保存 Promise 对象

    // 当授权结果返回时调用该方法
    fun onAuthResult(result: AuthResult) {
      Log.d("ExpoDouyinOpenSDK", "onAuthResult: $result")
      if (authorizePromise != null) {
        authorizePromise!!.resolve(result.toWritableMap()) // 返回授权结果
        authorizePromise = null // 清空 Promise 防止内存泄漏
      } else {
        // 如果没有保存 Promise 对象，说明授权请求没有发起，这里可以忽略
        // 打印一个警告日志，提示回调被意外触发
        Log.w("ExpoDouyinOpenSDK", "authorize promise is null, pls check if you have called authorize method")
      }
    }
  }
}
