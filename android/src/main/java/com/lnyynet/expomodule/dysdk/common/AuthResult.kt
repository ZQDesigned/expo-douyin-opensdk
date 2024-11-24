package com.lnyynet.expomodule.dysdk.common

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

data class AuthResult(
  val authType: AuthType,
  val authCode: String,
  val grantedPermissions: String,
  val errorCode: Int,
  val errorMsg: String
)

fun AuthResult.toWritableMap(): WritableMap {
  val map = Arguments.createMap()
  map.putString("authType", authType.toString())
  map.putString("authCode", authCode)
  map.putString("grantedPermissions", grantedPermissions)
  map.putInt("errorCode", errorCode)
  map.putString("errorMsg", errorMsg)
  return map
}
