package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.TransitionMode;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.SpyglassMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import net.minecraft.util.Identifier;

public class ZoomableItem extends Item {

    public ZoomInformation zoomInformation;

    public static class ZoomInformation {
        public float zoom_length;
        public boolean instant_zoom;
        public Identifier zoomOverlay;
        public String modifier;

        public TransitionMode getTransitionMode() {
            return instant_zoom ? new InstantTransitionMode() : new SmoothTransitionMode();
        }

        public MouseModifier getMouseModifier() {
            return switch (modifier) {
                case "cinematic_camera" -> new CinematicCameraMouseModifier();
                case "containing" -> new ContainingMouseModifier();
                case "spyglass" -> new SpyglassMouseModifier();
                case "zoom_divisor" -> new ZoomDivisorMouseModifier();
                default -> null;
            };
        }

        public ZoomOverlay getZoomOverlay() {
            return zoomOverlay != null ? new SpyglassZoomOverlay(zoomOverlay) : null;
        }
    }
}