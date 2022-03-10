import { Hub } from 'aws-amplify';
import logger from "./logger";
import {store} from "../store/store";

/**
 * Use Amplify's hub module to get notified of Auth events.
 * Specically, we want to be notified of refresh token events, so we can set
 * the latest tokens in the vuex store
 *
 */

const listener = async (data) => {
    switch (data.payload.event) {
        case 'signIn':
            logger.info('user signed in');
            break;
        case 'signUp':
            logger.info('user signed up');
            break;
        case 'signOut':
            logger.info('user signed out');
            break;
        case 'signIn_failure':
            logger.error('user sign in failed');
            break;
        case 'tokenRefresh':
            logger.info('token refresh succeeded');
            await store.dispatch("authenticateUser");
            break;
        case 'tokenRefresh_failure':
            logger.error('token refresh failed');
            break;
        case 'configured':
            logger.info('the Auth module is configured');
    }
}

Hub.listen('auth', listener);
logger.debug('Hub is listening');