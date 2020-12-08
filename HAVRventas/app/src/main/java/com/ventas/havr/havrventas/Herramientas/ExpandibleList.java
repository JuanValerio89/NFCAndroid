package com.ventas.havr.havrventas.Herramientas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ventas.havr.havrventas.R;

import java.util.HashMap;
import java.util.List;

public class ExpandibleList extends BaseExpandableListAdapter {

    private final static String TAG = "Expandible List";
    private Context contextoA;
    private HashMap<String, List<String>> mStringListHasMap;
    private String[] mListHeaderGroup;


    public ExpandibleList(HashMap<String, List<String>> stringListHasMap, Context context) {
        contextoA = context;
        mStringListHasMap = stringListHasMap;
        mListHeaderGroup = mStringListHasMap.keySet().toArray(new String[0]);
    }

    @Override
    public int getGroupCount() {
        return mListHeaderGroup.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return 1;
        return mStringListHasMap.get(mListHeaderGroup[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListHeaderGroup[groupPosition];

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mStringListHasMap.get(mListHeaderGroup[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //Adaptador adaptador = (Adaptador) getChild(groupPosition, 0);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grup_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.txtGroupNombre);
        textView.setText(String.valueOf(getGroup(groupPosition)));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final int item_p = childPosition;
        final int group_p = groupPosition;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.txtItemNombre);
        textView.setText(String.valueOf(getChild(groupPosition, childPosition)));

        ConstraintLayout constraintLayout = convertView.findViewById(R.id.item);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group_p == 0) {

                    switch (item_p) {
                        case 0:
                            Intent i = new Intent(contextoA, LeyDeOhmDC.class);
                            contextoA.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 1:

                            break;
                    }
                }

                if (group_p == 1) {
                    switch (item_p) {
                        case 0:
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=ges.bluetooth.hyperterminal");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            contextoA.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                    }
                }

                if (group_p == 2) {
                    switch (item_p) {
                        case 0:
                            Toast.makeText(contextoA, "Proximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(contextoA, "Proximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(contextoA, "Proximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(contextoA, "Proximamente", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(contextoA, "Proximamente", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                if (group_p == 3) {
                    switch (item_p) {
                        case 0:
                            Intent i = new Intent(contextoA, CodigoDeColores.class);
                            contextoA.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 1:
                            Intent i1 = new Intent(contextoA, ResistenciaParalelo.class);
                            contextoA.startActivity(i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 2:
                            Intent i2 = new Intent(contextoA, ResistenciaSerie.class);
                            contextoA.startActivity(i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 3:
                            Intent i3 = new Intent(contextoA, DivisorTension.class);
                            contextoA.startActivity(i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return false;
    }
}