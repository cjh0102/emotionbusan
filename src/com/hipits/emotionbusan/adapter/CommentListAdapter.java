package com.hipits.emotionbusan.adapter;

import java.util.List;

import com.hipits.emotionbusan.R;
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

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.listview_comment, null);
		}
		
		return null;
	}
}
