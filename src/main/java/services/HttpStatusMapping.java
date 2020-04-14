package services;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

public class HttpStatusMapping {
    private HttpStatus httpStatusResponse;

    public HttpStatus getHttpStatus(int responseCode){
        switch (responseCode)
        {
            case 200: httpStatusResponse = OK;
                break;
            case 300: httpStatusResponse = MULTIPLE_CHOICES;
                break;
            case 400: httpStatusResponse = BAD_REQUEST;
                break;
            case 500: httpStatusResponse = INTERNAL_SERVER_ERROR;
                break;
        }
        return httpStatusResponse;
    }
}
