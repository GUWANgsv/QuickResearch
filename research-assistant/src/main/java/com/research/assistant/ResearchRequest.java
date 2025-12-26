package com.research.assistant;

import lombok.Data;
//that accepts the request structure
@Data//from lombok,  generate getters and setters for all the fields in this class
public class ResearchRequest {
    //have all the fields that we want to accept from the request
    private String content;
    private String operation;
}

