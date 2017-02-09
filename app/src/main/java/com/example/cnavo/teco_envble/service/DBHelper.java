package com.example.cnavo.teco_envble.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by cnavo on 08.02.2017.
 */

public class DBHelper {
    private static final String BASE_URL = "http://mociotdb2.teco.edu/db.php/";
    private static final String DB_NAME = "CNAVOLSKYIDB";
    private static final String DB_KEY = "MeineDatenbank";

    private static final String CREATE_DB = "createDB?dbName=" + DB_NAME + "&dbKey=" + DB_KEY;
    private static final String WRITE_DB = "write/" + DB_NAME + "?dbKey=" + DB_KEY;
    private static final String READ_DB = "read/" + DB_NAME + "?dbKey=" + DB_KEY + "&q=";
    private static final String PRETTY_OUTPUT = "&pretty=true";

    private DBInterface dbInterface;

    public DBHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        this.dbInterface = retrofit.create(DBInterface.class);
    }

    public void createDB() {
        this.dbInterface.createDB().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("createDB was successful: " + response.isSuccessful() + " and body is: " + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void writeDB(String body) {
        this.dbInterface.writeDB(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("writeDB was successful: " + response.isSuccessful() + " and body is: " + response.message());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void readDB(String query) {
        this.dbInterface.readDB(READ_DB + query + PRETTY_OUTPUT).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonObject = response.body();
                    JsonArray results = jsonObject.getAsJsonArray("results");
                    JsonArray series = results.get(0).getAsJsonObject().getAsJsonArray("series");
                    JsonObject innerData = series.get(0).getAsJsonObject();

                    JsonArray columns = innerData.getAsJsonArray("columns");
                    JsonArray values = innerData.getAsJsonArray("values");

                    System.out.println("Size is: " + columns.size() + " Value is: " + columns.toString());
                    System.out.println("Size is: " + values.size() + " Value is: " + values.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    interface DBInterface {
        @POST(WRITE_DB)
        Call<String> writeDB(@Body String postBody);

        @GET(CREATE_DB)
        Call<ResponseBody> createDB();

        @GET
        Call<JsonObject> readDB(@Url String url);
    }

}
