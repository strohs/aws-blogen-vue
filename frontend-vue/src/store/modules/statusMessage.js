import logger from "../../configs/logger";

const statusMessageModule = {
    state: {
        // application wide status message
        message: '',
        // error code of the message
        code: 0,
    },
    mutations: {
        SET_MESSAGE(state, { message, code= 0 }) {
            state.message = message;
            state.code = code;
        },
        CLEAR_MESSAGE(state) {
            state.message = '';
            state.code = 0;
        },
    },
};