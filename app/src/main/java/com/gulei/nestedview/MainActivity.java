package com.gulei.nestedview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 50; i++) {
            data.add("数据" + i);
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RvAdapter rvAdapter = new RvAdapter();
        rvAdapter.addData(data);
        recyclerView.setAdapter(rvAdapter);
    }


    public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvViewHolder> {

        List<String> mData;

        @NonNull
        @Override
        public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
            return new RvViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
            holder.textView.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        public void addData(List<String> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        public class RvViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public RvViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }

    }


}
