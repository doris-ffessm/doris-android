package fr.ffessm.doris.android.activities.view;



import android.view.View;
import android.view.View.OnClickListener;

public class FoldableClickListener implements OnClickListener {

	View foldableView;
	
	public FoldableClickListener(View foldableView){
		this.foldableView = foldableView;
	}
	@Override
	public void onClick(View v) {
		if(foldableView.getVisibility() == View.GONE){
			foldableView.setVisibility(View.VISIBLE);
		}
		else{
			foldableView.setVisibility(View.GONE);
		}
		
	}

	

}
