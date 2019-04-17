package idv.david.jsontransferandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private AsyncTask retrieveTeamTask;


    class RetrieveTeamTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = Util.URL;

            String jsonIn;


            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Team t = new Team(1, "aa");
            String jsonStr = "";
            jsonStr = gson.toJson(t);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mem_no", jsonStr);


            getRemoteData(url, jsonObject.toString());
            jsonIn = getRemoteData(url, "123");


            return jsonIn;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    // check if the device connect to the network
    private boolean networkConnected() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getRemoteData(String url, String outStr) {
        HttpURLConnection connection = null;
        StringBuilder inStr = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            // 不知道請求內容大小時可以呼叫此方法將請求內容分段傳輸，設定0代表使用預設大小
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "UTF-8");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "output: " + outStr);
            bw.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.d(TAG, "input: " + inStr);
        return inStr.toString();
    }



    public void onSearchClick(View v) {

        String area = "M000001";
        if (networkConnected()) {
            retrieveTeamTask = new RetrieveTeamTask().execute(Util.URL, area);

        }
        String output = null;
        try {
            output = new RetrieveTeamTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPause() {
        if (retrieveTeamTask != null) {
            retrieveTeamTask.cancel(true);
            retrieveTeamTask = null;
        }
        super.onPause();
    }
}
