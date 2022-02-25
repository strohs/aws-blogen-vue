
import blogenImageUrl from '../assets/images/blogen-logo.jpg';

export default {
  API_SERVER_URL: import.meta.env.VITE_API_SERVER_URL,
  get DEFAULT_AVATAR_URL () { return this.API_SERVER_URL + '/avatars' },
  DEFAULT_IMAGE_PATH: blogenImageUrl,
  DEFAULT_AVATAR: 'avatar0.jpg'
}
