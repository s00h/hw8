import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.sql.SQLException;

class Main {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_KEY = "fc3565d883ab9ead40a1cdb278ff7d76";

    public static void main(String[] args) throws IOException, SQLException {
        String city, localDate, weatherText;
        Double temp;
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("api.openweathermap.org")
                .addPathSegment("data")
                .addPathSegment("2.5")
                .addPathSegment("weather")
                .addQueryParameter("q", "Vladivostok")
                .addQueryParameter("units", "metric")
                .addQueryParameter("lang", "ru")
                .addQueryParameter("appid", API_KEY)
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(httpUrl)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String responseJson = response.body().string();

        JsonNode cityNode = objectMapper.readTree(responseJson).at("/name");
        city = cityNode.asText();

        JsonNode cityDate = objectMapper.readTree(responseJson).at("/dt");
        localDate = cityDate.asText();

        JsonNode cityWeather = objectMapper.readTree(responseJson).at("/weather/0/main");
        weatherText = cityWeather.asText();

        JsonNode cityTemp = objectMapper.readTree(responseJson).at("/main/temp");
        temp = cityTemp.asDouble();


        // Заносим в БД
        Weather weather1 = new Weather(city, localDate, weatherText, temp);
        DbHandler dbHandler = new DbHandler();
        dbHandler.addWeather(weather1);

        //Читаем БД
        System.out.println(dbHandler.selectAllDb());

    }

}
