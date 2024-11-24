// Reexport the native module. On web, it will be resolved to ExpoDouyinOpenSDKModule.web.ts
// and on native platforms to ExpoDouyinOpenSDKModule.ts
export { default as DouyinOpenSDK } from "./ExpoDouyinOpenSDKModule";
export * from "./ExpoDouyinOpenSDK.types";
