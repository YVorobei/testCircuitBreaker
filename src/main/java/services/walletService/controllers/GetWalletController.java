package services.walletService.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@RestController
@RequestMapping("/wallet")
public class GetWalletController {

    Logger logger = LoggerFactory.getLogger(GetWalletController.class);

    @GetMapping("/getWallet")
    public String getWalletRequest(@RequestParam(value = "uid") long uid,
                                   @RequestParam(value = "walletName") String walletName,
                                   @RequestParam(value = "appToken") String appToken,
                                   @RequestHeader HttpHeaders requestHeaders) {

        return getWalletResponse(uid, walletName, appToken, requestHeaders);
    }

    @PostMapping("/updateWallet")
    public String getUpdateWalletRequest(@RequestParam(value = "appToken") String appToken,
                                                       @RequestHeader HttpHeaders requestHeaders,
                                                       @RequestBody String requestBody){
        return getUpdateWalletResponse(appToken, requestHeaders, requestBody);
    }

    public String getUpdateWalletResponse(String appToken, HttpHeaders requestHeaders, String requestBody) {
        String updateWalletUrl = "http://wallet-service-01-test.dublin.local:3332/wallet/updateWallet?appToken={appToken}";
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity<String> response = restTemplate.exchange(updateWalletUrl, HttpMethod.POST, entity, String.class, appToken);
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Request Successful.");
            logger.info(response.getBody());

        } else {
            logger.info("Request Failed");
            logger.info(response.getBody());
        }
        logger.info(response.getHeaders().toString());
        logger.info(response.getStatusCode().toString());
        logger.info("StatusCodeValue: " + response.getStatusCodeValue());
        logger.info(response.getBody());
        logger.info(response.getClass().toString());
        return response.getBody();
    }

    public String getWalletResponse(long uid, String walletName, String appToken, HttpHeaders requestHeaders) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity headers = new HttpEntity(requestHeaders);
        String getWalletUrl = "http://wallet-service-01-test.dublin.local:3332/wallet/getWallet?uid={uid}&walletName={walletName}&appToken={appToken}";
        ResponseEntity<String> response = restTemplate.exchange(getWalletUrl, HttpMethod.GET, headers, String.class, uid, walletName, appToken);
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Request Successful.");
            logger.info(response.getBody());
        } else {
            logger.info("Request Failed" + response.getStatusCode());
        }
        return response.getBody();
    }
}