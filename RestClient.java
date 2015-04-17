import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Asistant on 15/04/2015.
 */
public class RestClient {
    public static final String POST_METHOD = "POST";
    public static final String GET_METHOD = "GET";

    private HttpURLConnection connection;
    private String url;
    private String method;
    private ArrayList<Header> headers;
    private String body;
    private String response;
    private String error;
    private int statusCode;


    public RestClient(String url, String method, String body) {
        this(url, method);
        if (body != null)
            this.body = body;
    }

    public RestClient(String url, String method) {
        this.url = url;
        this.method = method;
        this.body = "";
        this.headers = new ArrayList<>();
    }

    public void execute() {
        error = "";
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            for (Header header : headers) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            connection.setUseCaches(false);
            connection.setDoInput(true);
            if (method.equals(POST_METHOD)) {
                //Send data if needed
                connection.setDoOutput(true);
                connection.connect();
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(body);
                wr.flush();
                wr.close();
            }

            //Get Response
            statusCode = connection.getResponseCode();
            response = getAsString(connection.getInputStream());

        } catch (Exception e) {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null)
                error = getAsString(errorStream);
            Log.d(ApplicationData.TAG, error);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getAsString(InputStream is) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public String getResponse() {
        return this.response;
    }

    public String getError() {
        return this.error;
    }

    public void addHeader(String key, String value) {
        headers.add(new Header(key, value));
    }

    public int getStatusCode() {
        return statusCode;
    }

    private class Header {
        private String key;
        private String value;

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}

