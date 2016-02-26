# RefreshListView
上拉刷新，下拉加载
实现接口：
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
