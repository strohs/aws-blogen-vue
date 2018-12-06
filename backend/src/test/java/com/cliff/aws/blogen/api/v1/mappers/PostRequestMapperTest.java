package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.PostRequestDTO;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

/**
 * Author: Cliff
 */
public class PostRequestMapperTest {

    private PostRequestMapper mapper = PostRequestMapper.INSTANCE;


    @Test
    public void should_map_title_text_imageUrl_categoryName_when_postRequestDtoToPost() {
        // given
        String title = "Post title";
        String text = "text of the post";
        String imageUrl = "http://imgur.com/dfkweri2342424";
        String categoryName = "Health & Fitness";
        PostRequestDTO dto = PostRequestDTO.builder().title(title).text(text).categoryName(categoryName).imageUrl(imageUrl).build();

        // when
        Blogen post = mapper.postRequestDtoToPost(dto);

        // then
        assertNotNull(post);
        assertThat( post.getTitle(), is(title) );
        assertThat( post.getText(), is(text) );
        assertThat( post.getCategoryName(), is(categoryName) );
        assertThat( post.getImageUrl(), is(imageUrl) );
    }

    @Test
    public void should_update_title_text_imageUrl_categoryName_updatedTimestamp_when_updatePostFromPostRequestDTO() {
        // given
        String postId = "AAAA-1111";
        Instant somePastDate = LocalDateTime.of(2018,1,1,12,1,1).toInstant(ZoneOffset.UTC);
       String primaryRange = Blogen.buildPostRangeKey( somePastDate, postId );
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder().primaryHash(postId).primaryRange(primaryRange).build();
        Blogen existingPost = Blogen.builder().blogenPrimaryKey(pk).postId(postId).text("existing").title("title").imageUrl("http://blah").categoryName("Cat").build();

        String newTitle = "new title";
        String newText = "new text";
        String newImageUrl = "http://imgur/new/image";
        String newCategory = "Technology";
        PostRequestDTO dto = PostRequestDTO.builder().title(newTitle).text(newText).imageUrl(newImageUrl).categoryName(newCategory).build();

        // when
        mapper.updatePostFromPostRequestDTO(dto, existingPost);

        // then
        assertNotNull(existingPost);
        assertThat( existingPost.getText(), is(newText) );
        assertThat( existingPost.getImageUrl(), is(newImageUrl) );
        assertThat( existingPost.getTitle(), is(newTitle) );
        assertThat( existingPost.getCategoryName(), is(newCategory) );
        assertThat( existingPost.getUpdatedTimestamp(), isA(Instant.class) );
        assertThat( existingPost.getUpdatedTimestamp(), greaterThan( somePastDate) );
    }
}