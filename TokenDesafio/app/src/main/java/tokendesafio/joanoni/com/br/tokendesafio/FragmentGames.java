package tokendesafio.joanoni.com.br.tokendesafio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import tokendesafio.joanoni.com.br.tokendesafio.JSON.GameData;
import tokendesafio.joanoni.com.br.tokendesafio.JSON.Games;
import tokendesafio.joanoni.com.br.tokendesafio.JSON.ProfileData;

/**
 * Created by caioj on 2/28/2018.
 */

public class FragmentGames extends Fragment {

    public ListView lv;
    public RelativeLayout rl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        lv = (ListView) rootView.findViewById(R.id.list);
        rl = (RelativeLayout) rootView.findViewById(R.id.layout_loading_data);
        // Checking if the game data has being downloaded to set it
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(MainActivity.getInstance().hasDownloadedGameData){
                    SetGameData();
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(runnable, 100);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.menu_games);
    }


    private void SetGameData(){
        ArrayList<GameData> gamesData = new ArrayList<>(MainActivity.getInstance().games.games);
        lv.setAdapter(new ListGameAdapter(MainActivity.getInstance(), gamesData));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();

            }
        });
    }
}

