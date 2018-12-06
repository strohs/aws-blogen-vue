package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.controllers.PostController;
import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.api.v1.model.PostDTO;
import com.cliff.aws.blogen.api.v1.model.PostUserDTO;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import com.cliff.aws.blogen.exceptions.BadRequestException;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * MapStruct mappers for mapping data between {@link com.cliff.aws.blogen.domain.Blogen} post
 * and {@link com.cliff.aws.blogen.api.v1.model.PostDTO}
 *
 * @author Cliff
 */
@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper( PostMapper.class );

    //todo need to fetch user domain object separately in order to get userId and userAvatar
    // map a single Blogen post, to a PostDTO
    default PostDTO blogenToPostDto( Blogen post ) {
        CategoryDTO categoryDTO = CategoryMapper.INSTANCE.blogenToCategoryDto( post );
        PostUserDTO postUserDTO = PostUserMapper.INSTANCE.userToPostUserDto( post );

        return PostDTO.builder()
                .id( buildCompositePostId( post ) )
                .threadId( post.getThreadId() )
                .postId( post.getPostId() )
                .category( categoryDTO )
                .user( postUserDTO )
                .created( post.getUpdatedTimestamp() )
                .imageUrl( post.getImageUrl() )
                .title( post.getTitle() )
                .text( post.getText() )
                .postUrl( buildPostUrl( post ) )
                .children( new ArrayList<>() )
                .build();
    }

    /**
     * maps a list of blogen posts to a list of PostDTO objects.
     * @param blogens a list of blogen objects containing post data. The post at index 0 must be the thread
     *                starting post, and the remaining elements must be sub-posts for that thread
     * @return a PostDTO object containing the thread starting post, and containing all the child posts of that thread
     * within PostDTO.children
     */
    default PostDTO blogenThreadToPostDTO( List<Blogen> blogens ) {
        PostDTO returnDTO = new PostDTO();
        List<PostDTO> children = new ArrayList<>();
        for ( int i = 0; i < blogens.size(); i++ ) {
            if ( i == 0 ) {
                // build the thread start dto
                returnDTO = blogenToPostDto( blogens.get( i ) );
            } else {
                // build a child post
                children.add( blogenToPostDto( blogens.get( i ) ) );
            }
        }
        if (children.size() > 0 ) returnDTO.setChildren( children );
        return returnDTO;
    }

//    default LocalDateTime instantToLocalDateTime( Instant instant ) {
//        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//    }
//
//    default Instant localDateTimeToInstant( LocalDateTime localDateTime ) {
//        return localDateTime.toInstant( ZoneOffset.UTC );
//    }

    default String buildPostUrl( Blogen post ) {
        return PostController.BASE_URL + "/" + buildCompositePostId( post );
    }


    /**
     * build a composite postId that will be used for clients requesting a specific post. This composite id is
     *  a concatenation of the threadID + the Blogen primaryRangeKey for a post. Each field of the composite key is
     * separated by Blogen.ATT_SEP character:  it will look like:
     *       THREAD_ID # 'POST' # CREATED_TIMESTAMP # POST_ID
     * @param post
     * @return
     */
    default String buildCompositePostId( Blogen post ) {
        return post.getPrimaryHash() +
                Blogen.ATT_SEP +
                post.getPrimaryRange();
    }

    /**
     *  parses the compositePostId and returns a BlogenPrimaryKey object with the object's fields set
     *
     * @param compositePostId
     * @return
     */
    default BlogenPrimaryKey parseCompositePostId( String compositePostId ) {
        String [] ids = compositePostId.split( Blogen.ATT_SEP );
        if (ids.length != 4) throw new BadRequestException( "invalid post id:" + compositePostId );
        String primaryRange = ids[1] + Blogen.ATT_SEP + ids[2] + Blogen.ATT_SEP + ids[3];
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder()
                .primaryHash( ids[0] )
                .primaryRange( primaryRange )
                .build();
        return pk;
    }
}
