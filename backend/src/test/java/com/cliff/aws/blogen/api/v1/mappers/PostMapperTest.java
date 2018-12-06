package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.controllers.PostController;
import com.cliff.aws.blogen.api.v1.model.PostDTO;
import com.cliff.aws.blogen.api.v1.services.AvatarService;
import com.cliff.aws.blogen.api.v1.services.CategoryService;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

/**
 * Author: Cliff
 */
public class PostMapperTest {

    private PostMapper mapper = PostMapper.INSTANCE;
    private Blogen threadStart;
    private Blogen post1;

    @Before
    public void setUp() throws Exception {
        String threadId = "1111-AAAA";

        Instant now = Instant.now();
        String threadPrimaryRange = Blogen.buildPostRangeKey(now, threadId);
        String technology = "Technology";
        String text = "post text";
        String title = "post title";
        String imageUrl = "http://image/url";
        String userId = "1212-userid";
        String userName = "johndoe";
        String userId1 = "2222-user1ID";
        String userName1 = "mcgill";
        String avaterFileName = "avatar1.jpg";
        // create a thread
        BlogenPrimaryKey threadPK = BlogenPrimaryKey.builder().primaryHash(threadId).primaryRange(threadPrimaryRange).build();
        threadStart = Blogen.builder()
                .blogenPrimaryKey( threadPK )
                .threadId(threadId)
                .postId(threadId)
                .categoryName( technology )
                .text( text )
                .title( title )
                .imageUrl( imageUrl )
                .userName( userName )
                .userId( userId )
                .avatarFileName( avaterFileName )
                .updatedTimestamp(now)
                .build();
        // create a post and attach it to the thread
        String post1Id = "1111-AAAA-2222-BBBB";
        now.plus( 1, ChronoUnit.DAYS );
        String post1PrimaryRange = Blogen.buildPostRangeKey(now, post1Id);
        BlogenPrimaryKey post1PK = BlogenPrimaryKey.builder().primaryHash(threadId).primaryRange(post1PrimaryRange).build();
        post1 = Blogen.builder()
                .blogenPrimaryKey(post1PK)
                .threadId(threadId)
                .postId(post1Id)
                .categoryName( technology )
                .text( text )
                .title( title )
                .imageUrl( imageUrl )
                .userId( userId1 )
                .userName( userName1 )
                .updatedTimestamp(now)
                .build();
    }

    @Test
    public void thread_should_have_postId_equal_to_threadId_when_blogenToPostDto() {
        // given a blogen thread

        // when
        PostDTO dto = mapper.blogenToPostDto(threadStart);

        //then
        assertNotNull( dto );
        assertThat( dto.getPostId(), is( threadStart.getPostId()) );
        assertThat( dto.getThreadId(), is(threadStart.getThreadId()) );
    }

    @Test
    public void should_map_all_fields_of_a_post_when_blogenToPostDto() {
        // given a blogen thread
        String categoryUrl = CategoryService.buildCategoryUrl( threadStart );
        String avatarUrl = AvatarService.buildAvatarUrl( threadStart );
        String postUrl = mapper.buildPostUrl( threadStart );
        String compId = mapper.buildCompositePostId( threadStart );

        // when
        PostDTO dto = mapper.blogenToPostDto(threadStart);

        // then
        assertThat( dto.getId(), is(compId) );
        assertThat( dto.getText(), is(threadStart.getText()) );
        assertThat( dto.getTitle(), is(threadStart.getTitle()) );
        assertThat( dto.getImageUrl(), is(threadStart.getImageUrl()) );
        assertThat( dto.getCategory().getName(), is(threadStart.getCategoryName()) );
        assertThat( dto.getCategory().getCategoryUrl(), is(categoryUrl) );
        assertThat( dto.getUser().getUserName(), is(threadStart.getUserName() ));
        assertThat( dto.getUser().getId(), is(threadStart.getUserId()) );
        assertThat( dto.getUser().getAvatarUrl(), is(avatarUrl) );
        assertThat( dto.getPostUrl(), is(postUrl) );
        assertThat( dto.getChildren(), is( empty()) );
    }

    @Test
    public void should_map_thread_and_oneChildPost_when_blogenThreadToPostDTO() {
        List<Blogen> blogens = Arrays.asList( threadStart, post1 );
        String postUrl = mapper.buildPostUrl( post1 );
        String post1compId = mapper.buildCompositePostId( post1 );

        PostDTO dto = mapper.blogenThreadToPostDTO( blogens );

        assertNotNull( dto );
        assertThat( dto.getThreadId(), is( threadStart.getThreadId() ));
        assertThat( dto.getPostId(), is( threadStart.getThreadId()) );
        assertThat( dto.getChildren().size(), is(1) );
        assertThat( dto.getChildren().get(0).getId(), is(post1compId) );
        assertThat( dto.getChildren().get(0).getThreadId(), is(post1.getThreadId() ));
        assertThat( dto.getChildren().get(0).getPostId(), is(post1.getPostId()) );
        assertThat( dto.getChildren().get(0).getPostUrl(), is( postUrl) );
    }



    @Test
    public void buildPostUrl() {
        Instant now = Instant.now();
        String threadId = "1111-AAAA";
        String postId = "1111-AAAA-2222";
        String primaryRange = Blogen.buildPostRangeKey(now, postId);
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder().primaryHash(threadId).primaryRange(primaryRange).build();
        Blogen blogen = Blogen.builder().blogenPrimaryKey(pk).threadId(threadId).postId(postId).build();
        String compositeId = threadId + Blogen.ATT_SEP + "POST" + Blogen.ATT_SEP + now.toString() + Blogen.ATT_SEP + postId;
        String postUrl = PostController.BASE_URL + "/" + compositeId;

        String builtPostUrl = mapper.buildPostUrl(blogen);

        assertThat( builtPostUrl, is(postUrl) );
    }

    @Test
    public void buildCompositePostId() {
        Instant now = Instant.now();
        String threadId = "1111-AAAA";
        String postId = "1111-AAAA-2222";
        String compositeId = threadId + Blogen.ATT_SEP + "POST" + Blogen.ATT_SEP + now.toString() + Blogen.ATT_SEP + postId;
        String primaryRange = Blogen.buildPostRangeKey(now, postId);
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder().primaryHash(threadId).primaryRange(primaryRange).build();
        Blogen blogen = Blogen.builder().blogenPrimaryKey(pk).threadId(threadId).postId(postId).build();

        String builtId = mapper.buildCompositePostId( blogen );

        assertThat( builtId, is(compositeId) );
    }

    @Test
    public void parseCompositePostId() {
        Instant now = Instant.now();
        String threadId = "1111-AAAA";
        String postId = "1111-AAAA-2222";
        String compositeId = threadId + Blogen.ATT_SEP + "POST" + Blogen.ATT_SEP + now.toString() + Blogen.ATT_SEP + postId;
        String primaryRange = "POST" + Blogen.ATT_SEP + now.toString() + Blogen.ATT_SEP + postId;

        BlogenPrimaryKey pk = mapper.parseCompositePostId(compositeId);

        assertThat( pk.getPrimaryHash(), is(threadId) );
        assertThat( pk.getPrimaryRange(), is(primaryRange) );
    }
}