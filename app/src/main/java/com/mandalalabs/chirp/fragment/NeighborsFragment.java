package com.mandalalabs.chirp.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.adapter.NeighborsListAdapter;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NeighborsFragment extends Fragment implements OnListFragmentInteractionListener {

    private static final String TAG = Constants.LOG_TAG;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private RecyclerView rvNeighbors;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NeighborsFragment() {
    }

    @SuppressWarnings("unused")
    public static NeighborsFragment newInstance(int columnCount) {
        NeighborsFragment fragment = new NeighborsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbors_list, container, false);

        Log.i(TAG, "onCreateView()");
        if (UserSession.neighborsList == null) {
            UserSession.neighborsList = new ArrayList<ParseObject>();
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            rvNeighbors = (RecyclerView) view;
            if (mColumnCount <= 1) {
                rvNeighbors.setLayoutManager(new LinearLayoutManager(context));
            } else {
                rvNeighbors.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            rvNeighbors.setAdapter(new NeighborsListAdapter(UserSession.neighborsList, mListener));
        }

        getNeighbors();
        return view;
    }

    public void getNeighbors() {
        Location userLocation = (UserSession.currentLocation == null ? UserSession.lastKnownLocation : UserSession.currentLocation);

        Log.d(TAG, "Getting neighbors!!!");
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.TABLE_USER_LOCATION_INFO);
        if (userLocation != null) {
            query.whereWithinMiles(Constants.LOCATION_KEY, new ParseGeoPoint(userLocation.getLatitude(), userLocation.getLongitude()), 100.0);
        }
//        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    UserSession.neighborsList.clear();
                    UserSession.neighborsList.addAll(objects);
                    Log.d(TAG, "List of neighboring users: size = " + objects.size());
                    for (ParseObject object : objects) {
                        ParseGeoPoint userLocation = (ParseGeoPoint) object.get(Constants.LOCATION_KEY);
                        Log.d(TAG, "User ID:" + object.get("userId") + "; Location: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());
                    }
                    rvNeighbors.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction(ParseObject item) {

    }
}
