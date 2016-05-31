package com.example.generals.fitbitmoniteringapplicationfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by generals on 05/08/2016.
 */
public class ListHistoryAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    private List<History> list;
    ButtonListenerHistory buttonListenerHistory;

    public ListHistoryAdapter(Context context,List<History> list)
    {
        this.list=list;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    public void setButtonListenerHistory(ButtonListenerHistory buttonListenerHistory) {
        this.buttonListenerHistory = buttonListenerHistory;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
     ViewHolderHistory viewHolder;
        if(view==null)
        {
            viewHolder=new ViewHolderHistory();

          view=this.inflater.inflate(R.layout.history_list_layout,viewGroup,false);
          viewHolder.maladie=(TextView)view.findViewById(R.id.maladie_name);
          viewHolder.maladieLogo=(ImageView)view.findViewById(R.id.maladie_logo);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolderHistory)view.getTag();
        }
        History hist=list.get(i);

        ImageView fleche=(ImageView) view.findViewById(R.id.fleche_logo);
        final View finalView = view;
        fleche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                buttonListenerHistory.clickedHistory(finalView, i);

            }
        });

        viewHolder.maladie.setText(hist.getMaladie());
        viewHolder.maladieLogo.setImageResource(R.mipmap.ic_heart_rate_logo);
        return view;
    }
    public interface ButtonListenerHistory
    {
        public void clickedHistory(View view,int position);
    }
}
