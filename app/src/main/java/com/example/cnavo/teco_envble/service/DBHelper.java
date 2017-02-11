package com.example.cnavo.teco_envble.service;

import android.content.Context;

import com.example.cnavo.teco_envble.data.CardData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

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

public class DBHelper implements DataChangeListener {
    private static final String BASE_URL = "http://mociotdb2.teco.edu/db.php/";
    private static final String DB_NAME = "CNAVOLSKYIDB";
    private static final String DB_KEY = "MeineDatenbank";

    private static final String DB_TABLE = "testTable2";

    private static final String CREATE_DB = "createDB?dbName=" + DB_NAME + "&dbKey=" + DB_KEY;
    private static final String WRITE_DB = "write/" + DB_NAME + "?dbKey=" + DB_KEY;
    private static final String READ_DB = "read/" + DB_NAME + "?dbKey=" + DB_KEY + "&q=";
    private static final String PRETTY_OUTPUT = "&pretty=true";

    private static DBInterface dbInterface;
    private static DBHelper dbHelper;

    private static String uploadQueue = "";
    private static int uploadQueueCounter = 0;

    private DBHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        dbInterface = retrofit.create(DBInterface.class);

        DataHelper.getDataHelper().setLocalDataChangeListener(this);
    }

    public static DBHelper getDBHelper() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
        }
        return dbHelper;
    }

    public void createDB() {
        dbInterface.createDB().enqueue(new Callback<ResponseBody>() {
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
        dbInterface.writeDB(body + " " + (System.currentTimeMillis() * 1000 * 1000)).enqueue(new Callback<String>() {
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
        dbInterface.readDB(READ_DB + query + PRETTY_OUTPUT).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    System.out.println("readDB was successful: " + response.isSuccessful() + " and body is: " + response.message());
                    if (response.isSuccessful()) {
                        JsonObject jsonObject = response.body();

                        JsonArray results = jsonObject.getAsJsonArray("results");
                        JsonArray series = results.get(0).getAsJsonObject().getAsJsonArray("series");
                        JsonObject innerData = series.get(0).getAsJsonObject();

                        JsonArray columns = innerData.getAsJsonArray("columns");
                        JsonArray values = innerData.getAsJsonArray("values");

                        List<JsonArray> valueSet = new ArrayList<JsonArray>();

                        for (JsonElement value : values) {
                            valueSet.add(value.getAsJsonArray());
                        }

                        for (int i = 1; i < columns.size(); i++) {
                            for (JsonArray jsonArray : valueSet) {
                                if (!jsonArray.get(i).isJsonNull()) {
                                    DataHelper.getDataHelper().addValue(columns.get(i).getAsString(), jsonArray.get(i).getAsDouble(), false);
                                }
                            }
                        }

                        System.out.println("Size is: " + columns.size() + " Value is: " + columns.toString());
                        System.out.println("Size is: " + values.size() + " Value is: " + values.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onValueAdded(String description, DataPoint dataPoint) {
        if (uploadQueueCounter == 0) {
            uploadQueue = DB_TABLE + " ";
        }
        boolean valuePresent = uploadQueue.contains(description);

        if (!valuePresent) {
            uploadQueue = uploadQueue + description + "=" + dataPoint.getY();
        }
        if (uploadQueueCounter > 20) {
            writeDB(uploadQueue);
            uploadQueueCounter = 0;
        }
    }

    @Override
    public void onItemChanged(CardData cardData) {

    }

    public void readFullDB () {
        readDB("select * from " + DB_TABLE);
    }

    public void writeTestData() {
        writeDB(DB_TABLE + " temperature=25.3,humidity=47,pressure=399,dust=199,co=24,no2=145,nh3=2534");
        writeDB(DB_TABLE + " temperature=26.3,humidity=43,pressure=199,dust=139,co=29,no2=115,nh3=2575");
        writeDB(DB_TABLE + " temperature=27.3,humidity=12,pressure=369,dust=159,co=26,no2=145,nh3=2541");
        writeDB(DB_TABLE + " temperature=23.3,humidity=42,pressure=395,dust=129,co=22,no2=135,nh3=2534");
        writeDB(DB_TABLE + " temperature=22.3,humidity=43,pressure=359,dust=119,co=26,no2=165,nh3=2587");
        writeDB(DB_TABLE + " temperature=21.3,humidity=42,pressure=334,dust=179,co=22,no2=135,nh3=2577");
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
