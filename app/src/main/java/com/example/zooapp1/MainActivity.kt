package com.example.zooapp1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var listViewData: ListView
    private lateinit var data: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewData = findViewById(R.id.listViewData)
        data = mutableListOf()

        // On Animals button click, load animals data
        findViewById<TextView>(R.id.buttonAnimals).setOnClickListener {
            loadAnimalsData()
        }

        // On Habitats button click, load habitats data
        findViewById<TextView>(R.id.buttonHabitats).setOnClickListener {
            loadHabitatsData()
        }

        /*
        * On item click, fetch full details and display warning if any.
        * Then navigate to details activity with full details.
         */
        listViewData.setOnItemClickListener { parent, view, position, id ->
            // Handle item click
            val selectedItem = parent.getItemAtPosition(position) as String
            val (fullDetails, hasWarning) = fetchFullDetails(selectedItem)
            // Display full details
            if (hasWarning) {
                showWarningDialog(selectedItem, fullDetails)
            } else {
                openDetailsActivity(selectedItem, fullDetails)
            }
        }
    }

    // Load available animals from animals.txt
    private fun loadAnimalsData() {
        val animalsTextResourceId = resources.getIdentifier("animals", "raw", packageName)
        val inputStream = resources.openRawResource(animalsTextResourceId)
        val lines = inputStream.bufferedReader().readLines()

        // Clear previous data
        data.clear()

        // Find lines that start with "Animal -"
        for (line in lines) {
            if (line.startsWith("Animal -")) {
                val animalInfo = line.substringAfter("Animal - ")
                data.add(animalInfo)
            }
        }

        // Display data in ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listViewData.adapter = adapter
    }

    // Load available habitats from habitats.txt
    private fun loadHabitatsData() {
        val habitatsTextResourceId = resources.getIdentifier("habitats", "raw", packageName)
        val inputStream = resources.openRawResource(habitatsTextResourceId)
        val lines = inputStream.bufferedReader().readLines()

        // Clear previous data
        data.clear()

        // Find lines that start with "Habitat -"
        for (line in lines) {
            if (line.startsWith("Habitat -")) {
                val habitatInfo = line.substringAfter("Habitat - ")
                data.add(habitatInfo)
            }
        }

        // Display data in ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listViewData.adapter = adapter
    }

    // Fetch full details for the selected item and check for warning
    private fun fetchFullDetails(selectedItem: String): Pair<String, Boolean> {
        val animalsTextResourceId = resources.getIdentifier("animals", "raw", packageName)
        val habitatsTextResourceId = resources.getIdentifier("habitats", "raw", packageName)

        // Search for the full details in animals.txt
        val animalsInputStream = resources.openRawResource(animalsTextResourceId)
        val animalsLines = animalsInputStream.bufferedReader().readLines()
        for (line in animalsLines) {
            if (line.startsWith("Animal - ") && line.substringAfter("Animal - ") == selectedItem) {
                val details = mutableListOf<String>()
                details.add(line)
                // Add following lines until next data block starts
                var nextLineIndex = animalsLines.indexOf(line) + 1
                while (nextLineIndex < animalsLines.size && !animalsLines[nextLineIndex].startsWith("Animal - ")) {
                    details.add(animalsLines[nextLineIndex])
                    nextLineIndex++
                }
                // Check for warning lines
                val lineWithWarning = details.any { it.trim().startsWith("*****") }
                return Pair(details.joinToString("\n"), lineWithWarning)
            }
        }

        // Search for the full details in habitats.txt
        val habitatsInputStream = resources.openRawResource(habitatsTextResourceId)
        val habitatsLines = habitatsInputStream.bufferedReader().readLines()
        for (line in habitatsLines) {
            if (line.startsWith("Animal - ") && line.substringAfter("Animal - ") == selectedItem) {
                val details = mutableListOf<String>()
                details.add(line)
                // Add following lines until next data block starts
                var nextLineIndex = habitatsLines.indexOf(line) + 1
                while (nextLineIndex < habitatsLines.size && !habitatsLines[nextLineIndex].startsWith("Animal - ")) {
                    details.add(habitatsLines[nextLineIndex])
                    nextLineIndex++
                }
                // Check for warning lines
                val lineWithWarning = details.any { it.trim().startsWith("*****") }
                return Pair(details.joinToString("\n"), lineWithWarning)
            }
        }

        return Pair("", false)
    }

    // Parse warning lines and display them in a dialog
    private fun showWarningDialog(selectedItem: String, details: String) {
        val linesWithWarning = details.lines().filter { it.trim().startsWith("*****") }

        if (linesWithWarning.isNotEmpty()) {
            val lineWithWarning = linesWithWarning.first()
            val warningTitle = "Warning! " + lineWithWarning.substringAfter("*****").substringBefore(":").trim()
            val warningDescription = lineWithWarning.substringAfter(": ").trim()

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(warningTitle)
            alertDialogBuilder.setMessage(warningDescription)
            alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                openDetailsActivity(selectedItem, details)
            }
            alertDialogBuilder.show()
        } else {
            // If somehow there's no warning line despite `hasWarning`, open details directly
            openDetailsActivity(selectedItem, details)
        }
    }

    // Navigate to details activity with full details
    private fun openDetailsActivity(selectedItem: String, details: String) {
        // Start new activity to show full details
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("selectedItem", selectedItem)
        intent.putExtra("details", details)
        startActivity(intent)
    }
}
