package com.gulei.nestedview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
    List<String> data = new ArrayList<>();
    List<RecyclerView> recyclerViews = new ArrayList<>();
    String[] tabs = new String[]{"推荐", "车展", "新闻"};
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        for (int i = 0; i < 50; i++) {
            data.add("数据" + i);
        }
        for (int i = 0; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            RvAdapter rvAdapter = new RvAdapter();
            rvAdapter.addData(data);
            recyclerView.setAdapter(rvAdapter);
            recyclerViews.add(recyclerView);
        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tabs.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(recyclerViews.get(position));
                return recyclerViews.get(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(recyclerViews.get(position));
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });

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
