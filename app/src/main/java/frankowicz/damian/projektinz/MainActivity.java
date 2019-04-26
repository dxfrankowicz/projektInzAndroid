package frankowicz.damian.projektinz;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import frankowicz.damian.projektinz.Model.Employee;
import frankowicz.damian.projektinz.Model.User;

public class MainActivity extends AppCompatActivity {

    long startTime;
    long endTime;
    long duration;
    String response;

    Button btnFetchDataFromApi;
    Button btnGetDataFromDatabase;
    Button btnGetApplicationDirectory;
    TextView txtViewTime;
    TextView txtViewResponse;
    SqLiteDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchDataFromApi = findViewById(R.id.btnFetchDataFromApi);
        btnGetDataFromDatabase = findViewById(R.id.btnGetDataFromDatabase);
        btnGetApplicationDirectory = findViewById(R.id.btnGetApplicationDirectory);
        txtViewTime = findViewById(R.id.txViewTime);
        txtViewResponse = findViewById(R.id.txtViewResponse);

        db = new SqLiteDb(this);
        db.open();
        createAndWriteFile();

        btnFetchDataFromApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                try {
                    response = new FetchDataFromApi().execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Wystąpił błąd, spróbuj jeszcze raz.\n" + e.toString();
                }
            }
        });

        btnGetDataFromDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                new GetDataFromDatabase().execute();
            }
        });

        btnGetApplicationDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                new GetFileFromAppDirectory().execute();
            }
        });
    }

    void startTimer() {
        startTime = System.currentTimeMillis();
        txtViewResponse.setText("...");
        txtViewTime.setText("--:--");
    }

    void endTimer() {
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime);
        txtViewTime.setText(String.valueOf(duration));
        txtViewResponse.setText(response);
    }

    void createAndWriteFile() {
        File file = new File(getApplicationContext().getFilesDir(), "test.txt");
        if (!file.exists()) {
            try {
                FileOutputStream outputStream;
                outputStream = openFileOutput("test.txt", getApplicationContext().MODE_PRIVATE);
                outputStream.write("Testowy dokument tekstowy".getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                response = "Wystąpił błąd, spróbuj jeszcze raz.\n" + e.toString();
                System.out.print(e.toString());
            }
        }
    }

    class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Cursor k = db.getAll();
            StringBuilder stringBuilder = new StringBuilder();
            while (k.moveToNext()) {
                User user = new User(k.getString(1), k.getString(2));
                user.setId(k.getInt(0));
                stringBuilder.append(user.toString() + "\n");
            }
            response = stringBuilder.toString();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endTimer();
        }
    }

    class FetchDataFromApi extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://dummy.restapiexample.com/api/v1/employees");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    List<Employee> employeeList = new ArrayList();
                    if (stringBuilder != null) {
                        try {
                            JSONArray array = new JSONArray(stringBuilder.toString());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                employeeList.add(new Employee(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("employee_name"),
                                        jsonObject.getString("employee_salary"),
                                        jsonObject.getString("employee_age"),
                                        jsonObject.getString("profile_image")
                                ));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return employeeList.toString();
                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            endTimer();
        }
    }

    class GetFileFromAppDirectory extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            File rootDirectory = getApplicationContext().getFilesDir();
            File file = new File(rootDirectory, "test.txt");
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                response = "Wystąpił błąd, spróbuj jeszcze raz.\n" + e.toString();
            }
            response = "Ścieżka\n " + rootDirectory.getPath() + "\n\nZawartość pliku\n" + text;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endTimer();
        }
    }
}

