import router from "../router/routes";
import logger from "../configs/logger";

function handleAxiosError(error) {
  if (error.response) {
    logger.debug("Error Data:", error.response.data);
    logger.debug("Error Status", error.response.status);
    logger.debug("Error Headers", error.response.headers);
    if (error.response.status === 401 || error.response.status === 403) {
      router.push({ name: "clogin" });
    }
  } else if (error.request) {
    logger.debug(
      "Request made but no response received. request=",
      error.request
    );
    router.push({ name: "clogin" });
  } else {
    logger.debug("Some other error occurred:", error.message);
  }
}

const getGlobalErrors = (errorObj) => {
  return errorObj;
};

export { handleAxiosError, getGlobalErrors };
