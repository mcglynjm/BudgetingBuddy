package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.budget_summary.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import java.util.*
import kotlin.collections.ArrayList


class ChartTask {
    fun execute(view: View, usersRef: DocumentReference, context: Context) : View{
        var pieDataArray = ArrayList<SliceValue>()
        var categoryTask =  usersRef.collection(Constants.CATEGORIES_COLLECTION).whereEqualTo("enabled", true)
            .get()
            .addOnSuccessListener { categories ->
                for (category in categories.documents!!) {
                    var categoryName = (category.data?.get("name") as String)
                    // var categorySum = getCategorySum(categoryName)
                    // var spent = 0.toDouble()
                    var task = usersRef.collection(Constants.TRANSACTIONS_COLLECTION)
                        .whereEqualTo("type", categoryName).get()
                    task.continueWith(Continuation<QuerySnapshot, SliceValue> { list ->
                        var spent =
                            list.result?.sumByDouble { it.getDouble("amount") ?: 0.toDouble() }
                                ?: 0.toDouble()
                        Log.d(Constants.TAG, "Spent $spent on category: $categoryName")
                        val rnd = Random()
                        return@Continuation SliceValue(
                            spent.toFloat(),
                            Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                        ).setLabel(context.getString(R.string.chart_label, categoryName, spent))
                    })
                    .addOnCompleteListener { num->
                        num.result?.let {
                            pieDataArray.add(
                                it
                            )
                            Log.d(Constants.TAG, "Pie SLices: ${pieDataArray.size}")
                            var pieData: PieChartData
                            pieData = PieChartData(pieDataArray)
                            pieData.setHasLabels(true)
                            pieData.setValueLabelsTextColor(R.color.black)
                            pieData.valueLabelTextSize = 12
                            pieData.isValueLabelBackgroundEnabled = false
                            val pieChartView: PieChartView = view.chart_view as PieChartView
                            pieChartView.pieChartData = pieData
                        }
                    }
                }

            }
        return view
    }
}