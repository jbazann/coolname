package com.awesome.filesys.json;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//TODO document
/**
 * 
 */
public final class JsonWrapper<T> {

    private final String id;
    private final Class<T> wrappedClass;
    private T wrapped;

    private final File target;
    private final ObjectMapper mapper;

    @JsonCreator
    public JsonWrapper(
        @JsonProperty String id,
        @JsonProperty T wrapped,
        @JsonProperty Class<T> wrappedClass,
        File target
        ) {
        this.id = id;
        this.wrapped = wrapped;
        this.wrappedClass = wrappedClass;
        this.target = target;
        this.mapper = new ObjectMapper();
    }

    public T read() throws JsonParseException, JsonMappingException, IOException {

        JsonNode node = mapper.missingNode(); 

        // check every tree in the file to find this element as root
        while( ! (node = mapper.readTree(target)).isMissingNode() ) { 
            if( node.has( "id" ) && node.get( "id" ).asText().equals( this.id ) )
            {
                return mapper.readValue(
                        node.get( "wrapped" ).toString(), 
                        this.wrappedClass);
            }
        }

        // entry not found, should write defaults here
        //TODO exceptions
        return this.wrapped;

    }

    public void write() throws JsonGenerationException, JsonMappingException, IOException {

        JsonNode node = mapper.missingNode(); 

        // find current entry if present
        while( ! (node = mapper.readTree(target)).isMissingNode() ){ 
            if( node.has("id") && node.get("id").asText().equals(this.id) )
                break;
        }

        // Create a new entry if needed, otherwise update it.
        if( node.isMissingNode() ) {
            node = mapper.valueToTree(this);
        }else{
            ((ObjectNode) node).set("wrapped", mapper.valueToTree(this.wrapped));            
        }

        // register updated entry
        mapper.writeTree(mapper.createGenerator(target, JsonEncoding.UTF8), node);

    }

    public T update(T newWrapped) throws JsonGenerationException, JsonMappingException, IOException {
        this.wrapped = newWrapped;
        this.write();
        return this.wrapped;
    }

    public String getId() {
        return id;
    }

    public T getWrapped() {
        return wrapped;
    }

    public Class<T> getWrappedClass() {
        return wrappedClass;
    }


}
