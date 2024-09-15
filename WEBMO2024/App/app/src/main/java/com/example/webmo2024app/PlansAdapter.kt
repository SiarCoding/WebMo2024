package com.example.webmo2024app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.webmo2024app.model.Plan

class PlansAdapter(
    private val plans: List<Plan>,
    private val editPlanCallback: (Plan) -> Unit,
    private val deletePlanCallback: (Int) -> Unit,
    private val context: Context,
    private val userRole: String?
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
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(plan: Plan) {
            textViewWeek.text = "Woche: ${plan.week_number}"

            // Überprüfe die Benutzerrolle und passe die Sichtbarkeit der Buttons an
            if (userRole == "admin") {
                buttonEdit.visibility = View.VISIBLE
                buttonDelete.visibility = View.VISIBLE
                buttonEdit.setOnClickListener { editPlanCallback(plan) }
                buttonDelete.setOnClickListener { deletePlanCallback(plan.plan_id) }
            } else {
                buttonEdit.visibility = View.GONE
                buttonDelete.visibility = View.GONE
            }
        }
    }
}

