package com.fly8.clone.data

/**
 * Named preview refresh presets (EysClone: Smooth 30 / Balanced 10 / Power saving 5 / Low 2 fps).
 * FLY8 stores a raw number AppSettings.previewFps ("a_fps2", clamped 1..60) and offers
 * resolution presets ("Balanced · 720×1280", "Sharp · 1080×1920"). This maps the
 * numeric setting to named choices without changing storage.
 */
enum class PreviewPreset(val fps: Int, val label: String, val help: String) {
    SMOOTH(30, "Smooth · up to 30 fps", "Visible cards refresh up to 30 fps. Full view stays live."),
    BALANCED(10, "Balanced · up to 10 fps", "Good default for 3–6 clones."),
    POWER_SAVING(5, "Power saving · up to 5 fps", "Less heat and battery with many clones."),
    LOW(2, "Low · up to 2 fps", "Use when memory is low or the phone gets hot.");

    companion object {
        fun of(fps: Int) = entries.minBy { kotlin.math.abs(it.fps - fps) }
    }
}

/*
 * Settings UI: replace the fps slider with radio buttons over PreviewPreset.entries and save
 *     Fly8.settings.saveApp(app.copy(previewFps = preset.fps))
 * Keep the slider under "Advanced" for custom values.
 */
