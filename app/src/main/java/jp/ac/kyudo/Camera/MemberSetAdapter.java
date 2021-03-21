package jp.ac.kyudo.Camera;


import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyudo.R;

public class MemberSetAdapter extends RecyclerView.Adapter<MemberSetAdapter.ViewHolder> {

    private List<String> dataArray = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        TextView mTextView;
        TextView numberVIew;

        ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.text_view);
            numberVIew=v.findViewById(R.id.text_view2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MemberSetAdapter(List<String> dataset) {
        dataArray = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_set_label, parent, false);

        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StringBuilder name=new StringBuilder();
        StringBuilder number=new StringBuilder();
        String ID=dataArray.get(position).replaceAll("[^0-9]", "");
        String name_original=dataArray.get(position).replaceAll(ID, "");

        for (int i=0;i<name_original.length();i++){
            char s=name_original.charAt(i);
            name.append(s);
            name.append("\n");
        }
        number.append("\n\n");
        for (int i=0;i<ID.length();i++){
            char s=ID.charAt(i);
            number.append(s);
            number.append("\n");
        }
        holder.mTextView.setText(name.toString());
        holder.numberVIew.setText(number.toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataArray.size();
    }
}