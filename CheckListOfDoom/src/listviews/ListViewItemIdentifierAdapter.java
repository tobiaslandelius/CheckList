/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listviews;

import java.util.HashMap;
import java.util.List;

import com.example.checklistofdoom.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ListViewItemIdentifierAdapter extends ArrayAdapter<ListItem> {

    HashMap<ListItem, Integer> mIdMap = new HashMap<ListItem, Integer>();
    View.OnTouchListener mTouchListener;
    List<ListItem> listItemList;
    Activity context;

    public ListViewItemIdentifierAdapter(Context context, int listItemResourceId, int textViewResourceId, 
            List<ListItem> objects, View.OnTouchListener listener) {
        super(context, listItemResourceId, textViewResourceId, objects);
        mTouchListener = listener;
        listItemList = objects;
        this.context = (Activity) context;
        
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        ListItem item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    @Override
    public void add(ListItem item) {
    	mIdMap.put(item, mIdMap.size());
    	super.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
            view.setOnTouchListener(mTouchListener);
        }
        return view;
    }

}
