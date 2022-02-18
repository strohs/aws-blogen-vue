package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.PostUserDTO;
import com.cliff.aws.blogen.api.v1.services.AvatarService;
import com.cliff.aws.blogen.bootstrap.DynamoDbBootstrapper;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

/**
 * unit tests for the PostUserMapper
 * Author: Cliff
 */
public class PostUserMapperTest {

    private PostUserMapper mapper = PostUserMapper.INSTANCE;


    @Test
    public void should_map_userId_userName_userUrl_avatarUrl_when_userToPostUserDto() {
        //given
        String userId = "1233-ABCD";
        String userName = "mcgill";
        String avatarFileName = "avatar1.jpg";
        BlogenPrimaryKey pk = DynamoDbBootstrapper.buildUserPK(userId);
        Blogen user = Blogen.builder().blogenPrimaryKey(pk).userId(userId).userName(userName).avatarFileName(avatarFileName).build();
        String avatarUrl = AvatarService.buildAvatarUrl(user);

        //when
        PostUserDTO dto = mapper.userToPostUserDto(user);

        //then
        assertNotNull(dto);
        assertThat( dto.getId(), is(userId) );
        assertThat( dto.getUserName(), is(userName) );
        assertThat( dto.getAvatarUrl(), is(avatarUrl) );
        assertThat( dto.getUserName(), is(userName) );
    }

    @Test
    public void should_map_primaryHash_primaryRange_userId_userName_when_postUserDtoToUser() {
        //given
        String userId = "1233-ABCD";
        String userName = "mcgill";
        PostUserDTO dto = PostUserDTO.builder().id(userId).userName(userName).build();

        //when
        Blogen user = mapper.postUserDtoToUser(dto);

        //then
        assertNotNull(user);
        assertThat( user.getPrimaryHash(), is(userId) );
        assertThat( user.getUserId(), is(userId) );
        assertThat( user.getUserName(), is(userName) );
        assertThat( user.getPrimaryRange(), is(Blogen.RANGE_USER) );
    }
}