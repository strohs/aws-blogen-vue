package com.cliff.aws.blogen.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;

/**
 * convert java.time.Instant to/from ISO8601 string representation
 *
 * Author: Cliff
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@DynamoDBTypeConverted(converter=InstantFormat.Converter.class)
public @interface InstantFormat {


    class Converter implements DynamoDBTypeConverter<String,Instant> {

        @Override
        public String convert( Instant instant ) {
            return instant.toString();
        }

        @Override
        public Instant unconvert( String inString ) {
            return Instant.parse( inString );
        }
    }
}
