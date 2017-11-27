package com.example.kumar.guessceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chooseCeleb = 0;
    int locationOfCorrectAnswer  = 0;
    String[] answer = new String[4];

    ImageView imageView;

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public void celebChoosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext()," Correct! ",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext()," Wrong! it was "+celebNames.get(chooseCeleb),Toast.LENGTH_LONG).show();
        }

        createNewQuestion();

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
     public class DownloadTask extends AsyncTask<String, Void, String>{

         @Override
         protected String doInBackground(String... urls) {
             String result ="";
             URL url;
             HttpURLConnection urlConnection = null;
             try {

                 url = new URL(urls[0]);

                 urlConnection = (HttpURLConnection)url.openConnection();

                 InputStream inputStream = urlConnection.getInputStream();

                 InputStreamReader reader = new InputStreamReader(inputStream);
                 int data = reader.read();

                 while(data != -1)
                 {
                     char current = (char) data;
                     result += current;
                     data = reader.read();
                 }
                 return result;

             } catch (MalformedURLException e) {

                 e.printStackTrace();

             } catch (IOException e) {

                 e.printStackTrace();

             }
             return null;
         }
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.celebImageView);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find())
            {
                //System.out.println(m.group(1));

                celebUrls.add(m.group(1));
            }


            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find())
            {
                //System.out.println(m.group(1));

                celebNames.add(m.group(1));
            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }

        createNewQuestion();

    }
    public void createNewQuestion()
    {

        Random random = new Random();
        chooseCeleb   = random.nextInt(celebUrls.size());

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage = null;

        try {

            celebImage = imageTask.execute(celebUrls.get(chooseCeleb)).get();

        } catch (Exception e) {

            e.printStackTrace();

        }
        imageView.setImageBitmap(celebImage);

        locationOfCorrectAnswer = random.nextInt(4);

        int incorrectAnswerLocation;
        for(int i=0; i<4; i++)
        {
            if(i == locationOfCorrectAnswer)
            {
                answer[i] = celebNames.get(chooseCeleb);
            } else {

                incorrectAnswerLocation = random.nextInt(celebUrls.size());

                while(incorrectAnswerLocation == chooseCeleb)
                {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());

                }
                answer[i] = celebNames.get(incorrectAnswerLocation);
            }
        }

        button1.setText(answer[0]);
        button2.setText(answer[1]);
        button3.setText(answer[2]);
        button4.setText(answer[3]);

    }
}
