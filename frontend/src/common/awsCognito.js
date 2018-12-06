import CognitoIdentityServiceProvider from 'aws-sdk/clients/cognitoidentityserviceprovider'
import { Auth } from 'aws-amplify'
import jwtDecode from 'jwt-decode'

/**
 * map Blogen user attributes to cognito attributes. Only truthy values of the user object are mapped
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
 * map blogen signup data to the sign-up object expected by cognito
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
 * returns true if the preferredUsername is already taken by another user is our user pool
 * @param prefUsername
 */
export function prefUsernameTaken (prefUsername) {
  return listUsersByAttribute('preferred_username', prefUsername)
    .then(user => {
      console.log('pref_username taken:', user)
      return user.Users.length > 0
    })
    .catch(err => {
      console.log('error fetching preferred username from ListUsers', err)
      return true
    })
}

/**
 * returns true if email is in use by another user
 * @param email - email address to search for
 * @returns {*}
 */
export function emailTaken (email) {
  return listUsersByAttribute('email', email)
    .then(userInfo => {
      console.log('email taken:', userInfo)
      return userInfo.Users.length > 0
    })
    .catch(err => {
      console.log('error fetching email from ListUsers', err)
      return true
    })
}

function listUsersByAttribute (attributeName, attributeValue) {
  // will only work after user is authenticated
  // configure the CognitoIdentitiyServiceProvider using Amplify Auth credentials
  return Auth.currentCredentials()
    .then(credentials => {
      // console.log('essential credentials', Auth.essentialCredentials(credentials))
      const cisp = new CognitoIdentityServiceProvider({
        apiVersion: '2016-04-18',
        region: process.env.VUE_APP_AWS_REGION,
        credentials: Auth.essentialCredentials(credentials)
      })
      const params = {
        UserPoolId: process.env.VUE_APP_AWS_USER_POOL_ID,
        AttributesToGet: [attributeName],
        Filter: `${attributeName} = "${attributeValue}"`,
        Limit: 1
      }
      return new Promise((resolve, reject) => {
        cisp.listUsers(params, function (err, data) {
          if (err !== null) reject(err)
          else resolve(data)
        })
      })
    })
}
