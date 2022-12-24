package com.example.pppb_uas.Views.Dashboard;

import static com.example.pppb_uas.Views.Overview.MainActivity.sharedpreferences;
import static com.example.pppb_uas.Views.Overview.MainActivity.userLoggedIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pppb_uas.Model.ComplaintData;
import com.example.pppb_uas.R;
import com.example.pppb_uas.Views.Modify.ModifyActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.WordViewHolder> {
    private final List<ComplaintData> complaintData;
    private final LayoutInflater inflater;
    SharedPreferences sessions;

    public ComplaintAdapter(Context context, List<ComplaintData> complaintData) {
        inflater = LayoutInflater.from(context);
        this.complaintData = complaintData;
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ComplaintAdapter mAdapter;
        private final TextView tv_id, tv_title, tv_descriptions, tv_dateHappened, tv_locationHappened, tv_edit, tv_delete, tv_dataOwner;
        private final ImageView iv_deleteIcon, iv_editIcon;
        public WordViewHolder(View itemView, ComplaintAdapter adapter) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_dataId);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_descriptions = itemView.findViewById(R.id.tv_dataComplaint);
            tv_dateHappened = itemView.findViewById(R.id.tv_date);
            tv_locationHappened = itemView.findViewById(R.id.tv_locations);
            tv_edit = itemView.findViewById(R.id.tv_edit);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tv_dataOwner = itemView.findViewById(R.id.tv_dataOwner);

            iv_deleteIcon = itemView.findViewById(R.id.iv_deleteIcon);
            iv_editIcon = itemView.findViewById(R.id.iv_editIcon);

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflater.inflate(R.layout.recycler_data, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        sessions = holder.itemView.getContext().getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);

        ComplaintData mCurrent = complaintData.get(position);
        holder.tv_id.setText(mCurrent.getId());
        holder.tv_title.setText(mCurrent.getTitle());
        holder.tv_descriptions.setText(mCurrent.getDescriptions());
        holder.tv_dateHappened.setText(mCurrent.getDateHappened());
        holder.tv_locationHappened.setText(mCurrent.getLocationHappened());

        if (!sessions.getString(userLoggedIn, "").equals(mCurrent.getSender())) {
            holder.tv_dataOwner.setText(mCurrent.getSender());
            holder.tv_delete.setVisibility(View.GONE);
            holder.tv_edit.setVisibility(View.GONE);
            holder.iv_editIcon.setVisibility(View.GONE);
            holder.iv_deleteIcon.setVisibility(View.GONE);
        } else {
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View v = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_alert_delete, (LinearLayout) view.findViewById(R.id.ll_mainLayout));
                    builder.setView(v);

                    final AlertDialog alertDialog = builder.create();

                    if (alertDialog.getWindow() != null){
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    alertDialog.show();

                    LinearLayout ll_mainLayout = v.findViewById(R.id.ll_mainLayout);
                    Button delete = ll_mainLayout.findViewById(R.id.btn_delete);
                    Button cancelDelete = ll_mainLayout.findViewById(R.id.btn_cancelDelete);

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.child("data_complaint").child(mCurrent.getId()).removeValue();
                            alertDialog.dismiss();
                            Toast.makeText(view.getContext(), "Data has been deleted", Toast.LENGTH_SHORT).show();
                        }
                    });

                    cancelDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            holder.tv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent(new Intent(holder.itemView.getContext(), ModifyActivity.class));
                    data.putExtra("status", "modifyData");
                    data.putExtra("id", mCurrent.getId());
                    data.putExtra("descriptions", mCurrent.getDescriptions());
                    data.putExtra("datehappened", mCurrent.getDateHappened());
                    data.putExtra("location", mCurrent.getLocationHappened());
                    data.putExtra("title", mCurrent.getTitle());
                    holder.itemView.getContext().startActivity(data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return complaintData.size();
    }
}
