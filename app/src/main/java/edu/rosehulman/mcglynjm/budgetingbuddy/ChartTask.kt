package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.budget_summary.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView

class ChartTask {
    fun execute(view: View, usersRef: DocumentReference, context: Context) : View{
        var pieDataArray = ArrayList<SliceValue>()
        var categoryTask =  usersRef.collection(Constants.CATEGORIES_COLLECTION)
            .get()
            .addOnSuccessListener {
                for (category in it.documents!!) {
                    var categoryName = (category.data?.get("name") as String)
                    // var categorySum = getCategorySum(categoryName)
                    // var spent = 0.toDouble()
                    var task = usersRef.collection(Constants.TRANSACTIONS_COLLECTION)
                        .whereEqualTo("type", categoryName).get()
//                    task.continueWith(Continuation<QuerySnapshot, Double> {
//                        it.result?.sumByDouble { it.getDouble("amount") ?: 0.toDouble() }
//                    })
                    task.addOnSuccessListener { list ->
                        var spent = list.sumByDouble { it.getDouble("amount") ?: 0.toDouble() }
                        pieDataArray.add(
                            SliceValue(
                                spent.toFloat(),
                                R.color.green
                            ).setLabel(context.getString(R.string.chart_label, categoryName, spent))
                        )
                    }
//                     task.continueWith(Continuation<QuerySnapshot, Double> { num ->
//                         pieDataArray.add(
//                             SliceValue(
//                                 num.result.toFloat(),
//                                 R.color.green
//                             ).setLabel(context.getString(R.string.chart_label, categoryName, num.))
//                         )
//                         return@Continuation num.result as Double
//                        })
                    // var spent = task.result?.sumByDouble { it.getDouble("amount") ?: 0.toDouble() } ?: 0.toDouble()
                }
            }
        categoryTask.addOnCompleteListener {
            Log.d(Constants.TAG, "Pie SLices: ${pieDataArray.size}")
            var pieData: PieChartData
            pieData = PieChartData(pieDataArray)
            pieData.setHasLabels(true)
            pieData.valueLabelTextSize = 12
            pieData.isValueLabelBackgroundEnabled = false
            val pieChartView: PieChartView = view.chart_view as PieChartView
            pieChartView.pieChartData = pieData
        }

        return view
    }
}