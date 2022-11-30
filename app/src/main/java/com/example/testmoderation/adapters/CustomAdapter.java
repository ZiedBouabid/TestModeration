package com.example.testmoderation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testmoderation.R;
import com.example.testmoderation.datamodels.PropositionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<PropositionModel> implements View.OnClickListener{

    private ArrayList<PropositionModel> dataSet;
    Context mContext;
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    // View lookup cache
    private static class ViewHolder {
        TextView proposition;
        TextView date;
        TextView result;
        LinearLayout item_parent;
    }

    public CustomAdapter(ArrayList<PropositionModel> data, Context context) {
        super(context, R.layout.proposition_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View view) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PropositionModel propositionModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.proposition_item, parent, false);
            viewHolder.proposition = (TextView) convertView.findViewById(R.id.proposition);
            viewHolder.date = (TextView) convertView.findViewById(R.id.proposition_date);
            viewHolder.result = (TextView) convertView.findViewById(R.id.proposition_result);
            viewHolder.item_parent = (LinearLayout) convertView.findViewById(R.id.item_parent);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        if(propositionModel.getResult().equals("Acceptée")){
             viewHolder.item_parent.setBackgroundColor(getContext().getResources().getColor(R.color.green_transparant));
             viewHolder.result.setTextColor(getContext().getResources().getColor(R.color.green));
        }
        else{
            viewHolder.item_parent.setBackgroundColor(getContext().getResources().getColor(R.color.red_transparant));
            viewHolder.result.setTextColor(getContext().getResources().getColor(R.color.red));
        }
        viewHolder.proposition.setText(propositionModel.getProposition());
        try {
            viewHolder.date.setText(formatter.format(parser.parse(propositionModel.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.result.setText(propositionModel.getResult());
        // Return the completed view to render on screen
        return convertView;
    }
}
