/**
 * A module for authentication and authorization of Blogen users.
 * This module contacts AWS Cognito to retrieve user details and to determine the authentication state
 * of a user: authenticated, not authenticated. Additionally, the user's ID token is stored in vuex as it used
 * to make REST API calls to the Blogen API servers
 */

import axios from '../../axios-auth'
// import CognitoIdentityServiceProvider from 'aws-sdk/clients/cognitoidentityserviceprovider'
import { Auth } from 'aws-amplify'
import { mapBlogenAttrsToCognitoAttrs, mapCogitoUserInfoToBlogenUser } from '../../common/awsCognito'

const userAuthenticationModule = {
  state: {
    ID_TOKEN: '',
    // user holds details of the currently logged in user
    user: {
      id: '',
      firstName: '',
      lastName: '',
      userName: '',
      email: '',
      password: '',
      avatarImage: '',
      roles: []
    }
  },
  getters: {
    getIdToken: state => {
      return state.ID_TOKEN
    },
    getAuthUserRoles: state => {
      return state.user.roles
    },
    getAuthUser: state => {
      return state.user
    },
    getAuthUserId: state => {
      return state.user.id
    },
    isAuthenticated: state => {
      return state.ID_TOKEN.length > 0
    },
    isAdmin: state => {
      return (!!state.user.roles.find(el => el.toLowerCase() === 'admin'))
    }
  },
  mutations: {
    'SET_ID_TOKEN' (state, token) {
      state.ID_TOKEN = token
    },
    'SET_USER' (state, userObj) {
      state.user = userObj
    },
    'LOGOUT' (state) {
      // very basic logout that simply clears the ID_TOKEN
      if (state.ID_TOKEN.length > 0) {
        state.ID_TOKEN = ''
      }
    },
    'RESET_USER' (state) {
      state.user = { id: '', firstName: '', lastName: '', userName: '', email: '', roles: [] }
    }
  },
  actions: {
    // checks if a user is still currently Authenticated with Cognito, if so, save their info into vuex
    authenticateUser: ({ commit, dispatch }) => {
      return Auth.currentAuthenticatedUser()
        .then(user => {
          console.log('user is currently authenticated', { user })
          dispatch('setIdToken')
          dispatch('setCurrentUserInfo')
          return true
        })
        .catch(err => {
          // cognito will return the string 'not authenticated'
          console.log('user is not authenticated:', err)
          return false
        })
    },
    doSignIn: ({ commit, getters }, { email, password }) => {
      console.log('attempting sign-in with:', email, password)
      return Auth.signIn(email, password)
        .then(userData => {
          // userData may contain a challenge, clients will have to check for this
          console.log('sign-in to cognito okay', userData)
          return userData
        })
        .catch(err => {
          console.log('sign-in to cognito err', err)
          throw err
        })
    },
    // sign up a new user, email address is used as a username (unique id is generated by Cognito)
    doSignUp ({ commit, getters }, { email, password, firstName, lastName, preferredUsername, avatarName }) {
      const suData = {
        username: email,
        password: password,
        attributes: {
          given_name: firstName,
          family_name: lastName,
          preferred_username: preferredUsername,
          'custom:avatar_file_name': 'avatar0.jpg'
        }
      }
      console.log('cognito sign-up with:', suData)
      return Auth.signUp(suData)
        .then(data => {
          console.log('cognito sign-up response:', data)
          return data
        })
        .catch(err => {
          console.log('cognito sign-up error:', err)
          return err
        })
    },
    // sends the confirmation code back to Cognito for a newly signed up user
    doConfirmSignUp ({ commit, getters }, { email, confCode }) {
      console.log('congito confirm sign-up:', email, confCode)
      return Auth.confirmSignUp(email, confCode)
        .then(data => {
          // returns 'SUCCESS' if correct code entered
          console.log('cognito confirm response:', data)
          return data
        })
        .catch(err => {
          console.log('cognitp confirm sign-up error:', err)
          return err
        })
    },
    doLogout: ({ commit }) => {
      commit('LOGOUT')
      commit('RESET_USER')
      // global sign-out
      Auth.signOut({ global: true })
        .then(data => console.log('cognito sign out:', data))
        .catch(err => console.log('error during cognito sign out:', err))
    },
    updateUserAttributes: ({ commit, getters }, userAttrs) => {
      return Auth.currentAuthenticatedUser()
        .then(authUser => {
          // build the new attributes object
          const newAttrs = mapBlogenAttrsToCognitoAttrs(userAttrs)
          console.log('new ATTRS:', newAttrs)
          return Auth.updateUserAttributes(authUser, newAttrs)
        })
        .then(updateRes => {
          // todo check the result strings Cognito returns:
          //   'SUCCESS' if update succeeded
          console.log('update attributes result:', updateRes)
          let user = getters.getAuthUser
          Object.assign(user, userAttrs)
          commit('SET_USER', user)
          return updateRes
        })
        .catch(err => {
          throw err
        })
    },
    // submit a request to cognito for a password reset, cognito will email or sms a reset code
    doForgotPassword: ({ commit, getters }, email) => {
      console.log('do forgot password for:', email)
      return Auth.forgotPassword(email)
        .then(data => {
          console.log('forgot password response', data)
          return data
        })
        .catch(err => {
          console.log('forgot password error:', err)
          throw err
        })
    },
    // submit the password reset code, and new password to Cognito
    doForgotPasswordSubmit: ({ commit, getters }, { email, confCode, newPassword }) => {
      console.log('do forgot password submit for:', email)
      return Auth.forgotPasswordSubmit(email, confCode, newPassword)
        .then(data => {
          // NOTE: at this time Cognito is returning undefined if the password reset is successful
          console.log('forgot password submit response', data)
          return data
        })
        .catch(err => {
          console.log('forgot password submit error:', err)
          throw err
        })
    },
    changeUserPassword: ({ commit, getters }, { curPassword, newPassword }) => {
      console.log('change password:', curPassword, newPassword)
      return Auth.currentAuthenticatedUser()
        .then(authUser => {
          return Auth.changePassword(authUser, curPassword, newPassword)
        })
        .then(res => {
          // todo check the result strings Cognito returns:
          //   'SUCCESS' if update succeeded
          console.log('change password result:', res)
          return res
        })
        .catch(err => {
          console.log('error from changeUserPassword', err)
          throw err
        })
    },
    setCurrentUserInfo: ({ commit, getters }) => {
      // user must be logged in (i.e. currently Authenticated by Cognito)
      return Auth.currentUserInfo()
        .then(userInfo => {
          const user = mapCogitoUserInfoToBlogenUser(userInfo, getters.getIdToken)
          commit('SET_USER', user)
          return userInfo
        })
        .catch(err => {
          console.log('error from currentUserInfo', err)
          throw err
        })
    },
    setIdToken: ({ commit }) => {
      return Auth.currentSession()
        .then(userSession => {
          commit('SET_ID_TOKEN', userSession.idToken.jwtToken)
          // set axios Authorization header to id token
          axios.defaults.headers.common['Authorization'] = 'Bearer ' + userSession.idToken.jwtToken
          return userSession
        })
        .catch(err => {
          console.log('error from currentSession', err)
          throw err
        })
    },
    doCognitoChallenge: ({ commit }, { cogUserData, userAttrs }) => {
      // cogUserData is a cognito user object required by Auth.completeNewPassword
      //  AWS Amplify returns it when Auth.signIn was originally called
      // userAttrs are user attributes captured by the ChallengeForm, requested by the Cognito challenge
      userAttrs.avatarImage = 'avatar0.jpg'
      const reqAttrs = mapBlogenAttrsToCognitoAttrs(userAttrs)
      console.log('doCognitoChallenge with required attributes:', reqAttrs)
      return Auth.completeNewPassword(cogUserData, userAttrs.password, reqAttrs)
        .then(data => {
          console.log('complete new password challenge:', data)
          return data
        })
        .catch(err => {
          console.log('newPasswordChallenge error', err)
          throw err
        })
    }
  }
}

export default userAuthenticationModule
