package fr.ffessm.doris.android.activities.view;



import android.view.View;
import android.view.View.OnClickListener;

public class FoldableClickListener implements OnClickListener {

	protected View associatedFoldableView;
	
	public FoldableClickListener(View foldableView){
		this.associatedFoldableView = foldableView;
	}
	@Override
	public void onClick(View v) {
		if(associatedFoldableView.getVisibility() == View.GONE){
			associatedFoldableView.setVisibility(View.VISIBLE);
		}
		else{
			associatedFoldableView.setVisibility(View.GONE);
		}
		
	}
	
	public void fold(){
		associatedFoldableView.setVisibility(View.GONE);
	}
	public void unfold(){
		associatedFoldableView.setVisibility(View.VISIBLE);
	}
	

}
