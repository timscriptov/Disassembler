package com.mcal.disassembler.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mcal.disassembler.data.Constants

object AdsAdmob {
    private var interstitialAd: InterstitialAd? = null

    @JvmStatic
    fun loadInterestialAd(context: Context) {
        InterstitialAd.load(context, Constants.INTERESTIAL_AD, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    interstitialAd = p0
                }
            })
    }

    @JvmStatic
    fun showInterestialAd(activity: Activity, callback: (() -> Unit)? = null) {
        if(interstitialAd != null) {
            interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    interstitialAd = null
                    callback?.invoke()
                }
            }
            interstitialAd!!.show(activity)
        } else {
            callback?.invoke()
        }
    }
}