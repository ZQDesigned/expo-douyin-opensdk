import { registerWebModule, NativeModule } from "expo";

import {
  AuthResult,
  ExpoDouyinOpenSDKModuleEvents,
} from "./ExpoDouyinOpenSDK.types";

class ExpoDouyinOpenSDKModule extends NativeModule<ExpoDouyinOpenSDKModuleEvents> {
  initSDK(clientKey: string): boolean {
    throw new Error("Method not implemented.");
  }
  async authorize(acceptWhiteList: boolean): Promise<AuthResult> {
    throw new Error("Method not implemented.");
  }

  isDouyinInstalled(): boolean {
    throw new Error("Method not implemented.");
  }
}

export default registerWebModule(ExpoDouyinOpenSDKModule);
