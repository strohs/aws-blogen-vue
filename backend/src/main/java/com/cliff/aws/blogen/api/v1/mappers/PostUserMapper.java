package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.PostUserDTO;
import com.cliff.aws.blogen.domain.Blogen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Map between {@link Blogen} user information and {@link PostUserDTO}
 *
 * @author Cliff
 */
@Mapper
public interface PostUserMapper {

    PostUserMapper INSTANCE = Mappers.getMapper( PostUserMapper.class );

    @Mapping( target = "id", source = "userId")
    @Mapping( target = "avatarUrl", expression = "java(com.cliff.aws.blogen.api.v1.services.AvatarService.buildAvatarUrl(user))")
    PostUserDTO userToPostUserDto( Blogen user );

    @Mapping( target = "userId", source = "id")
    @Mapping( target = "primaryHash", source = "id")
    @Mapping( target = "primaryRange", expression = "java(com.cliff.aws.blogen.domain.Blogen.RANGE_USER)")
    Blogen postUserDtoToUser( PostUserDTO postUserDTO );

}
