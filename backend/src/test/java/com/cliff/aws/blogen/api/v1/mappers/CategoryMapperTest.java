package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.api.v1.services.CategoryService;
import com.cliff.aws.blogen.bootstrap.Bootstrapper;
import com.cliff.aws.blogen.domain.Blogen;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

/**
 * Author: Cliff
 */
public class CategoryMapperTest {

    private CategoryMapper mapper = CategoryMapper.INSTANCE;

    @Test
    public void should_map_CategoryName_and_CategoryUrl_into_CategoryDto() {
        //given
        Blogen techCategory = buildCategory("Technology");
        String catUrl = CategoryService.buildCategoryUrl( techCategory );

        // when
        CategoryDTO dto = mapper.blogenToCategoryDto(techCategory);

        // then
        assertNotNull( dto );
        assertThat( dto.getName(), is("Technology"));
        assertThat( dto.getCategoryUrl(), is(catUrl) );

    }


    @Test
    public void should_map_categoryName_to_Blogen_primaryHash_when_categoryDtoToBlogen() {
        //given
        CategoryDTO dto = CategoryDTO.builder().name("Technology").build();

        //when
        Blogen blogen = mapper.categoryDtoToBlogen( dto );

        assertNotNull( blogen );
        assertThat( blogen.getPrimaryHash(), is("Technology") );
    }

    @Test
    public void should_map_categoryName_to_Blogen_categoryName_when_categoryDtoToBlogen() {
        //given
        CategoryDTO dto = CategoryDTO.builder().name("Technology").build();

        //when
        Blogen blogen = mapper.categoryDtoToBlogen( dto );

        assertNotNull( blogen );
        assertThat( blogen.getCategoryName(), is("Technology") );
    }

    @Test
    public void should_set_Blogen_primaryRange_to_CATEGORY_when_mapping_categoryDtoToBlogen() {
        //given
        CategoryDTO dto = CategoryDTO.builder().name("Technology").build();

        //when
        Blogen blogen = mapper.categoryDtoToBlogen( dto );

        assertNotNull( blogen );
        assertThat( blogen.getPrimaryRange(), is(Blogen.RANGE_CATEGORY) );
    }


    private Blogen buildCategory(String name) {
        return Bootstrapper.buildCategory(name);
    }
}