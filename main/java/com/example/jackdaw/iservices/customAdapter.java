package com.example.jackdaw.iservices;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.SimpleAdapter;


import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class customAdapter extends SimpleAdapter
{
    public customAdapter(Activity activity, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
    {
        super(activity, data, resource, from, to);
    }

    @Override
    public void setViewImage(ImageView v, String value)
    {
        if(value.isEmpty())
        {
            Picasso.get().load(R.drawable.placeholder).into(v);
        }
        else
        {
            Picasso.get().load(value).into(v);
        }

    }
}
