export default {
  API_SERVER_URL: process.env.VUE_APP_API_SERVER_URL,
  get DEFAULT_AVATAR_URL () { return this.API_SERVER_URL + '/avatars' },
  DEFAULT_IMAGE_PATH: '/images/blogen-logo.jpg',
  DEFAULT_AVATAR: 'avatar0.jpg'
}
