import router from '../router/routes'

function handleAxiosError (error) {
  if (error.response) {
    console.log('Error Data:', error.response.data)
    console.log('Error Status', error.response.status)
    console.log('Error Headers', error.response.headers)
    if (error.response.status === 401 || error.response.status === 403) {
      router.push({ name: 'clogin' })
    }
  } else if (error.request) {
    console.log('Request made but no response received. request=', error.request)
    router.push({ name: 'clogin' })
  } else {
    console.log('Some other error occurred:', error.message)
  }
}

const getGlobalErrors = (errorObj) => {
  return errorObj
}

export { handleAxiosError, getGlobalErrors }
