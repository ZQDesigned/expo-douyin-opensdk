# expo-douyin-opensdk

DouYin OpenSDK for Expo

## 安装

```sh
npm install expo-douyin-opensdk
```

## 使用前的配置

> 该依赖仅适用于使用 expo 的 React Native 项目，未在原版 React Native 项目中测试。

### Android

在 `android/build.gradle` 中添加如下配置：

```gradle
allprojects {
    repositories {
        maven { url 'https://artifact.bytedance.com/repository/AwemeOpenSDK' }
    }
}
```

该配置用于添加抖音 SDK 的 Maven 仓库地址，由于通过对模块添加该仓库在测试环境下会出现无法下载 SDK 的问题，所以需要手动在原生项目中集成该仓库。

在 `main/AndroidManifest.xml` 中添加如下配置：

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android">
  <queries>
    <!--允许查询抖音和抖音极速版的软件包信息-->
    <package android:name="com.ss.android.ugc.aweme" />
    <package android:name="com.ss.android.ugc.aweme.lite" />
    <package android:name="com.ss.android.ugc.live" />
  </queries>
</manifest>
```

该配置用于声明你的应用会使用到抖音 SDK，以便在 Android 11 以上的设备上可以正常使用 SDK。

同时，你需要注册该 SDK 的 Activity，添加如下配置：

```xml
<activity android:name="com.lnyynet.expomodule.dysdk.douyinapi.DouYinEntryActivity"
        android:launchMode="singleTask"
        android:taskAffinity="你的包名"
        android:exported="true" />
```

### iOS

在 `Info.plist` 中添加如下配置：

```plist
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>douyinopensdk</string>
    <string>douyinliteopensdk</string>
    <string>douyinsharesdk</string>
    <string>snssdk1128</string>
</array>
<key>CFBundleURLTypes</key>
<array>
<dict>
    <key>CFBundleTypeRole</key>
    <string>Editor</string>
    <key>CFBundleURLName</key>
    <string>douyin</string>
    <key>CFBundleURLSchemes</key>
    <array>
        <string>你的 Client Key</string>
    </array>
</dict>
</array>
```

该配置用于声明你的应用会使用到抖音 SDK，以便在 iOS 14 以上的设备上可以正常使用 SDK。

在 `AppDelegate.mm` 中添加如下配置：

```objc
#import <DouyinOpenSDK/DouyinOpenSDKApplicationDelegate.h>

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  self.moduleName = @"main";

  // You can add your custom initial props in the dictionary below.
  // They will be passed down to the ViewController used by React Native.
  self.initialProps = @{};  

  [[DouyinOpenSDKApplicationDelegate sharedInstance] application:application didFinishLaunchingWithOptions:launchOptions];
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

// Linking API
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
  if ([[DouyinOpenSDKApplicationDelegate sharedInstance] application:application openURL:url sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey] annotation:options[UIApplicationOpenURLOptionsAnnotationKey]]
          ) {
          return YES;
      }
  return [super application:application openURL:url options:options] || [RCTLinkingManager application:application openURL:url options:options];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{

    if ([[DouyinOpenSDKApplicationDelegate sharedInstance] application:application openURL:url sourceApplication:sourceApplication annotation:annotation]) {
        return YES;
    }

    return NO;
}

- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
{
    if ([[DouyinOpenSDKApplicationDelegate sharedInstance] application:application openURL:url sourceApplication:nil annotation:nil]) {
        return YES;
    }
    return NO;
}
```

## 使用文档

### 初始化 SDK

> Available on iOS and Android

```ts
initSDK(clientKey: string): boolean
```

初始化 SDK，需要传入你的 Client Key。

params:

- `clientKey` - 你的 Client Key

returns:

- `boolean` - 初始化是否成功

> 注意：该方法需要在调用其他方法之前调用。

### 发起授权登录请求

> Available on iOS and Android

```ts
authorize(acceptWhiteList: boolean): Promise<AuthResult>;
```

发起授权登录请求。

params:

- `acceptWhiteList` - 是否添加白名单，如果为 `true`，则会在授权登录时添加白名单，否则不添加。

returns:

- `Promise<AuthResult>` - 授权登录结果

> ```ts
> interface AuthResult {
>   authType: string;
>   authCode: string;
>   grantedPermissions: string;
>   errorCode: number;
>   errorMsg: string;
> }
> ```

### 判断是否安装抖音

> Available on iOS and Android

```ts
isDouyinInstalled(): boolean;
```

判断是否安装抖音。

returns:

- `boolean` - 是否安装抖音