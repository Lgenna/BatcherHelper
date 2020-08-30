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
        if (mBatchItems.get(position).getmIsQC()) {
            // don't draw the button when its qc, the user should not be able to remove qc items, only the system
//                    ContextCompat.getDrawable(getContext(), R.drawable.custom_button);
            viewHolder.removeItem.setVisibility(View.INVISIBLE);
            viewHolder.removeItem.setEnabled(false);
        }
        viewHolder.removeItem.setOnClickListener(v -> {

            System.out.println("removing position :" + position);

            notifyItemRemoved(position);


            mBatchItems.remove(position);


            notifyItemRangeChanged(position, getItemCount());

            try {
//                Objects.requireNonNull(getActivity()).runOnUiThread(() -> getBatchSize());
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e);
            }
//                    notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
                return mBatchItems.size();
            }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView batchItemCount;
        private TextView batchItem;
        private ImageButton removeItem;

        public CustomViewHolder(View itemView) {
            super(itemView);

            batchItemCount = itemView.findViewById(R.id.batchItemCount);
            batchItem = itemView.findViewById(R.id.batchItem);
            removeItem = itemView.findViewById(R.id.remove_item);
        }
    }
}
