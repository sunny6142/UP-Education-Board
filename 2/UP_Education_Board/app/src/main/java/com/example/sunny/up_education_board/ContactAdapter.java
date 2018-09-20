/*
 * Copyright (C) 2014 Francesco Azzola
 *  Surviving with Android (http://www.survivingwithandroid.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.sunny.up_education_board;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context mContext;
    private List<ContactInfo> contactList;
    private OnItemClickListener ItemClickListener ;

    public ContactAdapter(Context context , List<ContactInfo> contactList) {
        this.contactList = contactList;
        this.mContext = context;
    }

    public void setContactList(List<ContactInfo> contactList) {

        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
       final ContactInfo ci = contactList.get(i);
        contactViewHolder.vName.setText(ci.name);
        contactViewHolder.vSurname.setText(ci.surname);
        contactViewHolder.vEmail.setText(ci.title);
        contactViewHolder.vTitle.setText(ci.email);

        contactViewHolder.setClickListener(ItemClickListener);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.cardview, viewGroup, false);
        itemView.setOnClickListener(ItemClickListener);
            return new ContactViewHolder(itemView);
    }



    public static class ContactViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{

        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;
        private OnItemClickListener clickListener;

        public ContactViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);

            v.setTag(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        public void setClickListener(OnItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            if(clickListener != null)
            {
                clickListener.onClick(view, getLayoutPosition(),false);
                clickListener.onClick(view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getLayoutPosition() , true);
            return true;
        }
    }



    public interface OnItemClickListener extends View.OnClickListener {
        void onClick(View view, int layoutPosition, boolean b);

        @Override
        void onClick(View view);

    }


    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.ItemClickListener = mItemClickListener;
    }

}
