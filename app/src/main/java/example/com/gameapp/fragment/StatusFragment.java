package example.com.gameapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import example.com.gameapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private TextView textScore;
    private ImageView imageView;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        textScore = view.findViewById(R.id.text_score);
        imageView = view.findViewById(R.id.picture);
        return view;
    }

    public void setTextScore(String score) {
        textScore.setText(score);
    }

    public void setPicture(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

}
