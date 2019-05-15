package com.sori.touchsori.search;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.activity.AnsimActivity;
import com.sori.touchsori.data.ApiContactListData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ContactsViewHolder> {

    private ArrayList<ApiContactListData> sosList;

    private ArrayList<ContactItem> contactItems;
    private List<ContactItem> items = null;
    private Context mContext;
    private boolean type;

    public SearchAdapter(Context context , List<ContactItem> contactItems , boolean type) {
        this.mContext = context;
        this.items = contactItems;
        this.contactItems = new ArrayList<>();
        this.contactItems.addAll(items);
        this.type = type;
    }

    public SearchAdapter(Context context , ArrayList<ApiContactListData> contactItems , boolean type) {
        this.mContext = context;
        this.sosList = contactItems;
        this.type = type;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_adapter_view , parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {
        if (type) {
            final ContactItem contactItem = items.get(position);
            holder.name.setText(contactItem.getUser_Name());
            holder.number.setText(contactItem.getUser_Number());

            holder.iconTv.setText(contactItem.getUser_Name().substring(0 , 1));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchActivity)mContext).longClickDialogShow(contactItem.getUser_Name() , contactItem.getUser_Number() , "insert");
                }
            });
        }else {
            final ApiContactListData apiContactListData = sosList.get(position);
            holder.name.setText(apiContactListData.getName());
            holder.number.setText(apiContactListData.getPhone());


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ((SearchActivity)mContext).longClickDialogShow(apiContactListData.getName() , apiContactListData.getPhone() , "delete");
                    return true;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (type) {
            return items.size();
        }else {
            return sosList.size();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(contactItems);
        } else {
            for (ContactItem contactItem : contactItems) {
                String name = contactItem.getUser_Name();
                if (name.toLowerCase().contains(charText)) {
                    items.add(contactItem);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        public TextView number , name , iconTv;
        public ContactsViewHolder(View itemView) {
            super(itemView);

            iconTv = itemView.findViewById(R.id.serach_adapter_icon_img);
            number = itemView.findViewById(R.id.search_adapter_number_tv);
            name = itemView.findViewById(R.id.search_adapter_name_tv);
        }
    }

}
