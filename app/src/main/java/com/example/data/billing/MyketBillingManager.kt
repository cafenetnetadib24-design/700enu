package com.example.data.billing

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.android.vending.billing.IInAppBillingService
import com.example.data.repository.SentenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class MyketBillingManager(
    private val context: Context,
    private val repository: SentenceRepository,
    private val coroutineScope: CoroutineScope
) {

    companion object {
        private const val TAG = "MyketBillingManager"
        const val PRODUCT_ID = "premium_upgrade"
        const val PRODUCT_PRICE_TOMAN = "۲۰۰,۰۰۰ تومان"
        const val RC_BUY = 1001
    }

    private val _isPremiumUnlocked = MutableStateFlow(true)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked

    private var billingService: IInAppBillingService? = null
    private var isConnected = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Myket billing service connection bypassed")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Myket billing service disconnected")
        }
    }

    init {
        _isPremiumUnlocked.value = true
        coroutineScope.launch(Dispatchers.IO) {
            try {
                repository.setPremiumUnlocked(true)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting repository premium unlocked", e)
            }
        }
    }

    /**
     * Initializes connection to the Myket IAP Service
     */
    fun startConnection(onSuccess: () -> Unit = {}) {
        Log.d(TAG, "Connecting to Myket IAP service bypassed (app is free)")
        _isPremiumUnlocked.value = true
        onSuccess()
    }

    /**
     * Disconnect from the billing service to free up resources
     */
    fun disconnect() {
        // No-op
    }

    /**
     * Triggers the purchase flow for Myket Product Premium Upgrade
     */
    fun launchPurchaseFlow(activity: Activity, onResult: (Boolean, String) -> Unit) {
        _isPremiumUnlocked.value = true
        onResult(true, "این برنامه کاملاً رایگان است و تمام بخش‌ها برای شما فعال می‌باشند.")
    }

    /**
     * Handles Activity result passed back from MainActivity/Settings screen
     * Returns true if the result was handled by the billing manager
     */
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, onResult: (Boolean, String) -> Unit): Boolean {
        if (requestCode != RC_BUY) return false
        onResult(true, "این برنامه کاملاً رایگان است و تمام بخش‌ها برای شما فعال می‌باشند.")
        return true
    }

    /**
     * Queries purchased products offline/online to restore purchase state
     */
    fun restorePurchases(onResult: (Boolean) -> Unit) {
        _isPremiumUnlocked.value = true
        onResult(true)
    }

    /**
     * Debug feature to reset premium state (useful for testing the flow)
     */
    fun resetPremiumState(onCompleted: () -> Unit) {
        _isPremiumUnlocked.value = true
        onCompleted()
    }
}
