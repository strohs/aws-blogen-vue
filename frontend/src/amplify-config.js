// configures the AWS amplify library to use these configurations when accessing
// a Cognito user pool and identity pool

const amplifyConf = {
  Auth: {
    // REQUIRED only for Federated Authentication - Amazon Cognito Identity Pool ID
    identityPoolId: process.env.VUE_APP_AWS_IDENTITY_POOL_ID,

    // REQUIRED - Amazon Cognito Region
    region: process.env.VUE_APP_AWS_REGION,

    // REQUIRED - Amazon Cognito User Pool ID
    userPoolId: process.env.VUE_APP_AWS_USER_POOL_ID,

    // REQUIRED - Amazon Cognito Web Client ID (26-char alphanumeric string)
    userPoolWebClientId: process.env.VUE_APP_AWS_USER_POOL_WEB_CLIENT_ID,

    // OPTIONAL - Enforce user authentication prior to accessing AWS resources or not
    mandatorySignIn: false,

    // // OPTIONAL - Configuration for cookie storage
    // cookieStorage: {
    //   // REQUIRED - Cookie domain (only required if cookieStorage is provided)
    //   domain: '.yourdomain.com',
    //   // OPTIONAL - Cookie path
    //   path: '/',
    //   // OPTIONAL - Cookie expiration in days
    //   expires: 365,
    //   // OPTIONAL - Cookie secure flag
    //   secure: true
    // },
    //
    // // OPTIONAL - customized storage object
    // storage: new MyStorage(),

    // OPTIONAL - Manually set the authentication flow type. Default is 'USER_SRP_AUTH'
    authenticationFlowType: 'USER_SRP_AUTH'
  }
}

export default amplifyConf
