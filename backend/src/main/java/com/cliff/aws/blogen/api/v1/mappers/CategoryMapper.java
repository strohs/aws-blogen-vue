package com.cliff.aws.blogen.api.v1.mappers;

import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.domain.Blogen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Maps between {@link com.cliff.aws.blogen.domain.Blogen} category and {@link com.cliff.aws.blogen.api.v1.model.CategoryDTO}
 *
 * @author Cliff
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    @Mapping( target = "name", source = "categoryName" )
    @Mapping( target = "categoryUrl", expression = "java(com.cliff.aws.blogen.api.v1.services.CategoryService.buildCategoryUrl(blogen))")
    CategoryDTO blogenToCategoryDto( Blogen blogen );

    @Mapping( target = "primaryHash", source = "name" )
    @Mapping( target = "categoryName", source = "name" )
    @Mapping( target = "primaryRange", expression = "java(com.cliff.aws.blogen.domain.Blogen.RANGE_CATEGORY)" )
    Blogen categoryDtoToBlogen( CategoryDTO categoryDTO );

    @Mapping( target = "categoryName", source = "name" )
    void updateBlogenFromCategoryDTO( CategoryDTO requestDTO, @MappingTarget Blogen blogen );
}
