package frankowicz.damian.projektinz;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
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
    Button btnAnimationTest;
    TextView txtViewTime;
    TextView txtViewResponse;
    SqLiteDb db;

    ImageView imgLogo;
    Button btnBeginAnimationTest;

    RelativeLayout relativeLayout;
    List<String> durations = new ArrayList<>();
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchDataFromApi = findViewById(R.id.btnFetchDataFromApi);
        btnGetDataFromDatabase = findViewById(R.id.btnGetDataFromDatabase);
        btnGetApplicationDirectory = findViewById(R.id.btnGetApplicationDirectory);
        btnAnimationTest = findViewById(R.id.btnAnimationTest);
        txtViewTime = findViewById(R.id.txViewTime);
        txtViewResponse = findViewById(R.id.txtViewResponse);
        relativeLayout = findViewById(R.id.layout);

        db = new SqLiteDb(this);
        db.open();
        createAndWriteFile();

        btnFetchDataFromApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    hideAnimationTest();
                    startTimer();
                    try {
                        response = new FetchDataFromApi().execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        response = getErrorMsg(e);
                    }
            }
        });

        btnGetDataFromDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAnimationTest();
                startTimer();
                new GetDataFromDatabase().execute();
            }
        });

        btnGetApplicationDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAnimationTest();
                startTimer();
                new GetFileFromAppDirectory().execute();
            }
        });

        btnAnimationTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Times: " + durations.toString());
                txtViewResponse.setText("");
                resetAnimationTest();
                btnBeginAnimationTest = new Button(getApplicationContext());
                btnBeginAnimationTest.setText("ROZPOCZNIJ TEST");
                btnBeginAnimationTest.setVisibility(View.VISIBLE);
                relativeLayout.addView(btnBeginAnimationTest);

                btnBeginAnimationTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ResizeAnimation resizeAnimation = new ResizeAnimation(imgLogo);
                        resizeAnimation.setDuration(3000);
                        startTimer();
                        imgLogo.startAnimation(resizeAnimation);
                        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                endTimer();
                                relativeLayout.removeView(imgLogo);
                                resetAnimationTest();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        });
    }


    void hideAnimationTest() {
        if (imgLogo != null) imgLogo.setVisibility(View.INVISIBLE);
        if (btnBeginAnimationTest != null) btnBeginAnimationTest.setVisibility(View.INVISIBLE);
    }

    void resetAnimationTest() {
        RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        imgLogo = new ImageView(getApplicationContext());
        imgLogo.setLayoutParams(vp);
        imgLogo.setVisibility(View.VISIBLE);
        imgLogo.setImageResource(R.drawable.ic_android);
        relativeLayout.addView(imgLogo);
    }

    void startTimer() {
        startTime = System.currentTimeMillis();
        response = "...";
        txtViewTime.setText("--:--");
    }

    void endTimer() {
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime);
        durations.add(String.valueOf(duration));
        txtViewTime.setText(String.valueOf(duration));
        txtViewResponse.setText(response);

        index++;
        if(index<10) btnGetApplicationDirectory.performClick();
        else {
            File file = new File(getApplicationContext().getFilesDir(), "times.txt");
            if (!file.exists()) {
                try {
                    FileOutputStream outputStream;
                    outputStream = openFileOutput("times.txt", getApplicationContext().MODE_PRIVATE);
                    for(int i=0;i<durations.size();i++)
                    outputStream.write(durations.get(i).getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    response = getErrorMsg(e);
                    System.out.print(e.toString());
                }
            }
        }

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
                response = getErrorMsg(e);
                System.out.print(e.toString());
            }
        }
    }

    String getErrorMsg(Exception e) {
        return "Wystąpił błąd, spróbuj jeszcze raz.\n" + e.toString();
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

    public class ResizeAnimation extends Animation {
        final int startWidth;
        final int startHeight;
        final int targetWidth;
        final int targetHeight;
        View view;

        public ResizeAnimation(View view) {
            this.view = view;
            startWidth = view.getWidth();
            startHeight = view.getHeight();
            targetWidth = startWidth * 10;
            targetHeight = startHeight * 10;

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }

        @Override
        public void reset() {
            super.reset();
        }
    }
}


class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }

}


