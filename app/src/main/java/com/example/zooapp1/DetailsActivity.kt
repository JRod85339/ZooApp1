package com.example.zooapp1

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

@Suppress("DEPRECATION")
class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val selectedItem = intent.getStringExtra("selectedItem") ?: ""
        val details = intent.getStringExtra("details") ?: ""

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = selectedItem
            setDisplayHomeAsUpEnabled(true)
        }
        // Set the details text
        val detailsTextView = findViewById<TextView>(R.id.textViewDetails)
        formatDetailsText(details, detailsTextView)
    }

    // Function to format the details text
    private fun formatDetailsText(details: String, textView: TextView) {
        val lines = details.split("\n")
        val formattedText = StringBuilder()

        for (line in lines) {
            if (line.trim().startsWith("*****")) {
                // Format lines starting with *****
                formattedText.append("\n")
                val formattedLine = line.replace("*****", "")
                formattedText.append(
                    "<font color='#FF0000'><big><b>$formattedLine</b></big></font><br/>"
                )
            } else {
                // Append normal lines
                formattedText.append(line).append("<br/>")
            }
        }

        textView.text = android.text.Html.fromHtml(formattedText.toString(), android.text.Html.FROM_HTML_MODE_LEGACY)
        textView.setTextColor(Color.BLACK)
    }

    // Handle back button press
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
