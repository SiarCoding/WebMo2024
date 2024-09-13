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
    private val editPlanCallback: (Plan) -> Unit,
    private val deletePlanCallback: (Int) -> Unit
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
        private val textViewWeek: TextView = itemView.findViewById(R.id.textViewWeek)

        fun bind(plan: Plan) {
            textViewWeek.text = "Woche: ${plan.week_number}" // Beispiel für die korrekte Nutzung von 'week_number'
            // Weitere Setups und Listener hier
        }
    }
}


