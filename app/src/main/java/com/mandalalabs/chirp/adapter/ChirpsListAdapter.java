package com.mandalalabs.chirp.adapter;

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.fragment.OnListFragmentInteractionListener;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

        ParseUser sender = holder.mItem.getParseUser(Constants.SENDER_KEY);
        final boolean isMe = sender != null && sender.getObjectId().equals(UserSession.loggedInUser.getObjectId());
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.mImageMe.setVisibility(View.VISIBLE);
            holder.mImageOther.setVisibility(View.GONE);
            holder.mBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            holder.mImageOther.setVisibility(View.VISIBLE);
            holder.mImageMe.setVisibility(View.GONE);
            holder.mBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        final ImageView profileView = isMe ? holder.mImageMe : holder.mImageOther;
//        Picasso.with(getContext()).load(getProfileUrl(message.getUserId())).into(profileView);
        holder.mBody.setText(holder.mItem.getString(Constants.MESSAGE_KEY));

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
        public final ImageView mImageOther;
        public final ImageView mImageMe;
        public final TextView mBody;
        public ParseObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageOther = (ImageView) view.findViewById(R.id.ivProfileOther);
            mImageMe = (ImageView) view.findViewById(R.id.ivProfileMe);
            mBody = (TextView) view.findViewById(R.id.tvBody);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBody.getText() + "'";
        }
    }
}
