package example.com.gameapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import example.com.gameapp.AnswerListener;
import example.com.gameapp.R;
import example.com.gameapp.game.Question;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Context context;
    private GridView gridView;
    private ArrayAdapter<String> adapter;

    AnswerListener answerListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        answerListener = (AnswerListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        gridView = view.findViewById(R.id.answers);
        adapter = new ArrayAdapter<>(context, R.layout.item_question_columns2, R.id.name);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        return view;
    }

    public void optionsCount(int counts) {
        if (counts > 6) {
            adapter = new ArrayAdapter<>(context, R.layout.item_question_columns2, R.id.name);
            gridView.setAdapter(adapter);
            gridView.setNumColumns(2);
        } else {
            adapter = new ArrayAdapter<>(context, R.layout.item_question_columns1, R.id.name);
            gridView.setAdapter(adapter);
            gridView.setNumColumns(1);
        }
    }

    public void showQuestion(Question question) {
        adapter.clear();
        adapter.addAll(question.getPossibleNames());
        adapter.notifyDataSetChanged();
    }

    public void clear() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String answer = adapter.getItem(position);
        answerListener.answer(answer);
    }
}
