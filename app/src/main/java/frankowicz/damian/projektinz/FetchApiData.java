package frankowicz.damian.projektinz;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import frankowicz.damian.projektinz.Model.Employee;

class FetchApiData extends AsyncTask<Void, Void, String> {

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

}