package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;

public class WeatherApiUtil {

    public static String composeServiceEndpoint(String inUrl, String inCityName, String inCountryName)  {

        final String endpointUrl = inUrl + "%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                inCityName+"%2C"+inCountryName+"" +
                "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        return endpointUrl;
    }

    public static String parseApiResult (URL inEndpoint) {

        String weatherReport = "";

        String weatherTitle = new String();
        String weatherCity = new String();
        String weatherCountry = new String();
        String maxCel = new String();
        String minCel = new String();
        String weatherText = new String();
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(inEndpoint.openStream(), "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                total.append(line);

                try {

                    JSONObject jsonObj = new JSONObject(total.toString());
                    JSONArray values = jsonObj.toJSONArray(jsonObj.names());

                    for (int i = 0; i < values.length(); i++) {

                        JSONObject obj = values.getJSONObject(i);
                        JSONObject jResObj = obj.getJSONObject("results");
                        JSONObject jChanObj = jResObj.getJSONObject("channel");

                        String wt = jChanObj.getString("title");
                        weatherTitle = wt.substring(6);

                        JSONObject jLocObj = jChanObj.getJSONObject("location");
                        weatherCity = jLocObj.getString("city");
                        weatherCountry = jLocObj.getString("country");

                        JSONObject jItemObj = jChanObj.getJSONObject("item");
                        JSONArray jforecastObj = jItemObj.getJSONArray("forecast");
                        JSONObject j0Obj = (JSONObject) jforecastObj.get(0);

                        String maxFahr = j0Obj.getString("high");
                        maxCel = FahrToCelUtil.celFromFahr(maxFahr);

                        String minFahr = j0Obj.getString("low");
                        minCel = FahrToCelUtil.celFromFahr(minFahr);

                        weatherText = j0Obj.getString("text");

                    } // End Loop

                } catch (JSONException ex) {
                    Log.e(YaicaConstants.DEFAULT_ERROR, ex.getStackTrace().toString());
                }
            }

        } catch (FileNotFoundException e) {
            Log.e(YaicaConstants.DEFAULT_ERROR, e.getStackTrace().toString());

        } catch (IOException f) {
            Log.e(YaicaConstants.DEFAULT_ERROR, f.getStackTrace().toString());
            weatherReport = YaicaConstants.NO_LOCATION_FOUND_ERROR;
        }

        return weatherReport = weatherTitle + ": " + weatherCity + ", " + weatherCountry + ", Max :" + maxCel + ", Min:" + minCel + ", " + weatherText;
    }
}
