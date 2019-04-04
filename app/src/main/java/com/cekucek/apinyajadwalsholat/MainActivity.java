package com.cekucek.apinyajadwalsholat;

import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private ProgressDialog progressDialog;

    private static String url = "https://api.banghasan.com/sholat/format/json/kota";
    ArrayList<HashMap<String, String>> listKota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listKota = new ArrayList<>();
        listView = (ListView)findViewById(R.id.daftar_kota);

        new GetCity().execute();
    }

    private class GetCity extends AsyncTask<Void, Void, Void> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Mohon Tunggu..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler sh = new HttpHandler();

            String jsonString = sh.makeServiceCall(url);

            Log.e(TAG, "Response : " + jsonString);

            if (jsonString != null) {

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("kota");

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        String id = c.getString("id");
                        String nama = c.getString("nama");

                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("id", id);
                        hashMap.put("nama", nama);

                        listKota.add(hashMap);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON Parsing Error : "+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            if (progressDialog.isShowing());
            progressDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, listKota, R.layout.daftar_kota, new String[]{"id", "nama"}, new int[]{R.id.id_kota, R.id.nama_kota});
            listView.setAdapter(adapter);
        }

    }


}
