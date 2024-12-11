package com.example.aufgabe3.ui.add

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }

    val context = LocalContext.current

    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO Error handling and creating new BookingEntry and save in sharedViewModel
                    if (name.isBlank()) {
                        Toast.makeText(context, "Please enter a name!", Toast.LENGTH_SHORT).show()
                    } else if (arrivalDate == null || departureDate == null) {
                        Toast.makeText(context, "Please select arrival and departure dates!", Toast.LENGTH_SHORT).show()
                    } else if (arrivalDate!!.isBefore(LocalDate.now())) {
                        Toast.makeText(context, "Arrival date cannot be in the past", Toast.LENGTH_SHORT).show()
                    } else {
                        val bookingEntry = BookingEntry(name = name, arrivalDate = arrivalDate!!, departureDate = departureDate!!)
                        sharedViewModel.addBookingEntry(bookingEntry)
                        navController.navigate("home")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }

    // TODO implement DateRangePicker Dialog logic
    if (showDateRangePicker) {
        DateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onDatesSelected = { startDate, endDate ->
                arrivalDate = startDate
                departureDate = endDate
                showDateRangePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onDatesSelected: (LocalDate, LocalDate) -> Unit
) {
    // TODO implement DateRangePicker see https://developer.android.com/develop/ui/compose/components/datepickers?hl=de
    val datePickerState = rememberDateRangePickerState()
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedStartDateMillis?.let { startDateMillis ->
                    datePickerState.selectedEndDateMillis?.let { endDateMillis ->
                        onDatesSelected(
                            Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                            Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) { Text("Cancel") }
        }
    ) {
        DateRangePicker(state = datePickerState)
    }
}