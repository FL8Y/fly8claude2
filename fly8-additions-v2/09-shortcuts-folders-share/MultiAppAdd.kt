package com.fly8.clone.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Batch add for several apps at once. EysClone: "Choose clones", "New instances per selected
 * app", "Choose 1–20 per app, up to 100 in one batch". FLY8's AppPicker adds ONE app per
 * request (Fly8ViewModel.addClones(app, count), 1–30) and refuses a new request while the
 * previous one runs ("Still adding clones…").
 *
 * This queues FLY8's own addClones for each selected app and waits for
 * Fly8ViewModel.creating to return to false before the next one, so FLY8's existing
 * checks (engine ready, memory, profile limits) still apply to every app.
 */
object MultiAppAdd {
    const val MAX_PER_APP = 20
    const val MAX_TOTAL = 100

    /** Returns null when the request is valid, otherwise the message to show. */
    fun validate(selection: Map<InstalledApp, Int>): String? = when {
        selection.isEmpty() -> "Choose a clone first"
        selection.values.any { it !in 1..MAX_PER_APP } -> "Choose 1–$MAX_PER_APP per app, up to $MAX_TOTAL in one batch"
        selection.values.sum() > MAX_TOTAL -> "Select no more than $MAX_TOTAL instances in one batch"
        else -> null
    }

    fun start(vm: Fly8ViewModel, selection: Map<InstalledApp, Int>): Job = vm.viewModelScope.launch {
        for ((app, count) in selection) {
            vm.creating.first { !it }          // wait for any running add to finish
            vm.addClones(app, count)
            delay(300)                          // let addClones flip `creating` to true
            vm.creating.first { !it }
        }
    }
}

/*
 * UI: in AppPickerSheet add a "Select several" toggle; keep a Map<InstalledApp, Int> with a
 * 1–20 stepper per checked app; on confirm call MultiAppAdd.validate(), then MultiAppAdd.start().
 */
