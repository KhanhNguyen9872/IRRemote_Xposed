package com.oplus.os;

import android.content.Context;


// No-op compatibility stub for the proprietary ColorOS haptic API.

public class LinearmotorVibrator {

    public LinearmotorVibrator() {
    }

    public LinearmotorVibrator(
            Context context,
            ILinearmotorVibratorService service
    ) {
    }

    public void vibrate(WaveformEffect effect) {
        // Intentionally no-op on non-ColorOS ROMs.
    }
}