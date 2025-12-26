package com.research.assistant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;


import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")//from this ,url inject in to the under geminiAPiUrl variable
    private String geminiAPiUrl;
    @Value("${gemini.api.key}")//from this key inject in to the under geminiApiKey variable
    private String geminiApiKey;

    private final WebClient webClient; //WebClient instance for making HTTP requests
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClientBuilder,ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper=objectMapper;
    }

    //method to process the research request
    public String processContent(ResearchRequest req) {
        //Build the prompt
        String prompt=buildPrompt(req);
        //Query the AI model API
              //This map will become the root JSON object
        Map<String, Object> requestBody=Map.of(
                //"Contents" is a JSON array
                //"Contents" is the key//The value is an array (Object[])
                "contents",new Object[]
                        //Each element inside "Contents" is an object
                        {
                                Map.of(
                                        //Inside the content object, you add "parts"//"parts" is also an array
                                        "parts",new Object[]
                                                {
                                                        Map.of("text",prompt)
                                                }        // /Contains a "text" field //key is text//prompt is a Java String variable holding the user input
                                )
                        }
        );
                        /*
                        {Equivalent JSON
                          "Contents": [
                            {
                              "parts": [
                                {
                                  "text": "your prompt value here"
                                }
                              ]
                            }
                          ]
                        }
                        */

        String response=webClient.post().uri(geminiAPiUrl+geminiApiKey).bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

        //Parse the response

        return extractTextFromResponse(response);
        //Return response
    }

    private String extractTextFromResponse(String response) {
        try {
    GeminiResponse geminiResponse=objectMapper.readValue(response, GeminiResponse.class);
    if(geminiResponse.getCandidates() != null&& !geminiResponse.getCandidates().isEmpty()){
        GeminiResponse.Candidate firstcandidate=geminiResponse.getCandidates().get(0);
        if(firstcandidate.getContent() != null&& firstcandidate.getContent().getParts() != null&& !firstcandidate.getContent().getParts().isEmpty()){
            return firstcandidate.getContent().getParts().get(0).getText();
        }
    }
    return "No content found in response.";
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }

    }

    private String buildPrompt(ResearchRequest req){
        StringBuilder prompt=new StringBuilder();
        switch(req.getOperation()){
            case "summarize":
                prompt.append("Provide a clear and concise summary of the following text in a few sentences :\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content:suggest related topics and further reading.Format the response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + req.getOperation());
        }
        prompt.append(req.getContent());
        return prompt.toString();
    }

}
