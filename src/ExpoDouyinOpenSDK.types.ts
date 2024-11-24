export type ExpoDouyinOpenSDKModuleEvents = {};

export interface AuthResult {
  authType: string;
  authCode: string;
  grantedPermissions: string;
  errorCode: number;
  errorMsg: string;
}
