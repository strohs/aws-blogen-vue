package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.PostRequestDTO;
import com.cliff.aws.blogen.domain.Blogen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mappers for mapping data between {@link Blogen} post and {@link PostRequestDTO}
 *
 * @author Cliff
 */
//source properties that are null should not be mapped onto target properties
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PostRequestMapper {

    PostRequestMapper INSTANCE = Mappers.getMapper( PostRequestMapper.class );

    //PostRequestDTO postToPostRequestDto( Blogen post );

    Blogen postRequestDtoToPost( PostRequestDTO postDTO );

    @Mapping( target = "updatedTimestamp", expression = "java(java.time.Instant.now())")
    void updatePostFromPostRequestDTO( PostRequestDTO requestDTO, @MappingTarget Blogen post );




}
