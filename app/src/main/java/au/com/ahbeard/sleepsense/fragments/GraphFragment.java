package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.widgets.SleepSenseGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by neal on 12/03/2016.
 */
public class GraphFragment extends Fragment {

    @Bind(R.id.graph_view)
    SleepSenseGraphView mGraphView;

    private float[] mValues;
    private String[] mLabels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        ButterKnife.bind(this,view);

        mGraphView.setValues(mValues,mLabels,20,100);

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(float[] values, String[] names) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putFloatArray("values",values);
        args.putStringArray("labels",names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mValues = getArguments().getFloatArray("values");
            mLabels = getArguments().getStringArray("labels");
        }

    }

}
