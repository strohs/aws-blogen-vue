import axios from 'axios'

/**
 * example of creating a custom axios instance that we can configure and then import into our modules where needed
 *
 * NOTE Authorization header with Bearer token is set in Login component
 *
 * Axios docs at {@link https://github.com/axios/axios}
 * @type {AxiosInstance}
 */

const instance = axios.create({
  baseURL: process.env.VUE_APP_API_SERVER_URL,
  responseType: 'json'
  // headers: {
  // during development, these headers are now configured in the webpack dev server, proxy table config (in config/index.js)
  //   'Access-Control-Allow-Origin': 'http://localhost:SOMEPORTHERE'
  // }
})

// alter defaults after instance creation instance.defaults.headers.common['SOMETHING'] = 'something';

export default instance
