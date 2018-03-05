package tokendesafio.joanoni.com.br.tokendesafio;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;

/**
 * Created by caioj on 2/28/2018.
 */

public class FragmentProfile extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.menu_profile);
        if(MainActivity.getInstance().hasDownloadedProfileData) {
            SetProfileData();
        }else{
            // Checking if the profile data has being downloaded to set it
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(MainActivity.getInstance().hasDownloadedProfileData){
                        SetProfileData();
                        handler.removeCallbacks(this);
                    }else {
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.postDelayed(runnable, 100);
        }
        // Checking if the profile picture has being downloaded to update at the ImageView
        final Handler handlerPic = new Handler();
        final Runnable runnablePic = new Runnable() {
            @Override
            public void run() {
                if(MainActivity.getInstance().hasDownloadedProfilePicture){
                    SetProfilePicture();
                    handlerPic.removeCallbacks(this);
                }else {
                    handlerPic.postDelayed(this, 100);
                }
            }
        };
        handlerPic.postDelayed(runnablePic, 100);
    }

    private void SetProfileData(){
        getActivity().findViewById(R.id.gif_profile).setVisibility(View.INVISIBLE);
        ((TextView) getActivity().findViewById(R.id.textView_profile_name)).setText(MainActivity.getInstance().profileData.name);
        ((TextView) getActivity().findViewById(R.id.textViewProfileEmail)).setText(MainActivity.getInstance().profileData.email);
        //To avoid change to API26, I will parse the LocalDateTime
        String birthdayParsed = MainActivity.getInstance().profileData.birthday.substring(0, 10);
        birthdayParsed = birthdayParsed.substring(8,10)+("/")+birthdayParsed.substring(5,7)+("/")+birthdayParsed.substring(0,4);
        ((TextView) getActivity().findViewById(R.id.textViewProfileBirthday)).setText(birthdayParsed);
        ((TextView) getActivity().findViewById(R.id.textViewProfileAddress)).setText(MainActivity.getInstance().profileData.address);
        ((TextView) getActivity().findViewById(R.id.textViewProfileCity)).setText(MainActivity.getInstance().profileData.city);
        ((TextView) getActivity().findViewById(R.id.textViewProfileCountry)).setText(MainActivity.getInstance().profileData.country);
    }


    private void SetProfilePicture(){
        getActivity().findViewById(R.id.gif_profile_picture).setVisibility(View.INVISIBLE);
        ((ImageView)getActivity().findViewById(R.id.imageViewProfile)).setImageBitmap(MainActivity.getInstance().profileData.picture);
    }
}
