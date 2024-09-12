package com.example.webmo2024app

import com.example.webmo2024app.model.Plan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView




class PlansAdapter(
    private val plans: List<Plan>,
    private val onEdit: (Plan) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PlansAdapter.PlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.bind(plan)
    }

    override fun getItemCount(): Int = plans.size

    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(plan: Plan) {
            textViewTitle.text = itemView.context.getString(R.string.plan_for_week, plan.week)
            buttonEdit.setOnClickListener { onEdit(plan) }
            buttonDelete.setOnClickListener { onDelete(plan.week) }
        }
    }
}

