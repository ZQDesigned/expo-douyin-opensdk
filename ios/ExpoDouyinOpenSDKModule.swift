import ExpoModulesCore
import DouyinOpenSDK
import os

public class ExpoDouyinOpenSDKModule: Module {
    
    let logger = Logger()
  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  public func definition() -> ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ExpoDouyinOpenSDK')` in JavaScript.
    Name("ExpoDouyinOpenSDK")

      Function("initSDK") { (appId: String) -> Bool in
          logger.info("Start to init Douyin OpenSDK with client key: \(appId)")
          return DouyinOpenSDKApplicationDelegate.sharedInstance().registerAppId(appId)
      }
      
      AsyncFunction("authorize") { (acceptWhiteList: Bool, promise: Promise) in
          let appId = DouyinOpenSDKApplicationDelegate.sharedInstance().appId()
          if appId == nil {
              logger.error("SDK not initialized!!")
              promise.reject(Exception(name: "unintialized", description: "SDK not initialized!!"))
          }
          let request: DouyinOpenSDKAuthRequest = DouyinOpenSDKAuthRequest()
          request.permissions = if (acceptWhiteList) {
              ["user_info", "trial.whitelist"]
          } else {
              ["user_info"]
          }
          let currentVC: UIViewController? = Utilities.getCurrentViewController()
          if currentVC != nil {
              request.send(currentVC!) { resp in
                  switch resp?.errCode {
                  case .success, .success20000:
                      let grantedPermissionsString = resp!.grantedPermissions?.array
                              .compactMap { $0 as? String }
                              .joined(separator: ",")
                      let authResult = AuthResult(
                        authType: AuthType.SUCCESS.toString(),
                        authCode: resp!.code ?? "",
                        grantedPermissions: grantedPermissionsString ?? "",
                        errorCode: 0,
                        errorMsg: resp?.errString ?? ""
                      )
                      promise.resolve(authResult.toDictionary())
                      
                  case .errorCodeUserCanceled:
                      let grantedPermissionsString = resp?.grantedPermissions?.array
                          .compactMap { $0 as? String }
                          .joined(separator: ",")
                      let authResult = AuthResult(
                        authType: AuthType.USER_CANCELED.toString(),
                          authCode: resp?.code ?? "",
                          grantedPermissions: grantedPermissionsString ?? "",
                          errorCode: -2,
                          errorMsg: resp?.errString ?? ""
                      )
                      promise.resolve(authResult.toDictionary())

                  default:
                      let grantedPermissionsString = resp?.grantedPermissions?.array
                          .compactMap { $0 as? String }
                          .joined(separator: ",")
                      let authResult = AuthResult(
                        authType: AuthType.FAILED.toString(),
                          authCode: resp?.code ?? "",
                          grantedPermissions: grantedPermissionsString ?? "",
                          errorCode: Utilities.getErrorCodeValue(errorCode: resp!.errCode.rawValue).intValue,
                          errorMsg: resp?.errString ?? ""
                      )
                      promise.resolve(authResult.toDictionary())
                  }
              }
          } else {
              promise.reject(Exception(name: "no_view_controller", description: "Could not find current UIViewController"))
          }
          
      }
      
      Function("isDouyinInstalled") { () -> Bool in
          return DouyinOpenSDKApplicationDelegate.sharedInstance().isAppInstalled()
      }

  }
}

class Utilities {
    // 获取当前显示的 ViewController
    static func getCurrentViewController() -> UIViewController? {
        guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
            return nil
        }
        return getTopViewController(from: rootViewController)
    }

    // 获取顶部 ViewController
    static func getTopViewController(from rootViewController: UIViewController) -> UIViewController {
        if let presentedViewController = rootViewController.presentedViewController {
            return getTopViewController(from: presentedViewController)
        } else if let navigationController = rootViewController as? UINavigationController {
            return getTopViewController(from: navigationController.topViewController ?? navigationController)
        } else if let tabBarController = rootViewController as? UITabBarController {
            return getTopViewController(from: tabBarController.selectedViewController ?? tabBarController)
        } else {
            return rootViewController
        }
    }

    // 将错误码转换为 NSNumber
    static func getErrorCodeValue(errorCode: Int) -> NSNumber {
        return NSNumber(value: errorCode)
    }
}


enum AuthType {
    /**
     * 授权成功
     */
    case SUCCESS
    /**
     * 用户取消授权
     */
    case USER_CANCELED
    /**
     * 授权失败
     */
    case FAILED
    
    public func toString() -> String {
        switch self {
        case .SUCCESS:
            return "SUCCESS"
        case .USER_CANCELED:
            return "USER_CANCELED"
        case .FAILED:
            return "FAILED"
        }
    }
}

class AuthResult {
    var authType: String = ""
    var authCode: String = ""
    var grantedPermissions: String = ""
    var errorCode: Int = 0
    var errorMsg: String = ""
    
    init(authType: String, authCode: String, grantedPermissions: String, errorCode: Int, errorMsg: String) {
        self.authType = authType
        self.authCode = authCode
        self.grantedPermissions = grantedPermissions
        self.errorCode = errorCode
        self.errorMsg = errorMsg
    }
    
    public func toDictionary() -> NSMutableDictionary {
        let dict = NSMutableDictionary()
        dict["authType"] = self.authType
        dict["authCode"] = self.authCode
        dict["grantedPermissions"] = self.grantedPermissions
        dict["errorCode"] = self.errorCode
        dict["errorMsg"] = self.errorMsg
        return dict
    }
}
