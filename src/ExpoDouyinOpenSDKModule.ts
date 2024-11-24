import { NativeModule, requireNativeModule } from "expo";

import {
  AuthResult,
  ExpoDouyinOpenSDKModuleEvents,
} from "./ExpoDouyinOpenSDK.types";

declare class ExpoDouyinOpenSDKModule extends NativeModule<ExpoDouyinOpenSDKModuleEvents> {
  initSDK(clientKey: string): boolean;
  authorize(acceptWhiteList: boolean): Promise<AuthResult>;
  isDouyinInstalled(): boolean;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoDouyinOpenSDKModule>("ExpoDouyinOpenSDK");
