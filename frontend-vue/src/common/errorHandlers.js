import router from "../router/routes";
import logger from "../configs/logger";

function handleAxiosError(error) {
  if (error.response) {
    logger.debug("Error Data:", error.response.data);
    logger.debug("Error Status", error.response.status);
    logger.debug("Error Headers", error.response.headers);
    if (error.response.status === 401 || error.response.status === 403) {
      router.push({
        name: "clogin",
        params: { code: parseInt(error.response.status) }
      });
    }
  } else if (error.request) {
    logger.debug(
      "Request made but no response received. request=",
      error.request
    );
    router.push({
      name: "clogin",
      params: { code: 500 },
    });
  } else {
    logger.debug("Some other error occurred:", error.message);
    router.push({
      name: "clogin",
      params: { code: 500 },
    });
  }
}

/**
 * maps an error code to a message string that can be displayed to end users
 * @param code
 * @returns {string}
 */
function mapCodeToMessage(code) {
  // code 0 to 400 are not errors
  if (code >= 0 && code < 400 ) {
    return '';
  } else if (code >= 401 && code <= 403) {
    // these are authentication or token expiration errors
    return "token has expired, please log back in";
  } else {
    // all other errrors, usually code 500 where some internal server error has occurred
    return "all servers are busy, please try your request later"
  }
}

export { handleAxiosError, mapCodeToMessage };
