import CognitoIdentityServiceProvider from 'aws-sdk/clients/cognitoidentityserviceprovider'
import { Auth } from 'aws-amplify'
import jwtDecode from 'jwt-decode'

/**
 * map Blogen user attributes to cognito user pool attributes.
 * Only truthy values of the user object are mapped.
 * @param user - object containing attributes to map
 */
export function mapBlogenAttrsToCognitoAttrs (user) {
  let u = {}
  if (user.email) u['email'] = user.email
  if (user.firstName) u['given_name'] = user.firstName
  if (user.lastName) u['family_name'] = user.lastName
  if (user.userName) u['preferred_username'] = user.userName
  // preferredUsername will override userName if both are set in the user object
  if (user.preferredUsername) u['preferred_username'] = user.preferredUsername
  if (user.avatarImage) u['custom:avatar_file_name'] = user.avatarImage
  return u
}

/**
 * maps an amplify Auth.userInfo object into a blogen 'user' object
 * @param userInfo - a Amplify 'CognitoUser' object
 * @param idToken - the users current JWT string. This string is decoded to obtain the cognito groups they are
 * assigned to, which are then mapped to Blogen security roles
 * @returns {{firstName: string, lastName: string, password: string, avatarImage: string, roles: Array[string], id: string, userName: string, email: string}}
 */
export function mapCogitoUserInfoToBlogenUser (userInfo, idToken) {
  const jwtInfo = jwtDecode(idToken)
  const u = {
    id: userInfo.username,
    userName: userInfo.attributes.preferred_username,
    email: userInfo.attributes.email,
    firstName: userInfo.attributes.given_name,
    lastName: userInfo.attributes.family_name,
    avatarImage: userInfo.attributes['custom:avatar_file_name'],
    password: '',
    roles: jwtInfo['cognito:groups']
  }
  return u
}

/**
 * maps blogen signup data to the sign-up object expected by cognito
 * @returns {{username: *, password: *, attributes: {given_name: *, family_name: *, preferred_username: *, "custom:avatar_file_name": *}}}
 */
export function mapBlogenSignUpDataToCognito ({ email, password, firstName, lastName, preferredUsername, avatarName }) {
  return {
    username: email,
    password: password,
    attributes: {
      given_name: firstName,
      family_name: lastName,
      preferred_username: preferredUsername,
      'custom:avatar_file_name': avatarName
    }
  }
}

/**
 * returns true if the preferredUsername is NOT already taken by another user in the cognito user pool
 * @param prefUsername
 */
export function prefUsernameIsUnique (prefUsername) {
  return listUsersByAttribute('preferred_username', prefUsername)
    .then(user => {
      console.log('pref_username unique?:', user, user.Users.length === 0);
      return user.Users.length === 0
    })
    .catch(err => {
      console.log('error fetching preferred username from ListUsers', err);
      return false;
    })
}

/**
 * returns true if email is NOT in use by another user
 * @param email - email address to search for
 * @returns {*}
 */
export function emailIsUnique (email) {
  return listUsersByAttribute('email', email)
    .then(userInfo => {
      console.log('email unique?', email, userInfo.Users.length === 0);
      return userInfo.Users.length === 0
    })
    .catch(err => {
      console.log('error fetching email from ListUsers', err)
      return false
    })
}

/**
 * uses the aws javascript api to query our cognito user pool for any user
 * that has an attributeName equal to the attributeValue
 * @param attributeName - a cognito user pool attribute name, i.e. 'preferred_username'
 * @param attributeValue - the value that attributeName must equal
 * @returns {Promise<Object>} the returned promise will resolve to an object with the following shape
 * if a user is found:
 * {
 *     Users: [ { Username, Enabled, UserStatus, Attributes }, ]
 * }
 *
 * if no user is NOT found, an empty Users array will be returned:
 * {
 *     Users: []
 * }
 *
 */
function listUsersByAttribute (attributeName, attributeValue) {
  // will only work after user is authenticated
  // configure the CognitoIdentitiyServiceProvider using Amplify Auth credentials
  return Auth.currentCredentials()
    .then(credentials => {
      // console.log('essential credentials', Auth.essentialCredentials(credentials))
      const cisp = new CognitoIdentityServiceProvider({
        apiVersion: '2016-04-18',
        region: import.meta.env.VITE_AWS_REGION,
        credentials: Auth.essentialCredentials(credentials)
      });
      const params = {
        UserPoolId: import.meta.env.VITE_AWS_USER_POOL_ID,
        AttributesToGet: [attributeName],
        Filter: `${attributeName} = "${attributeValue}"`,
        Limit: 1
      };
      return new Promise((resolve, reject) => {
        cisp.listUsers(params, function (err, data) {
          if (err !== null) {
            reject(err);
          } else {
            console.log('listUsers data is:', data);
            resolve(data);
          }
        })
      })
    })
}
