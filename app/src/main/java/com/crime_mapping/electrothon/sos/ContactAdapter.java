package com.crime_mapping.electrothon.sos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.VH> {


    List<ContactModel> contactModel;
    Context context;
    String no;

    public ContactAdapter(List<ContactModel> contactModel, Context context) {
        this.contactModel = contactModel;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new VH(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.name.setText(contactModel.get(position).getName());
        holder.phn.setText(contactModel.get(position).getPhone());
        no = contactModel.get(position).getPhone();
    }

    @Override
    public int getItemCount() {
        return contactModel.size();
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView card;
        TextView phn;
        TextView name;
        Context context;
        ContactModel model;

        public VH(@NonNull final View itemView, final Context context) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.personName);
            phn = (TextView) itemView.findViewById(R.id.noSharedByother);
            card = (CardView) itemView.findViewById(R.id.contactCard);
            this.context = context;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("ContactNo", no);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }
}
