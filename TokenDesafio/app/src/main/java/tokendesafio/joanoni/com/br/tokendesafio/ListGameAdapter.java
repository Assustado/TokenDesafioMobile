package tokendesafio.joanoni.com.br.tokendesafio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import tokendesafio.joanoni.com.br.tokendesafio.JSON.GameData;

/**
 * Created by caioj on 2/28/2018.
 */

public class ListGameAdapter extends BaseAdapter {

    private static ArrayList<GameData> listGames;
    private LayoutInflater mInflater;
    public int numberDownloadedImage = 0;

    public ListGameAdapter(final Context gamesFragment, ArrayList<GameData> data){
        listGames = data;
        // Checking when a new image has being downloaded to update the ListView
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(MainActivity.getInstance().gamesImageDownloaded != numberDownloadedImage){
                    numberDownloadedImage = MainActivity.getInstance().gamesImageDownloaded;
                    notifyDataSetChanged();
                    if(numberDownloadedImage==MainActivity.getInstance().games.games.size()) {
                        handler.removeCallbacks(this);
                    }else{
                        handler.postDelayed(this, 100);
                    }
                }else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(runnable, 100);
        mInflater = LayoutInflater.from(gamesFragment);
    }

    @Override
    public int getCount() {
        return listGames.size();
    }

    @Override
    public Object getItem(int position) {
        return listGames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int id = (int)getItemId(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.textViewName = (TextView) convertView.findViewById(R.id.textView_game_title);
            holder.textViewRelease = (TextView) convertView.findViewById(R.id.textView_game_release);
            holder.textViewAvaiableList = (TextView) convertView.findViewById(R.id.textVew_avaiable_list);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewGamePic);
            holder.gifImageView = (GifImageView) convertView.findViewById(R.id.gif_game);
            holder.button = (Button) convertView.findViewById(R.id.buttonTrailer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(listGames.get(id).name);
        holder.textViewRelease.setText(listGames.get(id).release_date);
        // Concat all platforms in a single string
        String listAvaiable = "";
        for(int i=0;i<listGames.get(id).platforms.size();i++){
            listAvaiable+=listGames.get(id).platforms.get(i);
            if(i<listGames.get(id).platforms.size()-1){
                listAvaiable+=", ";
            }
        }
        holder.textViewAvaiableList.setText(listAvaiable);
        holder.imageView.setImageBitmap(listGames.get(id).imageBitmap);
        //Start the youtube application to show the trailer
        //I tried to play the video inside the app, but it was a lot more complicated :/
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(listGames.get(position).trailer)));
            }
        });
        return convertView;
    }


    private class ViewHolder{
        TextView textViewName, textViewRelease, textViewAvaiableList;
        ImageView imageView;
        GifImageView gifImageView;
        Button button;
    }

}

