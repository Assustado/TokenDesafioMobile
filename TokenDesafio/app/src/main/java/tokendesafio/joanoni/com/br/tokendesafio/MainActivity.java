package tokendesafio.joanoni.com.br.tokendesafio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import tokendesafio.joanoni.com.br.tokendesafio.JSON.Games;
import tokendesafio.joanoni.com.br.tokendesafio.JSON.ProfileData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity instance;

    public static MainActivity getInstance(){
        return instance;
    }

    public ProfileData profileData;
    public Games games;
    public boolean hasDownloadedGameData;
    public boolean hasDownloadedProfileData;
    public boolean hasDownloadedProfilePicture;
    public int gamesImageDownloaded = 0;
    public boolean hasDownloadedAllImages;
    private int currentScreenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Start at the games screen
        DisplaySelectedScreen(R.id.nav_games);
        //Start profile json download
        new GetJSON().execute("https://dl.dropboxusercontent.com/s/fiqendqz4l1xk61/userinfo", "profile");
        //Start games json download
        new GetJSON().execute("https://dl.dropboxusercontent.com/s/1b7jlwii7jfvuh0/games", "games");

        MainActivity.instance = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Close the nav drawer if it is open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Go back to the "main" screen
            if(currentScreenId != R.id.nav_games){
                DisplaySelectedScreen(R.id.nav_games);
            }else {
                super.onBackPressed();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DisplaySelectedScreen(item.getItemId());
        return true;
    }

    //Change the screen that is being showed
    private void DisplaySelectedScreen(int itemId) {
        currentScreenId = itemId;
        //Creating fragment object
        Fragment fragment = null;

        //Initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_games:
                fragment = new FragmentGames();
                break;
            case R.id.nav_profile:
                fragment = new FragmentProfile();
                break;
        }

        //Replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    //As impressive as it may sound, this class get json
    public class GetJSON extends AsyncTask<String , Void ,String> {
        String server_response;
        String type;
        @Override
        protected String doInBackground(String... strings) {
            //Doing downloads stuff
            URL url;
            HttpsURLConnection urlConnection = null;
            //To know if the download is profile or games
            //Could have it done at the constructor, but it was faster
            type = strings[1];
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Using Gson because it is reaaaally easy
            Gson gson = new GsonBuilder().create();
            if(type=="profile") {
                profileData = gson.fromJson(server_response, ProfileData.class);
                //Set name at nav drawer
                ((TextView) findViewById(R.id.textView_nav_name)).setText(profileData.name + " " + profileData.lastname);
                //Starting the profile image download
                new DownloadImageTask((ImageView) findViewById(R.id.imageView_nav_picture), -1)
                        .execute(profileData.avatar);
                //This is to notify another class
                //TODO: replace it for an event
                hasDownloadedProfileData = true;
            }else if(type == "games"){
                games = gson.fromJson(server_response, Games.class);
                //This is to notify another class
                //TODO: replace it for an event
                hasDownloadedGameData = true;
                //Start all games images download
                for(int i=0;i<games.games.size();i++){
                    new DownloadImageTask(null, i).execute(games.games.get(i).image);
                }
            }


        }
    }

    //Converting InputStream to String
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    //Download Image, O RLY?
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int gamePosition;

        public DownloadImageTask(@Nullable ImageView bmImage, int gamePosition) {
            //Receive an ImageView to update it programmatically
            this.bmImage = bmImage;
            this.gamePosition = gamePosition;
        }

        protected Bitmap doInBackground(String... urls) {
            //Download stuff
            String urldisplay = urls[0];
            Bitmap picture = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                picture = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return picture;
        }

        protected void onPostExecute(Bitmap result) {
            //Check if it is an image of a game or profile
            if(gamePosition==-1) {
                profileData.picture = result;
                hasDownloadedProfilePicture = true;
                bmImage.setImageBitmap(result);
                bmImage.setVisibility(View.VISIBLE);
                findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
            }else{
                //Check if it hasn`t an image
                if(result == null){
                    games.games.get(gamePosition).imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
                }else {
                    games.games.get(gamePosition).imageBitmap = result;
                }
                //This is to notify another class
                //TODO: replace it for an event
                gamesImageDownloaded++;
                if(gamesImageDownloaded == games.games.size()) {
                    hasDownloadedAllImages = true;
                }
            }
        }
    }
}

