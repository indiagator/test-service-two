package com.example.testservicetwo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class TestservicetwoApplication
{
    Logger logger = LoggerFactory.getLogger(TestservicetwoApplication.class);
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    Producer producer;


    public static void main(String[] args) {
        SpringApplication.run(TestservicetwoApplication.class, args);
    }

    @GetMapping("/")
    public ResponseEntity<String> greet()
    {
        logger.info("greeting was sent");
        return new ResponseEntity<>("hello from test service two", HttpStatus.OK);

    }

    @PostMapping("add")
    public Integer addTwoNos(@RequestBody NumberPair pair)
    {
        return pair.getN1()+ pair.getN2();
    }

    @GetMapping("percolate")
    public Mono<String> percolate()
    {
        logger.info("request forwarded to service 2");
        Mono<String> response3 =  webClientBuilder.build().get().uri("http://localhost:8072/test-service-3/").retrieve().bodyToMono(String.class);
        //return new ResponseEntity<>(response.subscribe().toString(),HttpStatus.OK);
        Mono<String> response5 =  webClientBuilder.build().get().uri("http://localhost:8072/test-service-5/").retrieve().bodyToMono(String.class);
        return response3;
    }

    @PostMapping("print/number")
    public String printJson(@RequestBody NumberPair numberPair) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        logger.info( mapper.writeValueAsString(numberPair));
        producer.sendMessage(mapper.writeValueAsString(numberPair));
        return "ALL_OK";
    }



}
