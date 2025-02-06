package dev.lyg.cp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import dev.lyg.cp.R;
import dev.lyg.cp.stacklib.StackLayout;

public class NoteAdapter extends StackLayout.Adapter<NoteAdapter.CustomViewHolder> {

    private final ArrayList<Ticket> ticketList;
    private final Context context;

    public NoteAdapter(Context context, ArrayList<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new CustomViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        // 设置票务数据
        holder.trainNumber.setText(ticket.getTrainNumber());
        holder.departureDate.setText(ticket.getDepartureDate());
        holder.departureTime.setText(ticket.getDepartureTime());
        holder.arrivalTime.setText(ticket.getArrivalTime());
        holder.departureStation.setText(ticket.getDepartureStation());
        holder.arrivalStation.setText(ticket.getArrivalStation());
        holder.checkInGate.setText(ticket.getCheckInGate());
        holder.seatNumber.setText(ticket.getSeatNumber());

        holder.itemView.setOnClickListener(v -> {
            // Toast.makeText(context, "点击了 " + position, Toast.LENGTH_SHORT).show();
            // 创建 Dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("选择删除，修改");

            // 加载自定义布局
            View customView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
            builder.setView(customView);

            AlertDialog dialog = builder.create();
            dialog.show();

            // 获取布局中的按钮
            MaterialButton btnDelete = customView.findViewById(R.id.btnDelete);
            MaterialButton btnModify = customView.findViewById(R.id.btnModify);

            // 删除按钮点击事件
            btnModify.setOnClickListener(x -> {
                Toast.makeText(context, "点击修改" + position, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            // 修改按钮点击事件
            btnDelete.setOnClickListener(x -> {
                DBHelper dbHelper = new DBHelper(context);
                String id = String.valueOf(ticketList.get(position).getId());

                dbHelper.deleteData(id);
                ticketList.remove(position);
                notifyChanged();
                dialog.dismiss();
            });
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item;
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    // 自定义 ViewHolder 适配 StackLayout
    static class CustomViewHolder extends StackLayout.ViewHolder {
        TextView trainNumber, departureDate, departureTime, arrivalTime, departureStation, arrivalStation, checkInGate, seatNumber;

        public CustomViewHolder(View itemView, NoteAdapter adapter) {
            super(itemView);
            trainNumber = itemView.findViewById(R.id.tvTrainNumber_item);
            departureDate = itemView.findViewById(R.id.departure_date_item);
            departureTime = itemView.findViewById(R.id.departure_time_item);
            arrivalTime = itemView.findViewById(R.id.arrival_time_item);
            departureStation = itemView.findViewById(R.id.departure_station_item);
            arrivalStation = itemView.findViewById(R.id.arrival_station_item);
            checkInGate = itemView.findViewById(R.id.tvGateNumber_item);
            seatNumber = itemView.findViewById(R.id.tvSeatNumber_item);
        }
    }
}
