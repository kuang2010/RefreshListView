package com.kuang.refreshlistview_demo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kuang.refreshlistview_library.RefreshListView;

public class MainActivity extends AppCompatActivity {

    private int size = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RefreshListView rflv = (RefreshListView) findViewById(R.id.rflv);

        final MyAdapter myAdapter = new MyAdapter();
        rflv.setAdapter(myAdapter);

        rflv.setOnRefreshDataListener(new RefreshListView.OnRefreshDataListener() {
            @Override
            public void refreshData() {

                new Thread() {

                    @Override
                    public void run() {

                        SystemClock.sleep(2000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "刷新数据成功", Toast.LENGTH_SHORT).show();
                                rflv.updateRefreshState();
                            }
                        });

                    }
                }.start();
            }

            @Override
            public void loadMore() {

                new Thread() {

                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        size = size + 20;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "加载数据成功", Toast.LENGTH_SHORT).show();
                                rflv.updateLoadmore();
                            }
                        });

                    }
                }.start();
            }
        });
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView tv = new TextView(MainActivity.this);

            tv.setGravity(Gravity.CENTER);

            tv.setTextSize(20);

            tv.setText("hello"+position);

            return tv;
        }
    }
}
