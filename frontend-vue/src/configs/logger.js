// A basic logging utility that logs to the console, AND that won't log debug messages if we are in production environment

const isProduction = import.meta.env.PROD;

const logger = {
    debug: (...rest) => {
        if (!isProduction) {
            console.debug(...rest);
        }
    },

    info: (...rest) => {
        if (!isProduction) {
            console.info(...rest);
        }
    },

    warn: (...rest) => {
        if (!isProduction) {
            console.warn(...rest);
        }
    },

    error: (...rest) => {
        console.error(...rest);
    },
}

export default logger;