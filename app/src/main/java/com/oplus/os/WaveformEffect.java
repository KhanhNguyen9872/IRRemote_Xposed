package com.oplus.os;


// Minimal compatibility model for ColorOS waveform effects.

public class WaveformEffect {

    private int effectType;
    private boolean effectLoop;
    private boolean strengthSettingEnabled;

    public WaveformEffect() {
    }

    public int getEffectType() {
        return effectType;
    }

    public boolean getEffectLoop() {
        return effectLoop;
    }

    public boolean getStrengthSettingEnabled() {
        return strengthSettingEnabled;
    }

    public static class Builder {

        private int effectType;
        private boolean effectLoop;
        private boolean strengthSettingEnabled;

        public Builder() {
        }

        public Builder(WaveformEffect effect) {
            if (effect != null) {
                effectType = effect.getEffectType();
                effectLoop = effect.getEffectLoop();
                strengthSettingEnabled =
                        effect.getStrengthSettingEnabled();
            }
        }

        public Builder setEffectType(int effectType) {
            this.effectType = effectType;
            return this;
        }

        public Builder setEffectLoop(boolean effectLoop) {
            this.effectLoop = effectLoop;
            return this;
        }

        public Builder setStrengthSettingEnabled(
                boolean enabled
        ) {
            strengthSettingEnabled = enabled;
            return this;
        }

        public WaveformEffect build() {
            WaveformEffect effect = new WaveformEffect();

            effect.effectType = effectType;
            effect.effectLoop = effectLoop;
            effect.strengthSettingEnabled =
                    strengthSettingEnabled;

            return effect;
        }
    }
}