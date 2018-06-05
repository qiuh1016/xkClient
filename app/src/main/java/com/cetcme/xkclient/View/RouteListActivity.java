package com.cetcme.xkclient.View;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.RealmModels.Message;
import com.cetcme.xkclient.Socket.SocketOrder;
import com.cetcme.xkclient.Utils.DateUtil;
import com.cetcme.xkclient.Utils.FileUtil;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RouteListActivity extends SwipeBackActivity {


    @BindView(R.id.title_view)
    QHTitleView qhTitleView;

    @BindView(R.id.sms_list)
    ListView listView;

    @BindView(R.id.pull_to_refresh)
    QMUIPullRefreshLayout mPullRefreshLayout;

    private List<Map<String, Object>> dataList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    private QMUITipDialog tipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTitleView();
        initListView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initTitleView() {
        qhTitleView.setTitle("航迹列表");
        qhTitleView.setBackView(R.mipmap.icon_back_button);
        qhTitleView.setRightView(0);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        });
    }

    private void initListView() {
        simpleAdapter = new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1,
                new String[] {"name"}, new int[] {android.R.id.text1});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new QMUIDialog.MessageDialogBuilder(RouteListActivity.this)
                        .setTitle("导出航迹数据")
                        .setMessage("确定要航迹数据吗，导出后终端上将删除数据？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "导出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                SocketOrder.getRouteDetail(RouteListActivity.this, dataList.get(i).get("navtime").toString());
                                dialog.dismiss();
                                tipDialog = new QMUITipDialog.Builder(RouteListActivity.this)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord("正在导出")
                                        .create();
                                tipDialog.show();
                            }
                        })
                        .show();
            }
        });
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                getData();
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        simpleAdapter.notifyDataSetChanged();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        SocketOrder.getRouteList(RouteListActivity.this);

        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在获取")
                .create();
        tipDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 10000);

        return dataList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "route_list":
                    mPullRefreshLayout.finishRefresh();
                    tipDialog.dismiss();

                    dataList.clear();
                    JSONArray routeList = receiveJson.getJSONArray("data");

                    for (int i = 0; i < routeList.length(); i++) {
                        JSONObject cellJson = routeList.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        Long navtime = Long.parseLong(cellJson.get("navtime").toString());
                        map.put("navtime", navtime);
                        map.put("name", DateUtil.Date2String(new Date(navtime)));
                        dataList.add(map);
                    }
                    simpleAdapter.notifyDataSetChanged();
                    break;

                case "route_detail":

                    String routeString = receiveJson.getString("data");
                    String navtime = receiveJson.getString("navtime");
                    //保存文件
                    Date date = new Date(Long.parseLong(navtime));
                    String fileName = DateUtil.Date2String(date).replace("/", "-").replace(":", "-") + ".txt";
                    FileUtil.saveFile(fileName, routeString);

                    tipDialog.dismiss();
                    new QMUIDialog.MessageDialogBuilder(RouteListActivity.this)
                            .setTitle("提示")
                            .setMessage("导出成功，已保存到 \"/0_routes/" + fileName + "\"")
                            .addAction("确定", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    getData();
                    break;
                case "socketDisconnect":
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
