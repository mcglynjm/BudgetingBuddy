package edu.rosehulman.mcglynjm.budgetingbuddy

class ManualTransaction(var amount: Float = 0.toFloat(), var type: String = "", var items: String = "", var renews: Renews = Renews.NEVER) {
}