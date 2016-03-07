package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.adapters.SimpleItemAnimator;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestingFragment extends Fragment {

    LogAdapter mLogAdapter = new LogAdapter();

    @OnClick(R.id.socket_button_connect_disconnect)
    void connectOrDisconnect() {
        mConnectDisconnectButton.setEnabled(false);
    }

    @Bind(R.id.socket_recycler_view_log)
    RecyclerView mLogRecyclerView;

    @Bind(R.id.socket_button_connect_disconnect)
    Button mConnectDisconnectButton;


    public TestingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_testing, container, false);

        ButterKnife.bind(this,view);

        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setItemAnimator(new SimpleItemAnimator());

        return view;
    }

    public static Fragment newInstance() {
        return new TestingFragment();
    }

    public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        List<String> mLogItems = new ArrayList<>();

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LogViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_log, parent, false));
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            holder.bind(mLogItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mLogItems.size();
        }

        public void clear() {
            mLogItems.clear();
            notifyDataSetChanged();
        }

        public void log(String logItem) {
            if ( mLogItems.size() >= 128 ) {
                mLogItems.remove(0);
            }
            mLogItems.add(logItem);
            notifyDataSetChanged();
        }
    }

    public class LogViewHolder extends  RecyclerView.ViewHolder {

        @Bind(R.id.log_text_view)
        TextView mDebugTextView;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(String logItem) {
            mDebugTextView.setText(logItem);
        }
    }
}
