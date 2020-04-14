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

    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    private HttpStatus httpStatusResponse = OK;
    private int responseTimeout;

    @PostMapping("/setResponseCodeWithTimeout")
    public String setResponseCodeWithTimeout(@RequestParam(value = "responseCode") int responseCode, @RequestParam(value = "timeout") int timeout){
        httpStatusResponse = new HttpStatusMapping().getHttpStatus(responseCode);
        responseTimeout = timeout;
        String infoLog = "Set httpStatusResponse: " + httpStatusResponse + "; with timeout: " + timeout;
        return infoLog;
    }

    @PostMapping("/updateWallet")
    public ResponseEntity<String> updateWalletRequest(@RequestParam(value = "appToken") String appToken,
                                                      @RequestHeader HttpHeaders requestHeaders,
                                                      @RequestBody String requestBody) throws InterruptedException {
        return updateWalletResponse(appToken, requestHeaders, requestBody);
    }

    public ResponseEntity<String> updateWalletResponse(String appToken, HttpHeaders requestHeaders, String requestBody) throws InterruptedException {
        String updateWalletUrl = "http://wallet-service-01-test.dublin.local:3332/wallet/updateWallet?appToken={appToken}";
        if(!httpStatusResponse.is2xxSuccessful()){
            return new ResponseEntity(httpStatusResponse);
        }
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();

        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        restTemplate.setMessageConverters(converters);

        ResponseEntity<String> response = restTemplate.exchange(updateWalletUrl, HttpMethod.POST, entity, String.class, appToken);
        logger.info(updateWalletUrl);
        logger.info(response.getStatusCode().toString());
        logger.info("Request body: " + requestBody);
        logger.info("Response body: "+ response.getBody());
        Thread.sleep(responseTimeout);
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }
}