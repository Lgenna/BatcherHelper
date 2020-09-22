package personal.batcherhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class BatchViewCustomAdapter extends RecyclerView.Adapter<BatchViewCustomAdapter.CustomViewHolder> {

    Context context;
    ArrayList<BatchItem> mBatchItems;


    public BatchViewCustomAdapter(Context context, ArrayList<BatchItem> mBatchItems) {
        this.context = context;
        this.mBatchItems = mBatchItems;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_general, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
        viewHolder.batchItemCount.setText(String.valueOf(mBatchItems.get(position).getmCount()));
        viewHolder.batchItem.setText(mBatchItems.get(position).getmItem());
    }


    @Override
    public int getItemCount() {
        return mBatchItems.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView batchItemCount;
        private TextView batchItem;

        public CustomViewHolder(View itemView) {
            super(itemView);

            batchItemCount = itemView.findViewById(R.id.batchItemCount);
            batchItem = itemView.findViewById(R.id.batchItem);
        }
    }
}
