package cn.vmatrices.danmakuplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.tool.AppInfo;
import cn.vmatrices.danmakuplus.tool.AppInfoFilter;


public class AppInfoAdapter<T> extends ArrayAdapter<T> implements Filterable {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;
    private ArrayList<AppInfo> appList;
    private AppInfoFilter filter;

    @Override
    public int getCount() {
        if (appList == null) {
            return 0;
        }
        return appList.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(filter==null){
            return filter=new AppInfoFilter(this,appList);
        }
        return filter;
    }

    public AppInfoAdapter(Context context, int resource, ArrayList<AppInfo> dat) {
        super(context, resource);
        intit(context, resource, dat);
    }

    public void intit(Context context, int resource, ArrayList<AppInfo> appInfos) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResource = resource;
        this.appList = appInfos;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.appName = (TextView) convertView.findViewById(R.id.app_showname);
            holder.pkgName = (TextView) convertView.findViewById(R.id.app_pkgname);
            holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AppInfo appInfo = appList.get(position);

        holder.appName.setText(appInfo.getAppName());
        holder.pkgName.setText(appInfo.getPkgName());
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());
        holder.checkBox.setChecked(appInfo.getChecked());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appInfo.setChecked(holder.checkBox.isChecked());

            }
        });

        return convertView;
    }

    public void setAppList(ArrayList<AppInfo> appList) {
        this.appList = appList;
    }


    public final class ViewHolder {
        public TextView appName;
        public TextView pkgName;
        public ImageView appIcon;
        public CheckBox checkBox;
    }

}
