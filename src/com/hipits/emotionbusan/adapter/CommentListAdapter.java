package com.hipits.emotionbusan.adapter;

import java.util.List;

import android.widget.TextView;
import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.entity.entity.BaasioEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommentListAdapter extends BaseAdapter {
	
	private Context context;
	private List<BaasioEntity> comments;
	private LayoutInflater inflater;
	
	public CommentListAdapter(Context context, List<BaasioEntity> comments) {
		this.context = context;
		this.comments = comments;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int index) {
		return comments.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

    private void setStringToView(BaasioEntity entity, TextView view,
                                 String value) {
        view.setText(EtcUtils.getStringFromEntity(entity, value));
    }

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.listview_comment, null);
		}

        BaasioEntity comment = comments.get(index);

        TextView bodyTextView = (TextView) view.findViewById(R.id.bodyTextView);
        TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
        
        setStringToView(comment, bodyTextView, "body");
        setStringToView(comment, idTextView, "writer_username");
        
        if (comment.getCreated() != null) {
        	String createdTime = EtcUtils.getDateString(comment.getCreated());
			timeTextView.setText(createdTime);
		}
        
        return view;
	}
}
