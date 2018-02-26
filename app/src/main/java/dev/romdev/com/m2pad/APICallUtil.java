package dev.romdev.com.m2pad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Kevin on 1/7/17.
 */

public class APICallUtil {

    private String baseUrl = null;

    private HttpURLConnection httpURLConnection = null;

    public APICallUtil(String base, String apiCall, String jsonString ){

        baseUrl = base + apiCall + "?requestJson=" + jsonString;
    }
    public String call() throws Exception{

        String retString = "";

        httpURLConnection = (HttpURLConnection) new URL(baseUrl).openConnection();

        httpURLConnection.setDoInput(true);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.connect();

        BufferedReader bufferedReader
                = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

        String line;

        while ((line = bufferedReader.readLine()) != null){

            retString += line;
        }

        httpURLConnection.disconnect();
        return retString;
    }

}
