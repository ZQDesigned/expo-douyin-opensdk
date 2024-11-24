package com.lnyynet.expomodule.dysdk.common

enum class AuthType {
  /**
   * 授权成功
   */
  SUCCESS,

  /**
   * 用户取消
   */
  USER_CANCELED,

  /**
   * 授权失败
   */
  FAILED;

  override fun toString(): String {
    return when (this) {
      SUCCESS -> "SUCCESS"
      USER_CANCELED -> "USER_CANCELED"
      FAILED -> "FAILED"
    }
  }
}
