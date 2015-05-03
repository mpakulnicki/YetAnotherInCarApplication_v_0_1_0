package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import marcinpakulnicki.net.yetanotherincarapplication.R;


    public class InstalledAppsListViewUtil extends ArrayAdapter<String> {
        private final Activity context;
        private final List<String> appName;
        private final List<Drawable> appDrawable;
        public InstalledAppsListViewUtil(Activity context, List<String> appName, List<Drawable> appDrawable) {
            super(context, R.layout.installed_apps_list_view, appName);
            this.context = context;
            this.appName = appName;
            this.appDrawable = appDrawable;
        }



        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.installed_apps_list_view, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.app_name_text);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.app_icon_image);
            txtTitle.setText(appName.get(position));
            imageView.setImageDrawable(appDrawable.get(position));

            return rowView;
        }


    }

