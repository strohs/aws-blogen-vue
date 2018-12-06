package com.cliff.aws.blogen.api.v1.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing category data to be exposed to clients.
 *
 * @author Cliff
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    @ApiModelProperty( value = "Category Name", required = true, example = "Business")
    private String name;

    @ApiModelProperty( value = "URL of this Category", readOnly = true, example = "/api/v1/categories/Technology")
    private String categoryUrl;
}
