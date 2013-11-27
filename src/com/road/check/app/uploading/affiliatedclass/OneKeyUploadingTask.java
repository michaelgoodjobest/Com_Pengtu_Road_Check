package com.road.check.app.uploading.affiliatedclass;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.road.check.app.uploading.affiliatedclass.CustomMultipartEntity.ProgressListener;
import com.road.check.sqlite.DatabaseService;

public class OneKeyUploadingTask extends AsyncTask<HttpResponse, Integer, String> {
    private ProgressDialog pd;
    private long totalSize;
    private Activity baseactivity;
    private String apiUrl = "";
    private DatabaseService dbs;
    public OneKeyUploadingTask(Activity activity,String apiUrl,DatabaseService dbs){
    	this.baseactivity = activity;
    	this.apiUrl = apiUrl;
    	this.dbs = dbs;
    }
    @Override
    protected void onPreExecute(){
        pd= new ProgressDialog(baseactivity);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("请稍候，正在上传数据...");
        pd.setCancelable(false);
        pd.show();
    }
    @Override
    protected String doInBackground(HttpResponse... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(apiUrl);
        try{
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num){
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });
            ContentBody cb = new StringBody("123");
            multipartContent.addPart("dd", cb);
            multipartContent.addPart("uploaded_file", new FileBody(
                    new File("")));
            totalSize= multipartContent.getContentLength();
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            String serverResponse = EntityUtils.toString(response.getEntity());
            
            return serverResponse;
        }catch(Exception e) {
        }
        return null;

    }
    @Override
    protected void onProgressUpdate(Integer... progress){
        pd.setProgress((int) (progress[0]));
    }
    @Override
    protected void onPostExecute(String ui) {
    	dbs.road_check_table.updateOneKeyUploadingState();
        pd.dismiss();
    }
}
