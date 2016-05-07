package com.objectivetruth.uoitlibrarybooking.app.networking;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.ResourceLoadingUtilities;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

public class MockHttpStack implements HttpStack{
    private static final int SIMULATED_DELAY_MS = 500;
    private final Context context;

    public MockHttpStack(Context context) {
        this.context = context;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> stringStringMap)
            throws IOException, AuthFailureError {
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        HttpResponse response
                = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        response.setLocale(Locale.CANADA);
        String MY_RESERVATIONS_ASPX = "https://rooms.library.dc-uoit.ca/uoit_studyrooms/myreservations.aspx";
        request.getHeaders(); //Simulate getting the headers
        request.getBody(); // Simulate getting the request body

        if(request.getUrl().equalsIgnoreCase(MY_RESERVATIONS_ASPX)) {
            switch(request.getMethod()) {
                case Request.Method.GET:
                    response.setEntity(_getInitialReservationWebpageEntity());
                    break;
                case Request.Method.POST:
                    response.setEntity(_getSignInFailReservationWebpageEntity());
                    break;
            }
        }else{
            switch(request.getMethod()) {
                case Request.Method.GET:
                    response.setEntity(_getInitialWebpageEntity(request));
                    break;
                case Request.Method.POST:
                    response.setEntity(_getClickableDateEntity(request));
                    break;
            }
        }

        return response;
    }

    private HttpEntity _getSignInFailReservationWebpageEntity() throws UnsupportedEncodingException {
        String FAKE_SIGN_IN_FAILURE_RESPONSE_FILENAME = "wrong_username_password.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_SIGN_IN_FAILURE_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getInitialReservationWebpageEntity() throws UnsupportedEncodingException {
        String FAKE_INITIAL_MY_RESERVATIONS_RESPONSE_FILENAME = "initial_my_reservations.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_INITIAL_MY_RESERVATIONS_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getInitialWebpageEntity(Request request) throws UnsupportedEncodingException {
        String FAKE_1_CLICKABLE_DATE_RESPONSE_FILENAME = "1_day_available.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_1_CLICKABLE_DATE_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getClickableDateEntity(Request request) throws UnsupportedEncodingException {
        String FAKE_HALF_CLOSED_HALF_OPEN_RESPONSE_FILENAME = "half_closed_half_open_8am-330pm.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_HALF_CLOSED_HALF_OPEN_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }
}