package com.example.cineverse_movie_app_two;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mParams;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mParams = null;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Add text params
            if (getParams() != null && getParams().size() > 0) {
                for (Map.Entry<String, String> entry : getParams().entrySet()) {
                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd).getBytes());
                    bos.write((lineEnd).getBytes());
                    bos.write(entry.getValue().getBytes("UTF-8"));
                    bos.write(lineEnd.getBytes());
                }
            }

            // Add file params
            if (getByteData() != null && getByteData().size() > 0) {
                for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                    DataPart dataFile = entry.getValue();
                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" +
                            entry.getKey() + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd).getBytes());
                    bos.write(("Content-Type: " + dataFile.getType() + lineEnd).getBytes());
                    bos.write(lineEnd.getBytes());

                    bos.write(dataFile.getContent());
                    bos.write(lineEnd.getBytes());
                }
            }

            bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data, String type) {
            this.fileName = name;
            this.content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }
        public byte[] getContent() {
            return content;
        }
        public String getType() {
            return type;
        }
    }
}
