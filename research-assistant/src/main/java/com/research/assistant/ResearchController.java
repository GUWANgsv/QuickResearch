package com.research.assistant;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //indicates that this class is a REST controller
@RequestMapping("/api/research")//map all research-related endpoints
@CrossOrigin(origins = "*") //allow accessing all the endpoints in these controller from any frontend
@AllArgsConstructor//from lambok ,that will make all argument constructor with all the fields that you have defined in this class
public class ResearchController {
    private final ResearchService r; //this and AllArgsConstructor  auto wired by spring

    // 1 endpoint
    @PostMapping("/process") //(from @RequestMapping("/api/research")) map POST requests to /api/research/process to this method
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest req){//accept a ResearchRequest object from the request body
        String result=r.processContent(req);//call the service layer to process the request
        return ResponseEntity.ok(result);
        //result wrapped in ResponseEntity
        //result have the processed content from the AI model
    }


}
