package com.mandalalabs.chirp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.fragment.OnListFragmentInteractionListener;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.ParseObject;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ParseObject} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ChirpsListAdapter extends RecyclerView.Adapter<ChirpsListAdapter.ViewHolder> {

    private final List<ParseObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ChirpsListAdapter(List<ParseObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chirps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.get(Constants.SENDER_KEY).toString());
        holder.mContentView.setText(holder.mItem.get(Constants.MESSAGE_KEY).toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ParseObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
