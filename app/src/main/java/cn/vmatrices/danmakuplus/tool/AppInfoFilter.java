package cn.vmatrices.danmakuplus.tool;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import cn.vmatrices.danmakuplus.adapter.AppInfoAdapter;

/**
 * Created by iBelieve on 2017/3/11.
 */

public class AppInfoFilter extends Filter {
    private AppInfoAdapter adapter;
    private ArrayList<AppInfo> appList;
    private CharSequence text = "";

    public AppInfoFilter(AppInfoAdapter adapter, ArrayList<AppInfo> appList) {
        super();
        this.adapter = adapter;
        this.appList = appList;
    }

    public List<AppInfo> getAppList() {
        return this.appList;
    }

    public void setAppList(ArrayList<AppInfo> newList) {
        this.appList = newList;
    }

    @Override
    protected FilterResults performFiltering(final CharSequence constraint) {
        text = constraint;
        List<AppInfo> items = new ArrayList<>();
        items.clear();

        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            String str=constraint.toString().toUpperCase();
            for (int i = 0; i < appList.size(); i++) {
                AppInfo appInfo = appList.get(i);
                if (appInfo.getAppName().toUpperCase().contains(str)) {
                    items.add(appInfo);
                }
            }
            results.values = items;
            results.count = items.size();
            return results;
        } else {
            results.values = appList;
            results.count = appList.size();
            return results;
        }
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (adapter != null) {
            adapter.setAppList((ArrayList<AppInfo>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public void reFilter() {
        filter(text);
    }
}
