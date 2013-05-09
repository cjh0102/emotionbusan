package com.hipits.emotionbusan.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.entity.entity.BaasioEntity;

public class TalksRoomListAdapter extends BaseAdapter {

	private Context context;
	private List<BaasioEntity> entities;
	private LayoutInflater inflater;

	public TalksRoomListAdapter(Context context, List<BaasioEntity> entities) {
		this.context = context;
		this.entities = entities;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return entities.size();
	}

	@Override
	public Object getItem(int position) {
		return entities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private void setStringToView(BaasioEntity entity, TextView view,
			String value) {
		view.setText(EtcUtils.getStringFromEntity(entity, value));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.listview_talksroom, null);
		}

		BaasioEntity entity = entities.get(position);

		TextView titleTextView = (TextView) view
				.findViewById(R.id.titleTextView);
		TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
		
		setStringToView(entity, titleTextView, "title");

		return view;
	}
}
