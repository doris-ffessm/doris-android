package fr.vojtisek.adm;

import java.text.SimpleDateFormat;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.DiveEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DiveEntryAdapter extends BaseAdapter  {

	private Context context;

    private List<DiveEntry> diveEntries;

	public DiveEntryAdapter(Context context, RuntimeExceptionDao<DiveEntry, Integer> diveEntriesDao) {
		super();
		this.context = context;
		// TODO find a way to query in a lazy way
		this.diveEntries = diveEntriesDao.queryForAll();
	}

	@Override
	public int getCount() {
		return diveEntries.size();
	}

	@Override
	public Object getItem(int position) {
		return diveEntries.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		DiveEntry entry = diveEntries.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.divelist_viewrow, null);
        }
        TextView tvLabel = (TextView) convertView.findViewById(R.id.label);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        tvLabel.setText(dateFormatter.format(entry.getDate()));

        TextView tvDetails = (TextView) convertView.findViewById(R.id.details);
        tvDetails.setText(entry.getLocation());

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.divelist_viewrow);
        llRow.setTag(entry);
        
        return convertView;

	}

	

}
