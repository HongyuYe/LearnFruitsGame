package example.com.gameapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.com.gameapp.R;
import example.com.gameapp.databases.Score;

/**
 * A simple {@link Fragment} subclass.
 */
public class HighScoreFragment extends Fragment {

    private ListView listView;
    private LinearLayout titleTab;
    private TextView emptyTip;

    public HighScoreFragment() {
        // Required empty public constructor
    }

    public static HighScoreFragment getInstance(ArrayList<Score> scores) {
        HighScoreFragment fragment = new HighScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("scores", scores);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<Score> scores = getArguments().getParcelableArrayList("scores");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_high_score, container, false);
        listView = view.findViewById(R.id.score_list);
        titleTab = view.findViewById(R.id.title_tab);
        emptyTip = view.findViewById(R.id.empty_tip);
        if (scores == null || scores.size() == 0) {
            titleTab.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            emptyTip.setVisibility(View.VISIBLE);
        } else {
            HighScoreAdapter adapter = new HighScoreAdapter(getActivity(), scores);
            listView.setAdapter(adapter);
        }
        return view;
    }

    private static class HighScoreAdapter extends BaseAdapter {
        private Context context;
        private List<Score> scoreList;

        public HighScoreAdapter(Context context, List<Score> scoreList) {
            this.context = context;
            this.scoreList = scoreList;
        }

        @Override
        public int getCount() {
            return scoreList == null ? 0 : scoreList.size();
        }

        @Override
        public Object getItem(int position) {
            return scoreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_high_score, null);
            }
            TextView score = convertView.findViewById(R.id.score);
            TextView total = convertView.findViewById(R.id.total);
            TextView createTime = convertView.findViewById(R.id.create_time);
            Score item = scoreList.get(position);
            score.setText(item.score + "");
            total.setText(item.total + "");
            Date date = new Date(item.time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            createTime.setText(format.format(date));
            return convertView;
        }
    }
}
