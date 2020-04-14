package services.walletService.controllers;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import static org.springframework.http.HttpStatus.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;
import services.helper.HttpStatusMapping;
import org.springframework.http.*;
import java.time.ZonedDateTime;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.slf4j.Logger;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private String updateWalletUrl = "http://wallet-service-01-test.dublin.local:3332/wallet/updateWallet?appToken={appToken}";
    private StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    private List<HttpMessageConverter<?>> converters = new ArrayList<>();
    private RestTemplate restTemplate = new RestTemplate();
    private List<Long> errorTimerList = new ArrayList<>();
    private HttpStatus httpStatusResponse = OK;
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
        logger.info("Get currentErrorCounter");

        return String.valueOf(errorCounter);
    }

    @GetMapping("/getErrorTimerList")
    public String getErrorTimerList(){
        logger.info("Get errorTimerList");

        return errorTimerList.toString();
    }

    @GetMapping("/resetErrorTimerList")
    public String resetErrorTimerList(){
        errorTimerList.clear();
        logger.info("Reset errorTimerList");

        return ("Reset errorTimerList: OK");
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
            errorTimerList.add(ZonedDateTime.now().toInstant().toEpochMilli());

            return new ResponseEntity("Status code: " + httpStatusResponse.value(), httpStatusResponse);
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