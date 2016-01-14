package com.pgyer.dialog.providers;

import com.intellij.openapi.ui.Messages;
import com.pgyer.dialog.utils.CustomMultiPartEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by Tao9jiu on 16/1/5.
 */
public class UploadService  implements CustomMultiPartEntity.ProgressListener {

    public UploadServiceDelegate uploadServiceDelegate;
    public HttpPost post;
    public static final String PGY_BASE_URL = "http://www.pgyer.com/";
    public static final String PGY_UPLOAD_URL = "http://www.pgyer.com/apiv1/app/upload";


    public void uploadAPKfile(final String apikey, final String ukey, final String filepath,final String changlog, UploadServiceDelegate uploadServiceDelegate){

        this.uploadServiceDelegate = uploadServiceDelegate;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File uploadFile = new File(filepath);

                    CustomMultiPartEntity multiPartEntity = new CustomMultiPartEntity(UploadService.this);

                    multiPartEntity.addPart("uKey", new StringBody(ukey));
                    multiPartEntity.addPart("_api_key", new StringBody(apikey));
                    multiPartEntity.addPart("updateDescription", new StringBody(changlog,Charset.forName("UTF-8")));
                    multiPartEntity.addPart("file", new FileBody(uploadFile));

                    if (UploadService.this.uploadServiceDelegate != null)
                    {
                        UploadService.this.uploadServiceDelegate.onPackageSizeComputed(multiPartEntity.getContentLength());
                    }

                    post = new HttpPost(PGY_UPLOAD_URL);
                    post.setEntity(multiPartEntity);


                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");

                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject data = jsonObject.getJSONObject("data");
                    String appShort;
//                    String qrUrl;
                    appShort = data.getString("appShortcutUrl");
//                    qrUrl = data.getString("appQRCodeURL");

                    ApkInformation.getInstance().setaShort(PGY_BASE_URL+appShort);

                    if (response.getStatusLine().getStatusCode() == 200) {

                        if (UploadService.this.uploadServiceDelegate != null)
                        {

                            UploadService.this.uploadServiceDelegate.onUploadFinished(true);
                        }
                    }
                    else if (UploadService.this.uploadServiceDelegate != null)
                    {
                        UploadService.this.uploadServiceDelegate.onUploadFinished(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (UploadService.this.uploadServiceDelegate != null)
                    {
                        UploadService.this.uploadServiceDelegate.onUploadFinished(false);
                    }
                }

            }
        }).start();
    }


    @Override
    public void transferred(long num) {
        if (this.uploadServiceDelegate != null)
            this.uploadServiceDelegate.onProgressChanged(num);
    }

    /**
     * Upload service callback interface used to notify uploading actions like status or progress
     */
    public interface UploadServiceDelegate {

        /**
         * Called when the upload is done, even if an error occurred
         *
         * @param finishedSuccessful this flag is true if the upload was made successfully, false otherwise
         */
        public void onUploadFinished(boolean finishedSuccessful);

        public void onPackageSizeComputed(long totalSize);

        public void onProgressChanged(long progress);

    }
}
