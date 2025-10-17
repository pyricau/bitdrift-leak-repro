package com.example.leakrepro

import android.os.Build
import android.os.Bundle
import android.view.inspector.WindowInspector
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.bitdrift.capture.Capture
import io.bitdrift.capture.providers.session.SessionStrategy
import leakcanary.LeakCanary

class MainActivity : AppCompatActivity() {

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(FrameLayout(this))

    // No window views so far.
    check(WindowInspector.getGlobalWindowViews().isEmpty())

    AlertDialog.Builder(this)
      .setTitle("This dialog is actually the first window")
      .setPositiveButton("Click here to trigger leak in 5 seconds", null)
      .show()

    // Now there is 1 window view, the dialog's window view.
    // The 2nd window view (the activity window view) will be added after onCreate()
    check(WindowInspector.getGlobalWindowViews().size == 1)

    Capture.Logger.start(
      apiKey = TODO("Paste Bitdrift API key to repro leak"),
      sessionStrategy = SessionStrategy.Fixed()
    )
  }

  companion object {
    init {
      // Trigger heap dumps as soon as any instance is retained.
      LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 1)
    }
  }
}