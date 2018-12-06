package com.cliff.aws.blogen.api.v1.services;

import com.cliff.aws.blogen.domain.Blogen;

import java.util.List;
import java.util.Optional;

/**
 * Service for retrieving Avatar Image names
 *
 * @author Cliff
 */
public interface AvatarService {

    String DEFAULT_AVATAR = "avatar0.jpg";
    String AVATAR_DIR = "/avatars";

    /**
     * Gets a list of all the distinct avatar filenames
     *
     * @return a List of filenames: e.g. avatar1.jpg,avatar2.jpg...
     */
    List<String> getAllAvatarImageNames();

    /**
     * save a Avatar filename into the Blogen repository
     * @param fileName
     * @return
     */
    Blogen saveAvatarFileName( String fileName );

    /**
     * fetch a Blogen containing the avatar image and any associated details
     * @param imageFileName the avatar image to fetch
     * @return an optional containing a Blogen object if the image was found, otherwise Optional.Empty
     */
    Optional<Blogen> findAvatarByFileName( String imageFileName );

    /**
     * builds a relative URL to the User's avatar image
     * @param user
     * @return a relative URL to the User's avatar
     */
    static String buildAvatarUrl( Blogen user ) {
        return AVATAR_DIR + "/" + user.getAvatarFileName();
    }

}
