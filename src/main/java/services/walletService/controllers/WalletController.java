package services.walletService.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import services.helper.HttpStatusMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private HttpStatus httpStatusResponse = OK;
    private RestTemplate restTemplate = new RestTemplate();
    private List<HttpMessageConverter<?>> converters = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    private StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    private String updateWalletUrl = "http://wallet-service-01-test.dublin.local:3332/wallet/updateWallet?appToken={appToken}";
    private int responseTimeout;
    private int errorCounter;

    @PostMapping("/setResponseCodeWithTimeout")
    public String setResponseCodeWithTimeout(@RequestParam(value = "responseCode") int responseCode,
                                             @RequestParam(value = "timeout") int timeout,
                                             @RequestParam(value = "resetErrorCounter") boolean resetErrorCounter){
        httpStatusResponse = new HttpStatusMapping().getHttpStatus(responseCode);
        responseTimeout = timeout;
        errorCounter = (resetErrorCounter) ? 0 : errorCounter;
        String logInfo = "Set httpStatusResponse: " + httpStatusResponse + "; with timeout: " + timeout;
        logger.info(logInfo);
        return logInfo;
    }

    @GetMapping("/getCurrentErrorCounter")
    public String getCurrentErrorCounter(){
        return String.valueOf(errorCounter);
    }

    @PostMapping("/updateWallet")
    public ResponseEntity<String> updateWalletRequest(@RequestParam(value = "appToken") String appToken,
                                                      @RequestHeader HttpHeaders requestHeaders,
                                                      @RequestBody String requestBody) throws InterruptedException {
        return updateWalletResponse(appToken, requestHeaders, requestBody);
    }

    private ResponseEntity<String> updateWalletResponse(String appToken, HttpHeaders requestHeaders, String requestBody) throws InterruptedException {
         if(!httpStatusResponse.is2xxSuccessful()){
            errorCounter++;
            logger.info("Counter for error request: "+ errorCounter);
            return new ResponseEntity(httpStatusResponse);
        }
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, requestHeaders);
        setMessageConverters(restTemplate);
        ResponseEntity<String> response = restTemplate.exchange(updateWalletUrl, HttpMethod.POST, entity, String.class, appToken);

        logger.info(updateWalletUrl);
        logger.info(response.getStatusCode().toString());
        logger.info("Request body: " + requestBody);
        logger.info("Response body: "+ response.getBody());
        Thread.sleep(responseTimeout);

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    private void setMessageConverters(RestTemplate restTemplate){
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        restTemplate.setMessageConverters(converters);
    }
}