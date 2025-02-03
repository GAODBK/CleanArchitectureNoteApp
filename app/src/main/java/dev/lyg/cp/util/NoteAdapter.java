package dev.lyg.cp.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.lyg.cp.R;
import dev.lyg.cp.unit_test.SearchFragment;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final ArrayList<Ticket> ticketList; // 使用 final 以强调不可变
    private final Context context;
    private DBHelper dbHelper;

    public NoteAdapter(Context context, ArrayList<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取当前 Ticket 对象
        Ticket ticket = ticketList.get(position);

        // 设置数据到视图
        holder.trainNumber.setText(ticket.getTrainNumber());
        holder.departureDate.setText(ticket.getDepartureDate());
        holder.departureTime.setText(ticket.getDepartureTime());
        holder.arrivalTime.setText(ticket.getArrivalTime());
        holder.departureStation.setText(ticket.getDepartureStation());
        holder.arrivalStation.setText(ticket.getArrivalStation());
        holder.checkInGate.setText(ticket.getCheckInGate());
        holder.seatNumber.setText(ticket.getSeatNumber());

        holder.btnDelete.setOnClickListener(v -> {
            int id = ticket.getId(); // 获取当前项的 ID
            Log.d("NoteAdapter", "删除 ID: " + id);

        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size(); // 返回列表大小
    }

    // 内部静态类，ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 定义视图组件
        TextView trainNumber, departureDate, departureTime, arrivalTime, departureStation, arrivalStation, checkInGate, seatNumber;
        ConstraintLayout btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化视图组件
            btnDelete = itemView.findViewById(R.id.myConstraintLayout);
            trainNumber = itemView.findViewById(R.id.tvTrainNumber);
            departureDate = itemView.findViewById(R.id.departure_date);
            departureTime = itemView.findViewById(R.id.departure_time);
            arrivalTime = itemView.findViewById(R.id.arrival_time);
            departureStation = itemView.findViewById(R.id.departure_station);
            arrivalStation = itemView.findViewById(R.id.arrival_station);
            checkInGate = itemView.findViewById(R.id.tvGateNumber);
            seatNumber = itemView.findViewById(R.id.tvSeatNumber);
        }
    }
}
